package org.container.platform.chaos.api.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
public class PropertyService {

    @Value("${cpChaos.api.access}")
    private String cpChaosApiAccessUrl;

    //podchaos
    @Value("${cpChaos.api.list.podFaults.podKill.list}")
    private String cpChaosApiListPodFaultsPodKillListUrl;
}
