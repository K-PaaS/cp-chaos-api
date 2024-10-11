package org.container.platform.chaos.api.chaos.model;

import lombok.Data;

import java.util.List;

/**
 * ResourceUsageItem 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-10-11
 */
@Data
public class ResourceUsageItem {
    private List<String> time;
    private List<String> podName;
    private List<String> cpu;
    private List<String> memory;
    private List<Integer> appStatus;
}
