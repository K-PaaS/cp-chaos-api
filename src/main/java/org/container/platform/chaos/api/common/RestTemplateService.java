package org.container.platform.chaos.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.container.platform.chaos.api.clusters.clusters.Clusters;
import org.container.platform.chaos.api.common.model.CommonStatusCode;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.container.platform.chaos.api.exception.CommonStatusCodeException;
import org.container.platform.chaos.api.login.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.container.platform.chaos.api.common.Constants.TARGET_CHAOS_COLLECTOR_API;
import static org.container.platform.chaos.api.common.Constants.TARGET_COMMON_API;

/**
 * Rest Template Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Service
public class RestTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateService.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private final String commonApiBase64Authorization;
    private final String chaosCollectorApiBase64Authorization;
    private final RestTemplate restTemplate;
    private final RestTemplate shortRestTemplate;
    protected final PropertyService propertyService;
    private final CommonService commonService;
    protected final VaultService vaultService;
    protected String base64Authorization;

    protected String baseUrl;


    /**
     * Instantiates a new Rest template service
     *
     * @param restTemplate    the rest template
     * @param propertyService the property service
     */
    @Autowired
    public RestTemplateService(RestTemplate restTemplate,
                               @Qualifier("shortTimeoutRestTemplate") RestTemplate shortRestTemplate,
                               PropertyService propertyService,
                               CommonService commonService,
                               VaultService vaultService,
                               @Value("${commonApi.authorization.id}") String commonApiAuthorizationId,
                               @Value("${commonApi.authorization.password}") String commonApiAuthorizationPassword,
                               @Value("${chaosCollectorApi.authorization.id}") String chaosCollectorApiAuthorizationId,
                               @Value("${chaosCollectorApi.authorization.password}") String chaosCollectorApiAuthorizationPassword
    ) {
        this.restTemplate = restTemplate;
        this.shortRestTemplate = shortRestTemplate;
        this.propertyService = propertyService;
        this.commonService = commonService;
        this.vaultService = vaultService;
        this.commonApiBase64Authorization = "Basic "
                + Base64.getEncoder().encodeToString(
                (commonApiAuthorizationId + ":" + commonApiAuthorizationPassword).getBytes(StandardCharsets.UTF_8));
        this.chaosCollectorApiBase64Authorization = "Basic "
                + Base64.getEncoder().encodeToString(
                (chaosCollectorApiAuthorizationId + ":" + chaosCollectorApiAuthorizationPassword).getBytes(StandardCharsets.UTF_8));
    }


    /**
     * t 전송(Send t)
     * <p>
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */


    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, Params params) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE, params);
    }

    @TrackExecutionTime
    public <T> T sendGlobal(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, Params params) {
        return send(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE, params);
    }

    public <T> T sendYaml(String reqApi, String reqUrl, HttpMethod httpMethod, Class<T> responseType, Params params) {
        return sendAdmin(reqApi, reqUrl, httpMethod, params.getYaml(), responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml", params);

    }

    public <T> T sendDryRun(String reqApi, String reqUrl, HttpMethod httpMethod, String yaml, Class<T> responseType, Params params) {
        return sendAdmin(reqApi, reqUrl + "?dryRun=All", httpMethod, yaml, responseType, Constants.ACCEPT_TYPE_JSON, "application/yaml", params);

    }

    /**
     * t 전송(Send t)
     * <p>
     * (Admin)
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @param contentType  the content type
     * @return the t
     */
    public <T> T sendAdmin(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType, Params params) {
        reqUrl = setRequestParameter(reqApi, reqUrl, httpMethod, params);
        setApiUrlAuthorization(reqApi, params);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, contentType);
        reqHeaders.add("ACCEPT", acceptType);

        HttpEntity<Object> reqEntity;
        if (bodyObject == null) {
            reqEntity = new HttpEntity<>(reqHeaders);
        } else {
            reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        }

        LOGGER.info("<T> T SEND :: REQUEST: {} BASE-URL: {}, CONTENT-TYPE: {}", CommonUtils.loggerReplace(httpMethod), CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(reqHeaders.get(CONTENT_TYPE)));

        ResponseEntity<T> resEntity = null;
        try {
            resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(exception.getStatusCode()), CommonUtils.loggerReplace(exception.getMessage()));
            throw new CommonStatusCodeException(Integer.toString(exception.getStatusCode().value()));
        }

        if (resEntity.getBody() != null) {
            LOGGER.info("RESPONSE-TYPE: {}", CommonUtils.loggerReplace(resEntity.getBody().getClass()));
            return statusCodeDiscriminate(reqApi, resEntity, httpMethod);

        } else {
            LOGGER.error("RESPONSE-TYPE: RESPONSE BODY IS NULL");
        }

        return resEntity.getBody();
    }


    /**
     * t 전송(Send t)
     * <p></p>
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @param contentType  the content type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType, Params params) {
        reqUrl = setRequestParameter(reqApi, reqUrl, httpMethod, params);
        setApiUrlAuthorization(reqApi, params);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, contentType);
        reqHeaders.add("ACCEPT", acceptType);

        HttpEntity<Object> reqEntity;
        if (bodyObject == null) {
            reqEntity = new HttpEntity<>(reqHeaders);
        } else {
            reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        }

        LOGGER.info("<T> T SEND :: REQUEST: {} BASE-URL: {}, CONTENT-TYPE: {}", CommonUtils.loggerReplace(httpMethod), CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(reqHeaders.get(CONTENT_TYPE)));

        ResponseEntity<T> resEntity = null;

        try {
            resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(exception.getStatusCode()), CommonUtils.loggerReplace(exception.getMessage()));
            throw new CommonStatusCodeException(Integer.toString(exception.getStatusCode().value()));
        }

        if (resEntity.getBody() == null) {
            LOGGER.error("RESPONSE-TYPE: RESPONSE BODY IS NULL");
        }

        return resEntity.getBody();
    }


    /**
     * t 전송(Send t)
     * <p></p>
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param acceptType   the accept type
     * @param contentType  the content type
     * @return the t
     */
    public <T> T sendPing(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, String acceptType, String contentType, Params params) {
        reqUrl = setRequestParameter(reqApi, reqUrl, httpMethod, params); // TODO 중복 코드 제거 필요.
        setApiUrlAuthorization(reqApi, params);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, contentType);
        reqHeaders.add("ACCEPT", acceptType);

        HttpEntity<Object> reqEntity;
        if (bodyObject == null) {
            reqEntity = new HttpEntity<>(reqHeaders);
        } else {
            reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        }

        LOGGER.info("<T> T SEND :: REQUEST: {} BASE-URL: {}, CONTENT-TYPE: {}", CommonUtils.loggerReplace(httpMethod), CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(reqHeaders.get(CONTENT_TYPE)));

        ResponseEntity<T> resEntity = null;

        try {
            resEntity = shortRestTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
        } catch (HttpStatusCodeException exception) {
            LOGGER.info("HttpStatusCodeException API Call URL : {}, errorCode : {}, errorMessage : {}", CommonUtils.loggerReplace(reqUrl), CommonUtils.loggerReplace(exception.getStatusCode()), CommonUtils.loggerReplace(exception.getMessage()));
            throw new CommonStatusCodeException(Integer.toString(exception.getStatusCode().value()));
        }

        if (resEntity.getBody() == null) {
            LOGGER.error("RESPONSE-TYPE: RESPONSE BODY IS NULL");
        }

        return resEntity.getBody();
    }


    /**
     * 생성, 갱신, 삭제 로직의 코드 식별(Create/Update/Delete logic's status code discriminate)
     *
     * @param reqApi     the reqApi
     * @param res        the response
     * @param httpMethod the http method
     * @return the t
     */
    public <T> T statusCodeDiscriminate(String reqApi, ResponseEntity<T> res, HttpMethod httpMethod) {
        // 200, 201, 202일때 결과 코드 동일하게(Same Result Code = 200, 201, 202)
        Integer[] RESULT_STATUS_SUCCESS_CODE = {200, 201, 202};
        ResultStatus resultStatus;

        List<Integer> intList = new ArrayList<>(RESULT_STATUS_SUCCESS_CODE.length);
        for (int i : RESULT_STATUS_SUCCESS_CODE) {
            intList.add(i);
        }

        // Rest 호출 시 에러가 났지만 에러 메세지를 보여주기 위해 200 OK로 리턴된 경우 (Common API Error Object)
        if (TARGET_COMMON_API.equals(reqApi)) {
            ObjectMapper oMapper = new ObjectMapper();
            Map map = oMapper.convertValue(res.getBody(), Map.class);

            if (Constants.RESULT_STATUS_FAIL.equals(map.get("resultCode"))) {
                resultStatus = new ResultStatus(Constants.RESULT_STATUS_FAIL, map.get("resultMessage").toString(), CommonStatusCode.INTERNAL_SERVER_ERROR.getCode(), CommonStatusCode.INTERNAL_SERVER_ERROR.getMsg());
                return (T) resultStatus;
            }
        }


        if (httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.POST || httpMethod == HttpMethod.DELETE) {
            if (Arrays.asList(RESULT_STATUS_SUCCESS_CODE).contains(res.getStatusCode().value())) {
                resultStatus = new ResultStatus(Constants.RESULT_STATUS_SUCCESS, res.getStatusCode().toString(), CommonStatusCode.OK.getCode(), CommonStatusCode.OK.getMsg());
                return (T) resultStatus;
            }
        }

        return res.getBody();
    }


    /**
     * Authorization 값을 입력(Set the authorization value)
     *
     * @param reqApi the reqApi
     */
    protected void setApiUrlAuthorization(String reqApi, Params params) {
        String apiUrl = "";
        String authorization = "";

        // K8S MASTER API
        if (Constants.TARGET_CP_MASTER_API.equals(reqApi)) {
            Clusters clusters = (params.getIsClusterToken()) ? vaultService.getClusterDetails(params.getCluster()) : commonService.getKubernetesInfo(params);
            Assert.notNull(clusters, "Invalid parameter");
            apiUrl = clusters.getClusterApiUrl();
            authorization = "Bearer " + clusters.getClusterToken();
        }

        // COMMON API
        if (TARGET_COMMON_API.equals(reqApi)) {
            apiUrl = propertyService.getCommonApiUrl();
            authorization = commonApiBase64Authorization;
        }

        // CHAOS COLLECTOR API
        if (TARGET_CHAOS_COLLECTOR_API.equals(reqApi)) {
            apiUrl = propertyService.getChaosCollectorApiUrl();
            authorization = chaosCollectorApiBase64Authorization;
        }

        // Chaos Dashboard API
        if (Constants.TARGET_CHAOS_API.equals(reqApi)) {
            Clusters clusters = (params.getIsClusterToken()) ? vaultService.getClusterDetails(params.getCluster()) : commonService.getKubernetesInfo(params);
            Assert.notNull(clusters, "Invalid parameter");
            authorization = "Bearer " + clusters.getClusterToken();
            apiUrl = propertyService.getCpChaosApiUrl();
        }
        this.base64Authorization = authorization;
        this.baseUrl = apiUrl;
    }


    /**
     * Request URL 파라미터 설정 (Set Request URL Parameters)
     *
     * @param reqApi the reqApi
     */
    public String setRequestParameter(String reqApi, String reqUrl, HttpMethod httpMethod, Params params) {

        if (reqApi.equals(Constants.TARGET_CP_MASTER_API)) {
            if (httpMethod.equals(HttpMethod.GET) && params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
                reqUrl = reqUrl.replace("namespaces/{namespace}/", "");
            }
            reqUrl = reqUrl.replace("{namespace}", params.getNamespace()).replace("{name}", params.getResourceName()).replace("{chaosname}", params.getName()).replace("{chaosnamespace}", params.getChaosNamespace());
        }

        if (reqApi.equals(Constants.TARGET_CHAOS_API)) {
            reqUrl = reqUrl.replace("{uid}", params.getUid());
        }
        return reqUrl;
    }


    public String getLang() {
        CustomUserDetails customUserDetails = null;
        String u_lang = "";
        try {
            customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
            u_lang = customUserDetails.getULang();
        } catch (Exception e) {
            u_lang = Constants.U_LANG_ENG;
        }
        return u_lang;
    }
}