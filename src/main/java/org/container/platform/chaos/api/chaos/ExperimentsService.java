package org.container.platform.chaos.api.chaos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.container.platform.chaos.api.chaos.model.*;
import org.container.platform.chaos.api.clusters.clusters.nodes.support.NodesList;
import org.container.platform.chaos.api.clusters.clusters.nodes.support.NodesListItem;
import org.container.platform.chaos.api.common.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.container.platform.chaos.api.chaos.model.ChaosResource;
import org.container.platform.chaos.api.chaos.model.ResourceUsageOfChaosList;
import org.container.platform.chaos.api.chaos.model.StressChaos;
import org.container.platform.chaos.api.chaos.model.StressChaosResourcesDataList;
import org.container.platform.chaos.api.workloads.pods.Pods;
import org.container.platform.chaos.api.workloads.pods.PodsList;
import org.container.platform.chaos.api.workloads.pods.support.PodsListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.springframework.vault.support.DurationParser.parseDuration;

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
     * @param commonService       common service
     * @param propertyService     the property service
     * @param templateService     the template service
     * @param metricsService
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

        HashMap responseMapPodFault = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosPodFaultsPodKillListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList podFaultList = commonService.setResultObject(responseMapPodFault, ExperimentsList.class);
        experimentsList.setItems(podFaultList.getItems());
        HashMap responseMapNetworkDelay = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosNetworkFaultsDelayListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList networkDelayList = commonService.setResultObject(responseMapNetworkDelay, ExperimentsList.class);
        experimentsList.getItems().addAll(networkDelayList.getItems());

        HashMap responseMapStress = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosStressScenariosListUrl(), HttpMethod.GET, null, Map.class, params);
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
            HashMap responseMapPodFault = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosPodFaultsPodKillGetUrl(), HttpMethod.GET, null, Map.class, params);
            ExperimentsItem podFaultItem = commonService.setResultObject(responseMapPodFault, ExperimentsItem.class);
            experiments.addItem(podFaultItem);
        }
        if (params.kind.equals("NetworkChaos")) {
            HashMap responseMapNetworkDelay = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosNetworkFaultsDelayGetUrl(), HttpMethod.GET, null, Map.class, params);
            ExperimentsItem networkDelayList = commonService.setResultObject(responseMapNetworkDelay, ExperimentsItem.class);
            experiments.addItem(networkDelayList);
        }

        if (params.kind.equals("StressChaos")) {
            HashMap responseMapStress = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosStressScenariosGetUrl(), HttpMethod.GET, null, Map.class, params);
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
                restTemplateService.send(Constants.TARGET_CHAOS_API,
                        propertyService.getChaosApiExperimentListUrl(),
                        HttpMethod.GET, null, ArrayList.class, params),
                new TypeReference<List<ExperimentsDashboardListItems>>() {
                }
        );
        List<ExperimentsDashboardListItems> newItems = new ArrayList<>();

        for (ExperimentsDashboardListItems item : items) {
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
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getChaosApiExperimentGetUrl(), HttpMethod.GET, null, Map.class, params);

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
        for (int i = 0; i < params.getNamespaces().size(); i++) {
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
            for (int i = 0; i < value.size(); i++) {
                line = "        - " + value.get(i);
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);
            }
        }

        ResultStatus resultStatus = null;
        if (params.getKind().equals(Constants.CHAOS_MESH_KIND_POD_CHAOS)) {
            stringBuilder.append(templateService.convert("create_podFaults_podKill.ftl", map));
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosPodFaultsPodKillCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        } else if (params.getKind().equals(Constants.CHAOS_MESH_KIND_NETWORK_CHAOS)) {
            stringBuilder.append(templateService.convert("create_networkFaults_delay.ftl", map));
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosNetworkFaultsDelayCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        } else if (params.getKind().equals(Constants.CHAOS_MESH_KIND_STRESS_CHAOS)) {
            stringBuilder.append(templateService.convert("create_stressScenarios.ftl", map));
            stringBuilder.append(Constants.NEW_LINE);

            Map<String, Object> mapStressors = objectMapper.convertValue(params.getStressors(), Map.class);
            for (Map.Entry<String, Object> entry : mapStressors.entrySet()) {
                if (entry.getKey().equals(Constants.CHAOS_MESH_STRESSORS_CPU)) {
                    line = "    " + Constants.CHAOS_MESH_STRESSORS_CPU + ":";
                    stringBuilder.append(line);
                    stringBuilder.append(Constants.NEW_LINE);

                    Object value = entry.getValue();
                    Map<String, Integer> mapCpu = objectMapper.convertValue(value, Map.class);
                    for (Map.Entry<String, Integer> entryCpu : mapCpu.entrySet()) {
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
                    for (Map.Entry<String, Integer> entryMemory : mapMemory.entrySet()) {
                        stringBuilder.append("      " + entryMemory.getKey() + ": " + entryMemory.getValue());
                        stringBuilder.append(Constants.NEW_LINE);
                    }
                }
            }
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosStressScenariosCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

            ResultStatus resultStatusDB = createStressChaosResourcesData(params);
            if (!resultStatusDB.getResultCode().equals("SUCCESS")) {
                params.setNamespace(params.getChaosNamespace());
                restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiChaosStressScenariosDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

                return (ResultStatus) commonService.setResultModel(resultStatusDB, Constants.RESULT_STATUS_FAIL);
            }
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
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosPodFaultsPodKillDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        }
        if (params.kind.equals("NetworkChaos")) {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosNetworkFaultsDelayDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        }
        if (params.kind.equals("StressChaos")) {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosStressScenariosDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

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

        if (isEmpty(params.getObject_id())) {

            if (params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
                List<ExperimentsEventsListItems> items = mapper.convertValue(
                        restTemplateService.send(Constants.TARGET_CHAOS_API,
                                propertyService.getChaosApiEventListUrl(),
                                HttpMethod.GET, null, ArrayList.class, params),
                        new TypeReference<List<ExperimentsEventsListItems>>() {
                        }
                );
                experimentsEventsList.setItems(items);
                experimentsEventsList = commonService.resourceListProcessing(experimentsEventsList, params, ExperimentsEventsList.class);
                return (ExperimentsEventsList) commonService.setResultModel(experimentsEventsList, Constants.RESULT_STATUS_SUCCESS);
            } else {
                String nsfieldSelector = "?namespace=" + params.getNamespace();

                List<ExperimentsEventsListItems> items = mapper.convertValue(
                        restTemplateService.send(Constants.TARGET_CHAOS_API,
                                propertyService.getChaosApiEventListUrl() + nsfieldSelector,
                                HttpMethod.GET, null, ArrayList.class, params),
                        new TypeReference<List<ExperimentsEventsListItems>>() {
                        }
                );
                experimentsEventsList.setItems(items);
                experimentsEventsList = commonService.resourceListProcessing(experimentsEventsList, params, ExperimentsEventsList.class);
                return (ExperimentsEventsList) commonService.setResultModel(experimentsEventsList, Constants.RESULT_STATUS_SUCCESS);
            }
        } else {
            String uidfieldSelector = "?object_id=" + params.getObject_id();
            List<ExperimentsEventsListItems> items = mapper.convertValue(
                    restTemplateService.send(Constants.TARGET_CHAOS_API,
                            propertyService.getChaosApiEventListUrl() + uidfieldSelector,
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
                "/chaos/resourceGraph", HttpMethod.GET, null, Map.class, params);

        ResourceUsageOfChaosList resourceUsageOfChaosList = commonService.setResultObject(responseMap, ResourceUsageOfChaosList.class);
        return (ResourceUsageOfChaosList) commonService.setResultModel(resourceUsageOfChaosList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StressChaos Resources Data 생성(Create StressChaos Resources Data)
     *
     * @param params the params
     * @return the ResultStatus
     */
    public ResultStatus createStressChaosResourcesData(Params params) {
        StressChaosResourcesDataList stressChaosResourcesDataList = new StressChaosResourcesDataList();
        stressChaosResourcesDataList.setStressChaos(getStressChaos(params));
        stressChaosResourcesDataList.setChaosResource(getChaosResources(params));
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos", HttpMethod.POST, stressChaosResourcesDataList, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Stress Chaos 정보 조회  (Get Stress Chaos info)
     *
     * @param params the params
     * @return the ResultStatus
     */
    public StressChaos getStressChaos(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosStressScenariosGetUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsItem experimentsItem = commonService.setResultObject(responseMap, ExperimentsItem.class);
        StressChaos stressChaos = setStressChaos(experimentsItem);

        return stressChaos;
    }

    /**
     * Chaos Resource 정보 조회  (Get Chaos Resource info)
     *
     * @param params the params
     * @return the ResultStatus
     */
    public List<ChaosResource> getChaosResources(Params params) {
        PodsList podsList = getChaosPodListByLabel(params);
        NodesList nodesList = getNodesList(params);
        List<ChaosResource> chaosResources = setChaosResources(podsList, nodesList, params);

        return chaosResources;
    }

    /**
     * StressChaos 값 설정  (Set StressChaos)
     *
     * @return the stressChaos
     * @StressChaos StressChaos the StressChaos
     */
    public StressChaos setStressChaos(ExperimentsItem experimentsItem) {
        StressChaos stressChaos = new StressChaos();
        stressChaos.setChaosName(experimentsItem.getMetadata().getName());
        stressChaos.setNamespaces((String) experimentsItem.getSpec().getSelector().getNamespaces().get(0));
        stressChaos.setCreationTime(experimentsItem.getMetadata().getCreationTimestamp());
        stressChaos.setDuration(experimentsItem.getSpec().getDuration());
        stressChaos.setEndTime(calculateEndTime(experimentsItem.getMetadata().getCreationTimestamp(), experimentsItem.getSpec().getDuration()));

        return stressChaos;
    }

    /**
     * Chaos Pod List By Label 조회  (Get Chaos Pod List By Label)
     *
     * @return the PodsList
     * @PodsList PodsList the PodsList
     */
    public PodsList getChaosPodListByLabel(Params params) {
        HashMap responsePodList;
        PodsList totalPodsList = new PodsList();
        params.setNamespace((String) params.getNamespaces().get(0));
        Boolean firstSetting = true;

        for (List<String> value : params.getPods().values()) {
            for (String pod : value) {
                params.setResourceName(pod);
                HashMap responsePod = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, Map.class, params);
                Pods pods = commonService.setResultObject(responsePod, Pods.class);
                Map labels = (Map) pods.getLabels();
                String fieldSelectors = "?labelSelector=";
                int count = 0;
                for (Object label : labels.entrySet()) {
                    count++;
                    if (count < labels.size()) {
                        fieldSelectors += label + ",";
                    } else {
                        fieldSelectors += label;
                    }
                }
                responsePodList = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiListPodsListUrl() + fieldSelectors, HttpMethod.GET, null, Map.class, params);
                PodsList podsList = commonService.setResultObject(responsePodList, PodsList.class);
                if (firstSetting) {
                    totalPodsList.setItems(podsList.getItems());
                    firstSetting = false;
                } else {
                    totalPodsList.getItems().addAll(podsList.getItems());
                }
            }
        }
        PodsList removeDuplicatePodLists = removeDuplicatePodsList(totalPodsList);

        return (PodsList) commonService.setResultModel(removeDuplicatePodLists, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Nodes 목록 조회(Get Nodes list)
     *
     * @param params the params
     * @return the nodes list
     */
    public NodesList getNodesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNodesListUrl(), HttpMethod.GET, null, Map.class, params);
        NodesList nodesList = commonService.setResultObject(responseMap, NodesList.class);
        nodesList = commonService.resourceListProcessing(nodesList, params, NodesList.class);

        return (NodesList) commonService.setResultModel(nodesList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Chaos Resource 값 설정  (Set Chaos Resource)
     *
     * @return the chaosResources
     * @List<ChaosResource> List<ChaosResource> the List<ChaosResource>
     */
    public List<ChaosResource> setChaosResources(PodsList podsList, NodesList nodesList, Params params) {
        List<ChaosResource> chaosResources = new ArrayList<>();

        if (!podsList.getItems().isEmpty()) {
            for (PodsListItem item : podsList.getItems()) {
                ChaosResource chaosResource = new ChaosResource();
                chaosResource.setResourceName(item.getName());
                chaosResource.setType("pod");
                for (List<String> pods : params.getPods().values()) {
                    for (String pod : pods) {
                        if (item.getName().equals(pod)) {
                            chaosResource.setChoice(1);
                            break;
                        } else {
                            chaosResource.setChoice(0);
                        }
                    }
                }
                chaosResource.setGenerateName(item.getMetadata().getGenerateName());
                chaosResource.setChaosName(params.getName());
                chaosResource.setNamespaces(item.getNamespace());
                chaosResources.add(chaosResource);
            }
        }

        for (NodesListItem item : nodesList.getItems()) {
            ChaosResource chaosResource = new ChaosResource();
            chaosResource.setResourceName(item.getName());
            chaosResource.setType("node");
            chaosResource.setChoice(0);
            chaosResources.add(chaosResource);
        }

        return chaosResources;
    }

    /**
     * EndTime 계산 (Calculate EndTime)
     *
     * @return the endTime
     * @creationTime creationTime the creationTime
     * @duration duration the duration
     */
    public String calculateEndTime(String creationTime, String duration) {
        ZonedDateTime creationDateTime = ZonedDateTime.parse(creationTime, DateTimeFormatter.ISO_DATE_TIME);
        Duration durationParsed = parseDuration(duration);
        ZonedDateTime endDateTime = creationDateTime.plus(durationParsed);

        return endDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * PodsList 내 중복 파드 제거 (Remove duplicate pods in PodsList)
     *
     * @return the PodsList
     * @PodsList PodsList the PodsList
     */
    public PodsList removeDuplicatePodsList(PodsList podsList) {
        Set<String> podNames = new HashSet<>();
        List<PodsListItem> removeDuplicateItems = podsList.getItems().stream()
                .filter(item -> podNames.add(item.getName()))
                .collect(Collectors.toList());
        PodsList newPodsList = new PodsList();
        newPodsList.setItems(removeDuplicateItems);

        return newPodsList;
    }
}
