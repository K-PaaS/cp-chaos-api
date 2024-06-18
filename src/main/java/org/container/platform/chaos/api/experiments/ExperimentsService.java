package org.container.platform.chaos.api.experiments;

import org.container.platform.chaos.api.common.Constants;
import org.container.platform.chaos.api.common.PropertyService;
import org.container.platform.chaos.api.common.RestTemplateService;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.container.platform.chaos.api.networkFaults.NetworkFaultsService;
import org.container.platform.chaos.api.podFaults.PodFaultsService;
import org.container.platform.chaos.api.stressScenarios.StressScenariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Experiments Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.06.04
 */
@Service
public class ExperimentsService {

    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final NetworkFaultsService networkFaultsService;
    private final PodFaultsService podFaultsService;
    private final StressScenariosService stressScenariosService;


    /**
     * Instantiates a new Experiments service
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     */
    @Autowired
    public ExperimentsService(RestTemplateService restTemplateService, PropertyService propertyService, NetworkFaultsService networkFaultsService,
                              PodFaultsService podFaultsService, StressScenariosService stressScenariosService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
        this.networkFaultsService = networkFaultsService;
        this.podFaultsService = podFaultsService;
        this.stressScenariosService = stressScenariosService;
    }

    /**
     * ExperimentsList 목록 조회 (Get Experiments List)
     *
     * @param params the params
     * @return the ExperimentsList
     */
   public ExperimentsList getExperimentsList(Params params) {

        HashMap responseMapPodFault = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getCpChaosApiListPodFaultsPodKillListUrl(), HttpMethod.GET, null, Map.class, params);

       HashMap responseMapNetworkDelay = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
               propertyService.getCpChaosApiListNetworkFaultsDelayListUrl(), HttpMethod.GET, null, Map.class, params);

       HashMap responseMapStress = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
               propertyService.getCpChaosApiListStressScenariosListUrl(), HttpMethod.GET, null, Map.class, params);


        return null;
    }

    /**
     * Experiments 상세 조회(Get Experiments Detail)
     *
     * @param params the params
     * @return the experiments detail
     */
    public Experiments getExperiments(Params params) {
       return null;
    }

    /**
     * Experiments 생성(Create Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    public Object createExperiments(Params params) {
       return null;
    }

    /**
     * Experiments 삭제(Delete Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteExperiments(Params params) {
       return null;
    }
}
