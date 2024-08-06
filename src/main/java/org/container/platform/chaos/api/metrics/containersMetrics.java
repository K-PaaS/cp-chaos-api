package org.container.platform.chaos.api.metrics;

import lombok.Data;
import org.container.platform.chaos.api.metrics.custom.Quantity;

import java.util.Map;

@Data
public class containersMetrics {
    private String name;
    private Map<String, Quantity> usage;
}
