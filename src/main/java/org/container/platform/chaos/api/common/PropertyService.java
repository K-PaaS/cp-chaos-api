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

    @Value("${cpChaos.api.access}")
    private String cpChaosApiAccessUrl;

    @Value("${cpChaos.api.url: }")
    private String cpChaosApiUrl;

    @Value("${cpChaos.api.list.event.url: }")
    private String cpChaosEventApiUrl;


    //podFaults
    @Value("${cpChaos.api.list.podFaults.podKill.list}")
    private String cpChaosApiListPodFaultsPodKillListUrl;

    @Value("${cpChaos.api.list.podFaults.podKill.get}")
    private String cpChaosApiListPodFaultsPodKillGetUrl;

    @Value("${cpChaos.api.list.podFaults.podKill.create}")
    private String cpChaosApiListPodFaultsPodKillCreateUrl;

    @Value("${cpChaos.api.list.podFaults.podKill.delete}")
    private String cpChaosApiListPodFaultsPodKillDeleteUrl;

    //networkFaults
    @Value("${cpChaos.api.list.networkFaults.delay.list}")
    private String cpChaosApiListNetworkFaultsDelayListUrl;

    @Value("${cpChaos.api.list.networkFaults.delay.get}")
    private String cpChaosApiListNetworkFaultsDelayGetUrl;

    @Value("${cpChaos.api.list.networkFaults.delay.create}")
    private String cpChaosApiListNetworkFaultsDelayCreateUrl;

    @Value("${cpChaos.api.list.networkFaults.delay.delete}")
    private String cpChaosApiListNetworkFaultsDelayDeleteUrl;

    //stressScenarios
    @Value("${cpChaos.api.list.stressScenarios.list}")
    private String cpChaosApiListStressScenariosListUrl;

    @Value("${cpChaos.api.list.stressScenarios.get}")
    private String cpChaosApiListStressScenariosGetUrl;

    @Value("${cpChaos.api.list.stressScenarios.create}")
    private String cpChaosApiListStressScenariosCreateUrl;

    @Value("${cpChaos.api.list.stressScenarios.delete}")
    private String cpChaosApiListStressScenariosDeleteUrl;

    // events
    @Value("${cpChaos.api.list.event.list}")
    private String cpChaosApiListEventListUrl;
    @Value("${cpChaos.api.list.event.get}")
    private String cpChaosApiListEventGetUrl;



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


}