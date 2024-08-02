package org.container.platform.chaos.api.chaos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.container.platform.chaos.api.chaos.model.*;
import org.container.platform.chaos.api.common.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.logging.log4j.util.Strings.isEmpty;

/**
 * Experiments Service 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.06.21
 */
@Service
public class ExperimentsService {

    private final CommonService commonService;

    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final TemplateService templateService;



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
        HashMap responseMapNetworkDelay = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getCpChaosApiListNetworkFaultsDelayListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList networkDelayList = commonService.setResultObject(responseMapNetworkDelay, ExperimentsList.class);
        experimentsList.getItems().addAll(networkDelayList.getItems());

        HashMap responseMapStress = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getCpChaosApiListStressScenariosListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList stressList = commonService.setResultObject(responseMapStress, ExperimentsList.class);
        experimentsList.getItems().addAll(stressList.getItems());

        experimentsList = commonService.resourceListProcessing(experimentsList, params, ExperimentsList.class);

        return (ExperimentsList) commonService.setResultModel(experimentsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Experiments 상세 조회(Get Experiments Detail)
     *
     * @param params the params
     * @return the experiments detail
     */

    public Experiments getExperiments(Params params) {
        Experiments experiments = new Experiments();

        if (params.kind.equals("PodChaos")) {
            HashMap responseMapPodFault = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListPodFaultsPodKillGetUrl(), HttpMethod.GET, null, Map.class, params);
            ExperimentsItem podFaultItem = commonService.setResultObject(responseMapPodFault, ExperimentsItem.class);
            experiments.addItem(podFaultItem);
        }
        if (params.kind.equals("NetworkChaos")) {
            HashMap responseMapNetworkDelay = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListNetworkFaultsDelayGetUrl(), HttpMethod.GET, null, Map.class, params);
            ExperimentsItem networkDelayList = commonService.setResultObject(responseMapNetworkDelay, ExperimentsItem.class);
            experiments.addItem(networkDelayList);
        }

        if (params.kind.equals("StressChaos")) {
            HashMap responseMapStress = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListStressScenariosGetUrl(), HttpMethod.GET, null, Map.class, params);
            ExperimentsItem stressList = commonService.setResultObject(responseMapStress, ExperimentsItem.class);
            experiments.addItem(stressList);
        }


        return (Experiments) commonService.setResultModel(experiments, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ExperimentsList Status 목록 조회 (Get Experiments Status List)
     *
     * @param params the params
     * @return the ExperimentsStatusList
     */
    public ExperimentsDashboardList getExperimentsStatusList(Params params) {
        ObjectMapper mapper = new ObjectMapper();
        ExperimentsDashboardList experimentsDashboardList = new ExperimentsDashboardList();

        List<ExperimentsDashboardListItems> items = mapper.convertValue(
                restTemplateService.send(Constants.TARGET_CHAOS_DASHBOARD_API,
                        propertyService.getCpChaosDashboardApiListUrl(),
                        HttpMethod.GET, null, ArrayList.class, params),
            new TypeReference<List<ExperimentsDashboardListItems>>(){}
        );
        List<ExperimentsDashboardListItems> newItems = new ArrayList<>();

        for(ExperimentsDashboardListItems item : items) {
            for (Object uid : params.getStatusList()) {
                if (Objects.equals(uid, item.getUid())) {
                    newItems.add(item);
                    break;
                }
            }
        }
        experimentsDashboardList.setItems(newItems);

       return (ExperimentsDashboardList) commonService.setResultModel(experimentsDashboardList, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * ExperimentsList 상세 Status 조회 (Get Experiments Detail Status)
     *
     * @param params the params
     * @return the ExperimentsDashboard
     */
    public ExperimentsDashboard getExperimentsStatus(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_DASHBOARD_API,
                propertyService.getCpChaosDashboardApiGetUrl(), HttpMethod.GET, null, Map.class, params);

        ExperimentsDashboard experimentsDashboard = commonService.setResultObject(responseMap, ExperimentsDashboard.class);

        return (ExperimentsDashboard) commonService.setResultModel(experimentsDashboard, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Experiments 생성(Create Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */

    public ResultStatus createExperiments(Params params) {
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();

        Map map = new HashMap();
        map.put("kind", params.getKind());
        map.put("name", params.getName());
        map.put("namespace", params.getChaosNamespace());
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
        Map<String, String> mapLabelSelector = params.getLabelSelectors();

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

        ResultStatus resultStatus = null;
        if (params.getKind().equals(Constants.CHAOS_MESH_KIND_POD_CHAOS)) {
            stringBuilder.append(templateService.convert("create_podFaults_podKill.ftl", map));
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListPodFaultsPodKillCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        }
        else if (params.getKind().equals(Constants.CHAOS_MESH_KIND_NETWORK_CHAOS)) {
            stringBuilder.append(templateService.convert("create_networkFaults_delay.ftl", map));
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListNetworkFaultsDelayCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        }
        else if (params.getKind().equals(Constants.CHAOS_MESH_KIND_STRESS_CHAOS)) {
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
                }
                if (entry.getKey().equals(Constants.CHAOS_MESH_STRESSORS_MEMORY)) {
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
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListStressScenariosDeleteUrl(), HttpMethod.POST, ResultStatus.class, params);


        }

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * Experiments 삭제(Delete Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteExperiments(Params params) {
        ResultStatus resultStatus = null;
        if (params.kind.equals("PodChaos")) {
            resultStatus = restTemplateService.send(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListPodFaultsPodKillDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        }
        if (params.kind.equals("NetworkChaos")) {
            resultStatus = restTemplateService.send(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListNetworkFaultsDelayDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        }
        if (params.kind.equals("StressChaos")) {
            resultStatus = restTemplateService.send(Constants.TARGET_CHAOS_API,
                    propertyService.getCpChaosApiListStressScenariosDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

        }
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * ExperimentsList Events 목록 조회 (Get Experiments Events List)
     *
     * @param params the params
     * @return the ExperimentsEventsList
     */
    public ExperimentsEventsList getExperimentsEventsList(Params params) {
        ObjectMapper mapper = new ObjectMapper();
        ExperimentsEventsList experimentsEventsList = new ExperimentsEventsList();

        if(isEmpty(params.getObject_id())) {

            if(params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
                List<ExperimentsEventsListItems> items = mapper.convertValue(
                        restTemplateService.send(Constants.TARGET_CHAOS_DASHBOARD_API,
                                propertyService.getCpChaosDashboardApiListEventUrl(),
                                HttpMethod.GET, null, ArrayList.class, params),
                        new TypeReference<List<ExperimentsEventsListItems>>() {
                        }
                );
                experimentsEventsList.setItems(items);
                experimentsEventsList = commonService.resourceListProcessing(experimentsEventsList, params, ExperimentsEventsList.class);
                return (ExperimentsEventsList) commonService.setResultModel(experimentsEventsList, Constants.RESULT_STATUS_SUCCESS);
            }else {
                String nsfieldSelector = "?namespace=" + params.getNamespace();

                List<ExperimentsEventsListItems> items = mapper.convertValue(
                        restTemplateService.send(Constants.TARGET_CHAOS_DASHBOARD_API,
                                propertyService.getCpChaosDashboardApiListEventUrl() + nsfieldSelector,
                                HttpMethod.GET, null, ArrayList.class, params),
                        new TypeReference<List<ExperimentsEventsListItems>>() {
                        }
                );
                experimentsEventsList.setItems(items);
                experimentsEventsList = commonService.resourceListProcessing(experimentsEventsList, params, ExperimentsEventsList.class);
                return (ExperimentsEventsList) commonService.setResultModel(experimentsEventsList, Constants.RESULT_STATUS_SUCCESS);
            }
        }
        else {
            String uidfieldSelector = "?object_id=" + params.getObject_id();
            List<ExperimentsEventsListItems> items = mapper.convertValue(
                    restTemplateService.send(Constants.TARGET_CHAOS_DASHBOARD_API,
                            propertyService.getCpChaosDashboardApiListEventUrl() + uidfieldSelector,
                            HttpMethod.GET, null, ArrayList.class, params),
                    new TypeReference<List<ExperimentsEventsListItems>>() {
                    }
            );
            experimentsEventsList.setItems(items);
            experimentsEventsList = commonService.resourceListProcessing(experimentsEventsList, params, ExperimentsEventsList.class);
            return (ExperimentsEventsList) commonService.setResultModel(experimentsEventsList, Constants.RESULT_STATUS_SUCCESS);
        }
    }

    /**
     * Experiments Resource Usage of Chaos 목록 조회 (Get Experiments Resource Usage of Chaos List)
     *
     * @param params the params
     * @return the ResourceUsageOfChaosList
     */
    public ResourceUsageOfChaosList getResourceUsageOfChaosList(Params params) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_COMMON_API,
                "/resourceUsageOfChaos", HttpMethod.GET, null, Map.class, params);

        ResourceUsageOfChaosList resourceUsageOfChaosList = commonService.setResultObject(responseMap, ResourceUsageOfChaosList.class);



        return (ResourceUsageOfChaosList) commonService.setResultModel(resourceUsageOfChaosList, Constants.RESULT_STATUS_SUCCESS);

    }


}
