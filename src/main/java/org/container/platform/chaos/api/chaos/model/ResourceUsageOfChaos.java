package org.container.platform.chaos.api.chaos.model;

import lombok.Data;

@Data
public class ResourceUsageOfChaos {
    private long id;
    private String uid;
    private String targetPod;
    private float cpuRatio;
    private float memRatio;
    private long appStatus;
    private String measurementTime;

}
