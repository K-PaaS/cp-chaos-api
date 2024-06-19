package org.container.platform.chaos.api.experiments;

import org.container.platform.chaos.api.common.Constants;
import org.container.platform.chaos.api.common.PropertyService;
import org.container.platform.chaos.api.common.RestTemplateService;
import org.container.platform.chaos.api.common.TemplateService;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.container.platform.chaos.api.networkFaults.NetworkFaultsService;
import org.container.platform.chaos.api.podFaults.PodFaultsService;
import org.container.platform.chaos.api.stressScenarios.StressScenariosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentsService.class);

    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final TemplateService templateService;
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
    public ExperimentsService(RestTemplateService restTemplateService, PropertyService propertyService, TemplateService templateService,
                              NetworkFaultsService networkFaultsService, PodFaultsService podFaultsService, StressScenariosService stressScenariosService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
        this.templateService = templateService;
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

      /*  HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getCpChaosApiListPodFaultsPodKillListUrl(), HttpMethod.GET, null, Map.class, params);
*/
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
        Map map = new HashMap();
        map.put("kind", params.getKind());
        map.put("name", params.getName());
        map.put("namespace", params.getNamespace());
        map.put("namespaces", params.getNamespaces());

        String commonYaml = templateService.convert("create_chaosMesh_common.ftl", map);

        if (params.getKind().equals(Constants.CHAOS_MESH_POD_CHAOS)) {

        } else if (params.getKind().equals(Constants.CHAOS_MESH_NETWORK_CHAOS)) {

        } else {

        }



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
