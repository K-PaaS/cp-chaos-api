package org.container.platform.chaos.api.workloads.pods.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.chaos.api.common.Constants;
import org.container.platform.chaos.api.common.model.CommonMetaData;
import org.container.platform.chaos.api.common.model.CommonSpec;
import java.util.Map;

@Data
public class PodsListItem {
    private String name;
    private String namespace;
    private Object labels;
    private String nodes;
    private String podStatus;
    private Integer restarts;
    private String creationTimestamp;
    private String phase;
    private String containerStatus;

    private Map<String, Object> cpu = Constants.INIT_USAGE;
    private Map<String, Object> memory = Constants.INIT_USAGE;


    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private PodsStatus status;


    public String getName() {
        return metadata.getName();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

}
