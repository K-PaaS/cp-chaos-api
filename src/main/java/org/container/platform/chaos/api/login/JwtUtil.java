package org.container.platform.chaos.api.login;

import io.jsonwebtoken.*;
import org.container.platform.chaos.api.accessInfo.AccessTokenService;
import org.container.platform.chaos.api.common.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.login.support.JWTRoleInfoItem;
import org.container.platform.chaos.api.login.support.PortalGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * JwtUtil 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Service
public class JwtUtil {

    private byte[] secret;
    public static int jwtExpirationInMs;
    public static int refreshExpirationDateInMs;


    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private CommonService commonService;

    @Value("${jwt.secret}")
    public void setSecret(String secretVal) {
        this.secret = secretVal.getBytes();
    }

    @Value("${jwt.expirationDateInMs}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    @Value("${jwt.refreshExpirationDateInMs}")
    public void setRefreshExpirationDateInMs(int refreshExpirationDateInMs) {
        this.refreshExpirationDateInMs = refreshExpirationDateInMs;
    }


    /**
     * 토큰 유효성 검사(Validation token value)
     *
     * @param authToken the auth token
     * @return the boolean
     */
    public boolean validateToken(String authToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException(MessageConstant.LOGIN_INVALID_CREDENTIALS.getMsg(), ex);
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), MessageConstant.LOGIN_TOKEN_EXPIRED.getMsg(), ex);
        }
    }


    /**
     * 토큰을 통한 사용자 이름 조회(Get User name from token)
     *
     * @param token the token
     * @return the string
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        return claims.getSubject();
    }


    /**
     * 토큰을 통한 Vault 권한 조회(Get Roles from token)
     *
     * @param authToken the auth token
     * @return the list
     */
    @TrackExecutionTime
    public List<GrantedAuthority> getPortalRolesFromToken(String authToken) {
        List<GrantedAuthority> roles = new ArrayList<>();
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
        Map<String, Object> rolesInfo = claims.get("rolesInfo", Map.class);
        String userType = claims.get("userType", String.class);
        String userAuthId = claims.get("userAuthId", String.class);


        roles.add(new SimpleGrantedAuthority(userType)); //Default role
        rolesInfo.keySet().forEach(clusterId -> {
            JWTRoleInfoItem item = commonService.setResultObject(rolesInfo.get(clusterId), JWTRoleInfoItem.class);
            if(item.getUserType().equals(Constants.AUTH_USER)){ //USER Type의 경우
                roles.add(new PortalGrantedAuthority(clusterId, Constants.ContextType.CLUSTER.name(), item.getUserType()));
                item.getNamespaceList().forEach(namespaceName -> {
                    try {
                        Params vaultResult = accessTokenService.getVaultSecrets(new Params(clusterId, userAuthId, item.getUserType(), namespaceName));
                        roles.add(new PortalGrantedAuthority(namespaceName, Constants.ContextType.NAMESPACE.name(),
                                item.getUserType(), vaultResult.getClusterToken(), vaultResult.getClusterApiUrl(), clusterId));
                    } catch (Exception e) {
                    }
                });
            }
            else { //ADMIN인 경우
                try {
                    Params vaultResult = accessTokenService.getVaultSecrets(new Params(clusterId, userAuthId, item.getUserType(), Constants.EMPTY_STRING));
                    roles.add(new PortalGrantedAuthority(clusterId, Constants.ContextType.CLUSTER.name(),
                            item.getUserType(), vaultResult.getClusterToken(), vaultResult.getClusterApiUrl()));
                } catch (Exception e) {
                    //pass
                }
            }
        });

        return roles;
    }


    /**
     * 토큰을 통한 클라이언트 IP 조회(Get Client IP from token)
     *
     * @param authToken the auth token
     * @return the string
     */
    public String getClientIpFromToken(String authToken) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
        String clientIp = String.valueOf(claims.get("IP"));

        return clientIp;
    }


    /**
     * API 요청으로부터 JWT 토큰 추출(Extract JWT token from API Request)
     *
     * @param request the request
     * @return the string
     */
    public String extractJwtFromRequest(HttpServletRequest request) {
        RequestWrapper requestWrapper = new RequestWrapper(request);

        String bearerToken = requestWrapper.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }


    /**
     * Refresh JWT 허가(Allow Refresh JWT token)
     *
     * @param ex      the ExpiredJwtException
     * @param request the HttpServletRequest
     * @return the string
     */
    public void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {

        // create a UsernamePasswordAuthenticationToken with null values.
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // Set the claims so that in controller we will be using it to create
        // new JWT
        request.setAttribute("claims", ex.getClaims());
    }


    /**
     * 토큰을 통한 UserType 조회(Get UserType from token)
     *
     * @param authToken the auth token
     * @return the list
     */
    public String getClaimsFromToken(String authToken, String name) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
        return claims.get(name, String.class);
    }

}