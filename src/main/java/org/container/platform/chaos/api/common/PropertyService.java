package org.container.platform.chaos.api.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
public class PropertyService {

    @Value("${cpChaos.api.access}")
    private String cpChaosApiAccessUrl;

    @Value("${cpChaos.api.url: }")
    private String cpChaosApiUrl;

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

    @Value("${cpChaos.api.list.event.list}")
    private String cpChaosApiListEventListUrl;
}
