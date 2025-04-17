package org.container.platform.chaos.api.login;

import org.container.platform.chaos.api.common.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

import static org.container.platform.chaos.api.common.Constants.TARGET_COMMON_API;

/**
 * Custom User Details Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.09.28
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final RestTemplateService restTemplateService;

    /**
     * Instantiates a new CustomUserDetails service
     *
     * @param propertyService the property service
     * @param usersService    the users service
     */
    @Autowired
    public CustomUserDetailsService(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }


    /**
     * 로그인 인증을 위한 User 상세 조회(Get Users detail for login authentication)
     *
     * @param userId the user id
     * @return the user details
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles = null;
        Users user = getUsersDetailsForLogin(userId);
        if (user != null) {
            roles = List.of(new SimpleGrantedAuthority(user.getUserType()));
            return new User(user.getUserId(), user.getUserAuthId(), roles);
        }
        throw new UsernameNotFoundException(MessageConstant.INVALID_LOGIN_INFO.getMsg());
    }

    /**
     * Users 로그인을 위한 상세 조회(Get Users for login)
     *
     * @param userId the userId
     * @return the users detail
     */
    public Users getUsersDetailsForLogin(String userId) {
        return restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_DETAIL_LOGIN.replace("{userId:.+}", userId)
                , HttpMethod.GET, null, Users.class, new Params());
    }

}