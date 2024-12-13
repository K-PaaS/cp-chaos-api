package org.container.platform.chaos.api.chaos;

import org.container.platform.chaos.api.chaos.model.*;
import org.container.platform.chaos.api.common.CommonService;
import org.container.platform.chaos.api.common.Constants;
import org.container.platform.chaos.api.common.PropertyService;
import org.container.platform.chaos.api.common.RestTemplateService;
import org.container.platform.chaos.api.common.model.CommonStatusCode;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.container.platform.chaos.api.workloads.pods.PodsList;
import org.container.platform.chaos.api.workloads.pods.support.HorizontalPodAutoscalerList;
import org.container.platform.chaos.api.workloads.replicaSets.ReplicaSetsList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * ExperimentsServiceTest 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-12-03
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ExperimentsServiceTest {

    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";

    private static final String PODKILLTYPE = "PodChaos";
    private static final String NETWORKDELAYTYPE = "NetworkChaos";
    private static final String STRESSCHAOSTYPE = "StressChaos";

    private static final String CREATIONTIME = "2024-12-06T09:00:00Z";
    private static final String NAME = "chaos";

    private static HashMap gResultMap;
    private static ResultStatus gResultStatusModel;

    private static ExperimentsList gExperimentsListModel;
    private static ExperimentsList gPodFaultExperimentsList;
    private static ExperimentsList gNetworkDelayExperimentsList;
    private static ExperimentsList gStressExperimentsList;
    private static ExperimentsItem gExperimentsItem;
    private static Experiments gExperiments;
    private static ExperimentsDashboardList gExperimentsDashboardList;
    private static ExperimentsDashboard gExperimentsDashboard;
    private static List<ExperimentsEventsListItems> gExperimentsEventsListItems;
    private static ExperimentsEventsList gExperimentsEventsList;
    private static StressChaos gStressChaos;
    private static HorizontalPodAutoscalerList gHorizontalPodAutoscalerList;
    private static PodsList gPodsList;
    private static ReplicaSetsList gReplicaSetsList;
    private static ResourceUsage gResourceUsage;

    private static Params gParams;


    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @InjectMocks
    ExperimentsService experimentsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        gParams = new Params();
        gParams.setCluster(CLUSTER);
        gParams.setNamespace(NAMESPACE);

        gResultMap = new HashMap<>();

        gResultStatusModel = new ResultStatus();
        gResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gResultStatusModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gExperimentsEventsListItems = new ArrayList<>();

        gExperimentsEventsList = new ExperimentsEventsList();

        ExperimentsListItems.Metadata podFaultMetadata = new ExperimentsListItems.Metadata();
        podFaultMetadata.setCreationTimestamp(CREATIONTIME);
        podFaultMetadata.setName(NAME);
        podFaultMetadata.setNamespace(NAMESPACE);

        ExperimentsListItems podFaultsItem = new ExperimentsListItems();
        podFaultsItem.setKind(PODKILLTYPE);
        podFaultsItem.setMetadata(podFaultMetadata);

        List<ExperimentsListItems> podFaultItemList = new ArrayList<>();
        podFaultItemList.add(podFaultsItem);

        gPodFaultExperimentsList = new ExperimentsList();
        gPodFaultExperimentsList.setItems(podFaultItemList);


        ExperimentsListItems.Metadata networkDelayMetadata = new ExperimentsListItems.Metadata();
        networkDelayMetadata.setCreationTimestamp(CREATIONTIME);
        networkDelayMetadata.setName(NAME);
        networkDelayMetadata.setNamespace(NAMESPACE);

        ExperimentsListItems networkDelayItem = new ExperimentsListItems();
        networkDelayItem.setKind(NETWORKDELAYTYPE);
        networkDelayItem.setMetadata(networkDelayMetadata);

        List<ExperimentsListItems> networkDelayItemList = new ArrayList<>();
        networkDelayItemList.add(networkDelayItem);

        gNetworkDelayExperimentsList = new ExperimentsList();
        gNetworkDelayExperimentsList.setItems(networkDelayItemList);

        ExperimentsListItems.Metadata stressMetadata = new ExperimentsListItems.Metadata();
        stressMetadata.setCreationTimestamp(CREATIONTIME);
        stressMetadata.setName(NAME);
        stressMetadata.setNamespace(NAMESPACE);

        ExperimentsListItems stressItem = new ExperimentsListItems();
        stressItem.setKind(STRESSCHAOSTYPE);
        stressItem.setMetadata(stressMetadata);

        List<ExperimentsListItems> stressItemList = new ArrayList<>();
        stressItemList.add(stressItem);

        gStressExperimentsList = new ExperimentsList();
        gStressExperimentsList.setItems(stressItemList);

        gExperimentsListModel = new ExperimentsList();
        gExperimentsListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gExperimentsListModel.setItems(podFaultItemList);
        gExperimentsListModel.getItems().add(networkDelayItem);
        gExperimentsListModel.getItems().add(stressItem);

        gExperiments = new Experiments();
        gExperiments.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gExperimentsItem = new ExperimentsItem();

        gExperimentsDashboard = new ExperimentsDashboard();

        gExperimentsDashboardList = new ExperimentsDashboardList();

        gStressChaos = new StressChaos();

        gHorizontalPodAutoscalerList = new HorizontalPodAutoscalerList();

        gPodsList = new PodsList();

        gReplicaSetsList = new ReplicaSetsList();

        gResourceUsage = new ResourceUsage();

    }

    @Test
    public void getExperimentsList_Valid_ReturnModel() {
        when(propertyService.getCpMasterApiChaosPodFaultsPodKillListUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/podchaos");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/podchaos", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsList.class)).thenReturn(gPodFaultExperimentsList);

        when(propertyService.getCpMasterApiChaosNetworkFaultsDelayListUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/networkchaos");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/networkchaos", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsList.class)).thenReturn(gNetworkDelayExperimentsList);

        when(propertyService.getCpMasterApiChaosStressScenariosListUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsList.class)).thenReturn(gStressExperimentsList);

        when(commonService.resourceListProcessing(gExperimentsListModel, gParams, ExperimentsList.class)).thenReturn(gExperimentsListModel);
        when(commonService.setResultModel(gExperimentsListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gExperimentsListModel);

        ExperimentsList experimentsList = experimentsService.getExperimentsList(gParams);

//        assertThat(experimentsList).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, experimentsList.getResultCode());
    }

    @Test
    public void getExperiments() {
        when(propertyService.getCpMasterApiChaosPodFaultsPodKillGetUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/podchaos/{chaosname}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/podchaos/{chaosname}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsItem.class)).thenReturn(gExperimentsItem);

        when(propertyService.getCpMasterApiChaosNetworkFaultsDelayListUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/networkchaos/{chaosname}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/networkchaos/{chaosname}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsItem.class)).thenReturn(gExperimentsItem);

        when(propertyService.getCpMasterApiChaosStressScenariosListUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos/{chaosname}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos/{chaosname}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsItem.class)).thenReturn(gExperimentsItem);

        when(commonService.setResultModel(gExperiments, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gExperiments);

        Experiments experiments = experimentsService.getExperiments(gParams);

//        assertThat(experiments).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, experiments.getResultCode());
    }

    @Test
    public void getExperimentsStatusList() {
        when(propertyService.getChaosApiExperimentListUrl()).thenReturn("/api/experiments");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/api/experiments", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);

        when(commonService.setResultModel(gExperimentsDashboardList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gExperimentsDashboardList);

        ExperimentsDashboardList experimentsDashboardList = experimentsService.getExperimentsStatusList(gParams);

//        assertThat(experimentsDashboardList).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, experimentsDashboardList.getResultCode());
    }

    @Test
    public void getExperimentsStatus() {
        when(propertyService.getChaosApiExperimentGetUrl()).thenReturn("/api/experiments/{uid}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/api/experiments/{uid}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsDashboard.class)).thenReturn(gExperimentsDashboard);

        when(commonService.setResultModel(gExperimentsDashboard, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gExperimentsDashboard);

        ExperimentsDashboard experimentsDashboard = experimentsService.getExperimentsStatus(gParams);

//        assertThat(experimentsDashboard).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, experimentsDashboard.getResultCode());
    }

    @Test
    public void createExperiments() {
        when(propertyService.getCpMasterApiChaosPodFaultsPodKillCreateUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/podchaos");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosPodFaultsPodKillCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(propertyService.getCpMasterApiChaosNetworkFaultsDelayCreateUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/networkchaos");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosNetworkFaultsDelayCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(propertyService.getCpMasterApiChaosStressScenariosCreateUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosStressScenariosCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(propertyService.getCpMasterApiChaosStressScenariosDeleteUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos/{chaosname}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosStressScenariosDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModel);

        when(restTemplateService.send(Constants.TARGET_CHAOS_COLLECTOR_API,
                "/scheduler", HttpMethod.POST, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModel);

//        ResultStatus resultStatus = experimentsService.createExperiments(gParams);

//        assertThat(resultStatus).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultStatus.getResultCode());

    }

    @Test
    public void deleteExperiments() {
        when(propertyService.getCpMasterApiChaosPodFaultsPodKillDeleteUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/podchaos/{chaosname}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosPodFaultsPodKillDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(propertyService.getCpMasterApiChaosNetworkFaultsDelayDeleteUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/networkchaos/{chaosname}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosNetworkFaultsDelayDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(propertyService.getCpMasterApiChaosStressScenariosDeleteUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos/{chaosname}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosStressScenariosDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModel);

        ResultStatus resultStatus = experimentsService.deleteExperiments(gParams);

//        assertThat(resultStatus).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultStatus.getResultCode());

    }

    @Test
    public void deleteStressChaos() {
        when(restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos/stressChaos/{chaosName}".replace("{chaosName}", gParams.getName()), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModel);

        ResultStatus resultStatus = experimentsService.deleteStressChaos(gParams);

//        assertThat(resultStatus).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultStatus.getResultCode());

    }

    @Test
    public void getExperimentsEventsList() {
        when(propertyService.getChaosApiEventListUrl()).thenReturn("/api/events");
        when(restTemplateService.send(Constants.TARGET_COMMON_API,
                propertyService.getChaosApiEventListUrl(),
                HttpMethod.GET, null, ArrayList.class, gParams)).thenReturn((ArrayList) gExperimentsEventsListItems);

        when(commonService.resourceListProcessing(gExperimentsEventsList, gParams, ExperimentsEventsList.class)).thenReturn(gExperimentsEventsList);
        when(commonService.setResultModel(gExperimentsEventsList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gExperimentsEventsList);

        ExperimentsEventsList experimentsEventsList = experimentsService.getExperimentsEventsList(gParams);

//        assertThat(experimentsEventsList).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, experimentsEventsList.getResultCode());

    }

    @Test
    public void createStressChaosData() {
        StressChaos stressChaos = new StressChaos();

        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API,
                "/chaos/stressChaos", HttpMethod.POST, stressChaos, StressChaos.class, gParams)).thenReturn(gStressChaos);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatusModel);

//        StressChaos resultStressChaos = experimentsService.createStressChaosData(gParams);

//        assertThat(resultStressChaos).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultStressChaos.getResultCode());
    }

    @Test
    public void getStressChaos() {
        when(propertyService.getCpMasterApiChaosStressScenariosGetUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos/{chaosname}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosStressScenariosGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsItem.class)).thenReturn(gExperimentsItem);

//        StressChaos stressChaos = experimentsService.getStressChaos(gParams);

//        assertThat(stressChaos).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, stressChaos.getResultCode());

    }

    @Test
    public void setStressChaos() {

    }

    @Test
    public void calculateEndTime() {

    }

    @Test
    public void getResourceUsageByPod() {
        when(propertyService.getCpMasterApiListAutoscalingListUrl()).thenReturn("/apis/autoscaling/v2/namespaces/{namespace}/horizontalpodautoscalers");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListAutoscalingListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, HorizontalPodAutoscalerList.class)).thenReturn(gHorizontalPodAutoscalerList);

        when(propertyService.getCpMasterApiListPodsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsList.class)).thenReturn(gPodsList);

        when(propertyService.getCpMasterApiListReplicaSetsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsList.class)).thenReturn(gReplicaSetsList);

        when(restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos/resourceUsageByHpaPod/{chaosName}".replace("{chaosName}", gParams.getName()), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);

        when(restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos/resourceUsageByPod/{chaosName}".replace("{chaosName}", gParams.getName()), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);

        when(commonService.setResultObject(gResultMap, ResourceUsage.class)).thenReturn(gResourceUsage);
        when(commonService.setResultModel(gResourceUsage, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResourceUsage);

//        ResourceUsage resourceUsage = experimentsService.getResourceUsageByPod(gParams);

//        assertThat(resourceUsage).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, resourceUsage.getResultCode());
    }

    @Test
    public void getResourceUsageByWorkload() {
        when(restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos/resourceUsageByWorkload/{chaosName}".replace("{chaosName}", gParams.getName()), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ResourceUsage.class)).thenReturn(gResourceUsage);
        when(commonService.setResultModel(gResourceUsage, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResourceUsage);

        ResourceUsage resourceUsage = experimentsService.getResourceUsageByWorkload(gParams);

//        assertThat(resourceUsage).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, resourceUsage.getResultCode());
    }

    @Test
    public void getResourceUsageByNode() {
        when(restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos/resourceUsageByNode/{chaosName}".replace("{chaosName}", gParams.getName()), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ResourceUsage.class)).thenReturn(gResourceUsage);
        when(commonService.setResultModel(gResourceUsage, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResourceUsage);

        ResourceUsage resourceUsage = experimentsService.getResourceUsageByNode(gParams);

//        assertThat(resourceUsage).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, resourceUsage.getResultCode());

    }
}