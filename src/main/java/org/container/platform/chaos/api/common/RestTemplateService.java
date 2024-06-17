package org.container.platform.chaos.api.common;

import org.container.platform.chaos.api.common.model.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Rest Template Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.06.07
 */

@Service
public class RestTemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateService.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";

    /*public Object send(String targetChaosApi, String cpChaosApiListPodFaultsPodKillListUrl, HttpMethod httpMethod, Object o, Class<Map> mapClass, Params params) {
    }*/


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


   /* public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, Params params) {
        return sendAdmin(reqApi, reqUrl, httpMethod, bodyObject, responseType, Constants.ACCEPT_TYPE_JSON, MediaType.APPLICATION_JSON_VALUE, params);
    }*/

}
