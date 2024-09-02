package org.container.platform.chaos.api.chaos.model;

import lombok.Data;

@Data
public class ChaosResourceUsage {
    private long resourceId;
    private String measurementTime;
    private String cpu;
    private String memory;
    private String appStatus;
}
