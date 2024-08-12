package org.container.platform.chaos.api.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Property Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Service
@Data
public class PropertyService {

    @Value("${chaos.url: }")
    private String cpChaosApiUrl;

    @Value("${commonApi.url}")
    private String commonApiUrl;


    //podFaults
    @Value("${cpMaster.api.chaos.podFaults.podKill.list}")
    private String cpMasterApiChaosPodFaultsPodKillListUrl;

    @Value("${cpMaster.api.chaos.podFaults.podKill.get}")
    private String cpMasterApiChaosPodFaultsPodKillGetUrl;

    @Value("${cpMaster.api.chaos.podFaults.podKill.create}")
    private String cpMasterApiChaosPodFaultsPodKillCreateUrl;

    @Value("${cpMaster.api.chaos.podFaults.podKill.delete}")
    private String cpMasterApiChaosPodFaultsPodKillDeleteUrl;

    //networkFaults
    @Value("${cpMaster.api.chaos.networkFaults.delay.list}")
    private String cpMasterApiChaosNetworkFaultsDelayListUrl;

    @Value("${cpMaster.api.chaos.networkFaults.delay.get}")
    private String cpMasterApiChaosNetworkFaultsDelayGetUrl;

    @Value("${cpMaster.api.chaos.networkFaults.delay.create}")
    private String cpMasterApiChaosNetworkFaultsDelayCreateUrl;

    @Value("${cpMaster.api.chaos.networkFaults.delay.delete}")
    private String cpMasterApiChaosNetworkFaultsDelayDeleteUrl;

    //stressScenarios
    @Value("${cpMaster.api.chaos.stressScenarios.list}")
    private String cpMasterApiChaosStressScenariosListUrl;

    @Value("${cpMaster.api.chaos.stressScenarios.get}")
    private String cpMasterApiChaosStressScenariosGetUrl;

    @Value("${cpMaster.api.chaos.stressScenarios.create}")
    private String cpMasterApiChaosStressScenariosCreateUrl;

    @Value("${cpMaster.api.chaos.stressScenarios.delete}")
    private String cpMasterApiChaosStressScenariosDeleteUrl;

    // chaos - experiments(status)
    @Value("${chaos.api.experiment.list}")
    private String chaosApiExperimentListUrl;
    @Value("${chaos.api.experiment.get}")
    private String chaosApiExperimentGetUrl;

    // chaos - events
    @Value("${chaos.api.event.list}")
    private String chaosApiEventListUrl;


    @Value("${cpResource.clusterResource}")
    private String adminResource;

    @Value("${cpNamespace.defaultNamespace}")
    private String defaultNamespace;


    @Value("${cpNamespace.clusterAdminNamespace}")
    private String clusterAdminNamespace;


    @Value("${cpAnnotations.configuration}")
    List<String> cpAnnotationsConfiguration;

    @Value("${cpAnnotations.last-applied}")
    String cpAnnotationsLastApplied;

    //rolebinding

    @Value("${cpMaster.api.list.roleBindings.create}")
    private String cpMasterApiListRoleBindingsCreateUrl;

    @Value("${cpMaster.api.list.roleBindings.delete}")
    private String cpMasterApiListRoleBindingsDeleteUrl;

    @Value("${vault.path.base}")
    private String vaultBase;

    @Value("${vault.path.cluster-token}")
    private String vaultClusterTokenPath;

    @Value("${vault.path.super-admin-token}")
    private String vaultSuperAdminTokenPath;

    @Value("${vault.path.user-token}")
    private String vaultUserTokenPath;

    // metrics api
    @Value("${cpMaster.api.metrics.node.list}")
    private String cpMasterApiMetricsNodesListUrl;

    @Value("${cpMaster.api.metrics.node.get}")
    private String cpMasterApiMetricsNodesGetUrl;

    @Value("${cpMaster.api.metrics.pod.list}")
    private String cpMasterApiMetricsPodsListUrl;

    @Value("${cpMaster.api.metrics.pod.get}")
    private String cpMasterApiMetricsPodsGetUrl;

    //pod
    @Value("${cpMaster.api.list.pods.get}")
    private String cpMasterApiListPodsGetUrl;
}