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
    private List<String> resourceName;
    private List<List<Integer>> cpu;
    private List<List<Integer>> memory;
    private List<List<Integer>> appStatus;
}
