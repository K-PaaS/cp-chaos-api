package org.container.platform.chaos.api.experiments;

import org.container.platform.chaos.api.common.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
/*import org.container.platform.chaos.api.networkFaults.NetworkFaultsService;
import org.container.platform.chaos.api.podFaults.PodFaultsService;
import org.container.platform.chaos.api.stressScenarios.StressScenariosService;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Experiments Service 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.06.21
 */
@Service
public class ExperimentsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentsService.class);
    private final CommonService commonService;

    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final TemplateService templateService;
/*    private final NetworkFaultsService networkFaultsService;
    private final PodFaultsService podFaultsService;
    private final StressScenariosService stressScenariosService;*/


    /**
     * Instantiates a new Experiments service
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     */
    @Autowired
    public ExperimentsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, TemplateService templateService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.templateService = templateService;
/*        this.networkFaultsService = networkFaultsService;
        this.podFaultsService = podFaultsService;
        this.stressScenariosService = stressScenariosService;*/
    }

    /**
     * ExperimentsList 목록 조회 (Get Experiments List)
     *
     * @param params the params
     * @return the ExperimentsList
     */
    public ExperimentsList getExperimentsList(Params params) {
        ExperimentsList experimentsList = new ExperimentsList();

        HashMap responseMapPodFault = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getCpChaosApiListPodFaultsPodKillListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList podFaultList = commonService.setResultObject(responseMapPodFault, ExperimentsList.class);
        experimentsList.setItems(podFaultList.getItems());
        System.out.println("experimentsList pod\n" + experimentsList);

        HashMap responseMapNetworkDelay = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getCpChaosApiListNetworkFaultsDelayListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList networkDelayList = commonService.setResultObject(responseMapNetworkDelay, ExperimentsList.class);
        experimentsList.getItems().addAll(networkDelayList.getItems());

        System.out.println("experimentsList network\n" + experimentsList);


        HashMap responseMapStress = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getCpChaosApiListStressScenariosListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList stressList = commonService.setResultObject(responseMapStress, ExperimentsList.class);
        experimentsList.getItems().addAll(stressList.getItems());


        System.out.println("응답값을 ExperimentsList에 넣기 파드\n" + podFaultList);
        System.out.println("응답값을 ExperimentsList에 넣기 네트워크\n" + networkDelayList);
        System.out.println("응답값을 ExperimentsList에 넣기 스트레스\n" + stressList);
        System.out.println("experimentsList\n" + experimentsList);

        experimentsList = commonService.resourceListProcessing(experimentsList, params, ExperimentsList.class);


        return (ExperimentsList) commonService.setResultModel(experimentsList, Constants.RESULT_STATUS_SUCCESS);
    };





    /**
     * Experiments 상세 조회(Get Experiments Detail)
     *
     * @param params the params
     * @return the experiments detail
     */
/*
    public Experiments getExperiments(Params params) {
        return null;
    }
*/

    /**
     * Experiments 생성(Create Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
/*
    public Object createExperiments(Params params) {
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();

        Map map = new HashMap();
        map.put("kind", params.getKind());
        map.put("name", params.getName());
        map.put("namespace", params.getNamespace());
        map.put("action", params.getAction());
        map.put("gracePeriod", params.getGracePeriod());
        map.put("duration", params.getDuration());
        map.put("latency", params.getLatency());

        stringBuilder.append(templateService.convert("create_chaosMesh_common.ftl", map));
        stringBuilder.append(Constants.NEW_LINE);
        stringBuilder.append(Constants.CHAOS_MESH_NAMESPACES);
        for (int i=0; i < params.getNamespaces().size(); i++ ) {
            line = "      - " + params.getNamespaces().get(i) + Constants.NEW_LINE;
            stringBuilder.append(line);
        }

        stringBuilder.append(Constants.CHAOS_MESH_LABEL_SELECTOR);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> mapLabelSelector = objectMapper.convertValue(params.getLabelSelectors(), Map.class);

        for (Map.Entry<String, String> entry : mapLabelSelector.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            line = "      " + key + ": " + value + Constants.NEW_LINE;
            stringBuilder.append(line);
        }

        stringBuilder.append(Constants.CHAOS_MESH_PODS);

        Map<String, ArrayList<String>> mapPods = objectMapper.convertValue(params.getPods(), Map.class);
        for (Map.Entry<String, ArrayList<String>> entry : mapPods.entrySet()) {
            String key = entry.getKey();
            line = "      " + key + ":";
            stringBuilder.append(line);
            stringBuilder.append(Constants.NEW_LINE);

            ArrayList<String> value = entry.getValue();
            for (int i=0; i < value.size(); i++) {
                line = "        - " + value.get(i);
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);
            }
        }

        if (params.getKind().equals(Constants.CHAOS_MESH_KIND_POD_CHAOS)) {
            stringBuilder.append(templateService.convert("create_podFaults_podKill.ftl", map));
        } else if (params.getKind().equals(Constants.CHAOS_MESH_KIND_NETWORK_CHAOS)) {
            stringBuilder.append(templateService.convert("create_networkFaults_delay.ftl", map));
        } else if (params.getKind().equals(Constants.CHAOS_MESH_KIND_STRESS_CHAOS)) {
            stringBuilder.append(templateService.convert("create_stressScenarios.ftl", map));
            stringBuilder.append(Constants.NEW_LINE);
            Map<String, Object> mapStressors = objectMapper.convertValue(params.getStressors(), Map.class);
            for (Map.Entry<String, Object> entry : mapStressors.entrySet() ) {
                if (entry.getKey().equals(Constants.CHAOS_MESH_STRESSORS_CPU)) {
                    line = "    " + Constants.CHAOS_MESH_STRESSORS_CPU + ":";
                    stringBuilder.append(line);
                    stringBuilder.append(Constants.NEW_LINE);

                    Object value = entry.getValue();
                    Map<String, Integer> mapCpu = objectMapper.convertValue(value, Map.class);
                    for (Map.Entry<String, Integer> entryCpu: mapCpu.entrySet()) {
                        stringBuilder.append("      " + entryCpu.getKey() + ": " + entryCpu.getValue());
                        stringBuilder.append(Constants.NEW_LINE);
                    }
                } else if (entry.getKey().equals(Constants.CHAOS_MESH_STRESSORS_MEMORY)) {
                    line = "    " + Constants.CHAOS_MESH_STRESSORS_MEMORY + ":";
                    stringBuilder.append(line);
                    stringBuilder.append(Constants.NEW_LINE);

                    Object value = entry.getValue();
                    Map<String, Integer> mapMemory = objectMapper.convertValue(value, Map.class);
                    for (Map.Entry<String, Integer> entryMemory: mapMemory.entrySet()) {
                        stringBuilder.append("      " + entryMemory.getKey() + ": " + entryMemory.getValue());
                        stringBuilder.append(Constants.NEW_LINE);
                    }

                }
            }

        }

        return null;
    }

*/
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
