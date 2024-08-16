package org.container.platform.chaos.api.metrics;

import org.container.platform.chaos.api.chaos.model.ExperimentsItem;
import org.container.platform.chaos.api.clusters.clusters.nodes.support.NodesList;
import org.container.platform.chaos.api.clusters.clusters.nodes.support.NodesListItem;
import org.container.platform.chaos.api.common.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.container.platform.chaos.api.metrics.custom.BaseExponent;
import org.container.platform.chaos.api.metrics.custom.ContainerMetrics;
import org.container.platform.chaos.api.metrics.custom.Quantity;
import org.container.platform.chaos.api.metrics.model.ChaosResource;
import org.container.platform.chaos.api.metrics.model.StressChaos;
import org.container.platform.chaos.api.metrics.model.StressChaosResourcesDataList;
import org.container.platform.chaos.api.workloads.pods.Pods;
import org.container.platform.chaos.api.workloads.pods.PodsList;
import org.container.platform.chaos.api.workloads.pods.support.PodsListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import static org.container.platform.chaos.api.overview.support.SuffixBase.suffixToBinary;
import static org.container.platform.chaos.api.overview.support.SuffixBase.suffixToDecimal;
import static org.springframework.vault.support.DurationParser.parseDuration;

/**
 * Metrics Service 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.08.07
 */

@Service
public class MetricsService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsService.class);

    /**
     * Instantiates a new Roles service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public MetricsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     *  StressChaos Resources Data 생성(Create StressChaos Resources Data)
     *
     * @param params the params
     * @return the ResultStatus
     */
    public ResultStatus createStressChaosResourcesData(Params params){
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
     * @StressChaos StressChaos the StressChaos
     * @return the stressChaos
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
     * @PodsList PodsList the PodsList
     * @return the PodsList
     */

    public PodsList getChaosPodListByLabel(Params params) {
        HashMap responsePodList;
        PodsList totalPodsList = new PodsList();
        params.setNamespace((String) params.getNamespaces().get(0));
        Boolean firstSetting = true;

        for(List<String> value : params.getPods().values()) {
            for(String pod : value){
                params.setResourceName(pod);
                HashMap responsePod = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, Map.class, params);
                Pods pods = commonService.setResultObject(responsePod, Pods.class);
                Map labels = (Map) pods.getLabels();
                String fieldSelectors = "?labelSelector=";
                int count = 0;
                for( Object label : labels.entrySet() ){
                    count++;
                    if(count < labels.size()){
                        fieldSelectors += label + ",";
                    }else {
                        fieldSelectors += label;
                    }
                };
                responsePodList = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiListPodsListUrl() + fieldSelectors, HttpMethod.GET, null, Map.class, params);
                PodsList podsList = commonService.setResultObject(responsePodList, PodsList.class);
                if(firstSetting){
                    totalPodsList.setItems(podsList.getItems());
                    firstSetting = false;
                }else {
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
     * @List<ChaosResource> List<ChaosResource> the List<ChaosResource>
     * @return the chaosResources
     */

    public List<ChaosResource> setChaosResources(PodsList podsList, NodesList nodesList, Params params) {
        List<ChaosResource> chaosResources = new ArrayList<>();

        if(!podsList.getItems().isEmpty()){
            for(PodsListItem item : podsList.getItems() ){
                ChaosResource chaosResource = new ChaosResource();
                chaosResource.setResourceName(item.getName());
                chaosResource.setType("pod");
                for(List<String> pods : params.getPods().values()){
                    for (String pod : pods){
                        if(item.getName().equals(pod)){
                            chaosResource.setChoice(1);
                            break;
                        }else {
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

        for(NodesListItem item : nodesList.getItems()){
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
     * @creationTime creationTime the creationTime
     * @duration duration the duration
     *
     * @return the endTime
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
     * @PodsList PodsList the PodsList
     * @return the PodsList
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

    /**
     * Metrics Node 목록 조회(Get Metrics List for Nodes)
     *
     * @param params the params
     * @return the NodesMetricsList
     */
    public NodesMetricsList getNodesMetricsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsNodesListUrl(), HttpMethod.GET, null, Map.class, params);
        NodesMetricsList nodeMetricsList = commonService.setResultObject(responseMap, NodesMetricsList.class);
        return (NodesMetricsList) commonService.setResultModel(nodeMetricsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Metrics Pods 목록 조회(Get Metrics List for Pods)
     *
     * @param params the params
     * @return the PodsMetricsList
     */
    public PodsMetricsList getPodsMetricsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsPodsListUrl(), HttpMethod.GET, null, Map.class, params);
        PodsMetricsList podsMetricsList = commonService.setResultObject(responseMap, PodsMetricsList.class);
        return (PodsMetricsList) commonService.setResultModel(podsMetricsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Nodes Metrics 조회(Get Metrics for Nodes)
     *
     * @param nodeName         the nodeName
     * @param nodesMetricsList the nodesMetricsList
     * @return the NodesMetricsItems
     */
    public NodesMetricsItems findNodeMetric(String nodeName, NodesMetricsList nodesMetricsList) {
        for (NodesMetricsItems metric : nodesMetricsList.getItems()) {
            if (metric.getName().equals(nodeName)) {
                return metric;
            }
        }
        return null;
    }

    /**
     * Pods 내 ContainerMetrics 합계 (Sum Container Metrics in Pods)
     *
     * @param podsMetricsItems the podsMetricsItems
     * @param type             the type
     * @return the double
     */
    public static double podMetricSum(PodsMetricsItems podsMetricsItems, String type) {
        double sum = 0;
        for (ContainerMetrics containerMetrics : podsMetricsItems.getContainers()) {
            Quantity value = containerMetrics.getUsage().get(type);
            if (value != null) {
                sum += value.getNumber().doubleValue();
            }
        }
        return sum;
    }

    /**
     * Top Pods 계산 (get TopN Pods)
     *
     * @param podsMetricsList the podsMetricsList
     * @param type            the type
     * @param topN            the topN
     * @return the List<TopPods>
     */
    public List<TopPods> topPods(List<PodsMetricsItems> podsMetricsList, String type, Integer topN) {
        List<PodsMetricsItems> items = podsMetricsList;
        Collections.sort(
                items,
                new Comparator<PodsMetricsItems>() {
                    @Override
                    public int compare(PodsMetricsItems pm0, PodsMetricsItems pm1) {
                        double m0 =
                                podMetricSum(pm0, type);
                        double m1 =
                                podMetricSum(pm1, type);
                        return Double.compare(m0, m1) * -1; // sort high to low
                    }

                });

        if (items.size() > topN) {
            items = items.subList(0, topN);
        }

        // top pods 변환
        List<TopPods> topPods = items.stream().map(x -> new TopPods(x.getNamespace(), x.getName(),
                generatePodsUsageMap(Constants.CPU, x), generatePodsUsageMap(Constants.MEMORY, x))).collect(Collectors.toList());

        return topPods;
    }


    /**
     * Pods 사용량 Map 반환 (Return Map for Pods Usage)
     *
     * @param type the type
     * @param pm   the podsMetricsItems
     * @return the Map<String, String>
     */
    public Map<String, Object> generatePodsUsageMap(String type, PodsMetricsItems pm) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.USAGE, convertUsageUnit(type, podMetricSum(pm, type)));
        return result;
    }


    /**
     * Top Nodes 계산 (get TopN Nodes)
     *
     * @param nodesList the nodesList
     * @param type      the type
     * @param topN      the topN
     * @return the List<TopNodes>
     */
    public List<TopNodes> topNodes(List<NodesListItem> nodesList, String type, Integer topN) {
        List<NodesListItem> items = nodesList;
        Collections.sort(
                items,
                new Comparator<NodesListItem>() {
                    @Override
                    public int compare(NodesListItem nm0, NodesListItem nm1) {
                        double p0 = findNodePercentage(nm0, type);
                        double p1 = findNodePercentage(nm1, type);
                        return Double.compare(p0, p1) * -1; // sort high to low
                    }
                });


        if (items.size() > topN) {
            items = items.subList(0, topN);
        }

        // top nodes 변환
        List<TopNodes> topNodes = items.stream().map(x ->
                new TopNodes(x.getClusterName(), x.getClusterId(), x.getName(), generateNodeUsageMap(Constants.CPU, x), generateNodeUsageMap(Constants.MEMORY, x))
        ).collect(Collectors.toList());
        return topNodes;
    }


    /**
     * Nodes 사용량 Map 반환 (Return Map for Nodes Usage)
     *
     * @param type the type
     * @param node the nodesListItem
     * @return the Map<String, String>
     */
    public Map<String, Object> generateNodeUsageMap(String type, NodesListItem node) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.USAGE, convertUsageUnit(type, node.getUsage().get(type).getNumber().doubleValue()));
        result.put(Constants.PERCENT, convertPercnUnit(findNodePercentage(node, type)));
        return result;
    }


    /**
     * Nodes 사용량 Percent 계산 (Get Nodes Usage Percent)
     *
     * @param node the nodesListItem
     * @param type the type
     * @return the double
     */
    public double findNodePercentage(NodesListItem node, String type) {
        Quantity capacity = node.getStatus().getAllocatable().get(type);
        Quantity usage = node.getUsage().get(type);
        if (capacity == null) {
            return Double.POSITIVE_INFINITY;
        }
        return usage.getNumber().doubleValue() / capacity.getNumber().doubleValue();
    }


    /**
     * Percent String 포맷 (Percent String format)
     *
     * @param value the value
     * @return the String
     */
    public long convertPercnUnit(double value) {
        return Math.round(value * 100);
    }


    /**
     * 사용량 단위 변환 (Convert Usage Units)
     *
     * @param type  the type
     * @param usage the usage
     * @return the String
     */
    public long convertUsageUnit(String type, double usage) {
        BaseExponent baseExpont = null;
        String unit = "";
        if (type.equals(Constants.MEMORY)) {
            unit = Constants.MEMORY_UNIT;
            baseExpont = suffixToBinary.get(unit);
        } else {
            unit = Constants.CPU_UNIT;
            baseExpont = suffixToDecimal.get(unit);
        }
        double multiply = Math.pow(baseExpont.getBase(), -baseExpont.getExponent());
        return Math.round(usage * multiply);
    }


    /**
     * Pod Metrics 조회(Get Metrics for Pods)
     *
     * @param podName         the podName
     * @param podsMetricsList the podsMetricsList
     * @return the PodsMetricsItems
     */
    public PodsMetricsItems findPodsMetrics(String podName, PodsMetricsList podsMetricsList) {
        for (PodsMetricsItems metric : podsMetricsList.getItems()) {
            if (metric.getName().equals(podName)) {
                return metric;
            }
        }
        return null;
    }


    /**
     * Pod Metrics 조회(Get Metrics for Nodes)
     *
     * @param podsList the podsList
     * @param params   the params
     * @return the PodsList
     */
    public PodsList setPodsMetrics(PodsList podsList, Params params) {
        PodsMetricsList podsMetricsList;
        try {
            //get pod metrics data
            podsMetricsList = getPodsMetricsList(params);
        } catch (Exception e) {
            LOGGER.info(CommonUtils.loggerReplace("[EXCEPTION OCCURRED WHILE GET PODS METRICS LIST...] >> " + e.getMessage()));
            return podsList;
        }

        for (PodsListItem pods : podsList.getItems()) {
            PodsMetricsItems podsMetricsItems;
            try {
                podsMetricsItems = findPodsMetrics(pods.getName(), podsMetricsList);
                pods.setCpu(generatePodsUsageMapWithUnit(Constants.CPU, podsMetricsItems));
                pods.setMemory(generatePodsUsageMapWithUnit(Constants.MEMORY, podsMetricsItems));
            } catch (Exception e) {
            }

        }
        return podsList;
    }


    /**
     * Pods 사용량 Map 반환 (Return Map for Pods Usage)
     *
     * @param type the type
     * @param pm   the podsMetricsItems
     * @return the Map<String, String>
     */
    public Map<String, Object> generatePodsUsageMapWithUnit(String type, PodsMetricsItems pm) {
        String unit = (type.equals(Constants.CPU)) ? Constants.CPU_UNIT : Constants.MEMORY_UNIT;
        Map<String, Object> result = new HashMap<>();
        result.put(Constants.USAGE, convertUsageUnit(type, podMetricSum(pm, type)) + unit);
        return result;
    }


    /**
     * Metrics Pods 상세 조회(Get Metrics for Pods)
     *
     * @param params the params
     * @return the PodsMetricsList
     */
        public PodsMetricsItems getPodsMetricsDetails(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiMetricsPodsGetUrl(), HttpMethod.GET, null, Map.class, params);
        PodsMetricsItems podsMetricsItems = commonService.setResultObject(responseMap, PodsMetricsItems.class);
        return podsMetricsItems;
    }


    /**
     * Metrics Pods 상세 설정(Setting Metrics for Pods)
     *
     * @param params the params
     * @return the Pods
     */
    public Pods setPodsMetricsDetails(Pods pods, Params params) {
        PodsMetricsItems podsMetricsItems;
        try {
            //get pod metrics detail data
            podsMetricsItems = getPodsMetricsDetails(params);
            pods.setCpu(generatePodsUsageMapWithUnit(Constants.CPU, podsMetricsItems));
            pods.setMemory(generatePodsUsageMapWithUnit(Constants.MEMORY, podsMetricsItems));
        } catch (Exception e) {
            LOGGER.info(CommonUtils.loggerReplace("[EXCEPTION OCCURRED WHILE GET PODS METRICS DETAIL ...] >> " + e.getMessage()));
            return pods;
        }
        return pods;
    }


}