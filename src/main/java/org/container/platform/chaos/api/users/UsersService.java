package org.container.platform.chaos.api.users;

import org.container.platform.chaos.api.common.*;
import org.container.platform.chaos.api.common.model.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

import static org.container.platform.chaos.api.common.Constants.*;

/**
 * User Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.25
 **/
@Service
public class UsersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;

    /**
     * Instantiates a new Users service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     */
    @Autowired
    public UsersService(RestTemplateService restTemplateService, CommonService commonService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
    }

    /**
     * User 과 맵핑된 클러스터 목록 조회
     *
     * @param params the params
     * @return the UsersList
     */
    public UsersList getMappingClustersListByUser(Params params) {
        UsersList usersList = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_LIST_BY_USER
                        .replace("{userAuthId:.+}", params.getUserAuthId()).replace("{userType:.+}", params.getUserType()),
                HttpMethod.GET, null, UsersList.class, params);

        if (params.getIsGlobal()) {
            // global 화면의 경우 SUPER-ADMIN, CLUSTER-ADMIN 권한과 맵핑된 클러스터 목록 반환
            List<Users> items = usersList.getItems().stream().filter(x -> !x.getUserType().equals(AUTH_USER)).collect(Collectors.toList());
            usersList.setItems(items);
        }
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * User 과 맵핑된 클러스터 & 네임스페이스 목록 조회
     *
     * @param params the params
     * @return the UsersList
     */
    public UsersList getMappingClustersAndNamespacesListByUser(Params params) {
        UsersList usersList = restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CLUSTER_AND_NAMESPACE_LIST_BY_USER
                        .replace("{userAuthId:.+}", params.getUserAuthId()).replace("{userType:.+}", params.getUserType()),
                HttpMethod.GET, null, UsersList.class, params);
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }



}