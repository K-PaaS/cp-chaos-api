package org.container.platform.chaos.api.chaos;

import org.container.platform.chaos.api.chaos.model.ExperimentsList;
import org.container.platform.chaos.api.common.CommonService;
import org.container.platform.chaos.api.common.Constants;
import org.container.platform.chaos.api.common.PropertyService;
import org.container.platform.chaos.api.common.RestTemplateService;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.workloads.pods.PodsList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
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
    private static final String ALL_NAMESPACE = "all";

    private static HashMap gResultMap;

    private static ExperimentsList gExperimentsListModel;
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
        gResultMap = new HashMap();

        gParams = new Params();
        gParams.setCluster(CLUSTER);
        gParams.setNamespace(NAMESPACE);


        gExperimentsListModel = new ExperimentsList();
        gExperimentsListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);


    }

    @Test
    public void getExperimentsList() {

        ExperimentsList podFaultExperimentsList = new ExperimentsList();
        gParams.setKind("PodChaos");
        when(propertyService.getCpMasterApiChaosPodFaultsPodKillListUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/podchaos");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/podchaos", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsList.class)).thenReturn(podFaultExperimentsList);
        gExperimentsListModel.setItems(podFaultExperimentsList.getItems());

        ExperimentsList networkDelayExperimentsList = new ExperimentsList();
        gParams.setKind("NetworkChaos");
        when(propertyService.getCpMasterApiChaosNetworkFaultsDelayListUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/networkchaos");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/networkchaos", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsList.class)).thenReturn(networkDelayExperimentsList);
        gExperimentsListModel.setItems(networkDelayExperimentsList.getItems());

        ExperimentsList stressExperimentsList = new ExperimentsList();
        gParams.setKind("StressChaos");
        when(propertyService.getCpMasterApiChaosStressScenariosListUrl()).thenReturn("/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/apis/chaos-mesh.org/v1alpha1/namespaces/{namespace}/stresschaos", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ExperimentsList.class)).thenReturn(stressExperimentsList);
        gExperimentsListModel.setItems(stressExperimentsList.getItems());

        when(commonService.resourceListProcessing(gExperimentsListModel, gParams, ExperimentsList.class)).thenReturn(gExperimentsListModel);
        when(commonService.setResultModel(gExperimentsListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gExperimentsListModel);

        ExperimentsList resultList = experimentsService.getExperimentsList(gParams);

        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());

    }

    @Test
    public void getExperiments() {
    }

    @Test
    public void getExperimentsStatusList() {
    }

    @Test
    public void getExperimentsStatus() {
    }

    @Test
    public void createExperiments() {
    }

    @Test
    public void deleteExperiments() {
    }

    @Test
    public void deleteStressChaos() {
    }

    @Test
    public void getExperimentsEventsList() {
    }

    @Test
    public void createStressChaosData() {
    }

    @Test
    public void getStressChaos() {
    }

    @Test
    public void setStressChaos() {
    }

    @Test
    public void calculateEndTime() {
    }

    @Test
    public void getResourceUsageByPod() {
    }

    @Test
    public void getResourceUsageByWorkload() {
    }

    @Test
    public void getResourceUsageByNode() {
    }
}