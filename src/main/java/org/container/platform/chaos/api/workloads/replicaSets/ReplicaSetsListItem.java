package org.container.platform.chaos.api.workloads.replicaSets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.chaos.api.common.CommonUtils;
import org.container.platform.chaos.api.common.model.CommonContainer;
import org.container.platform.chaos.api.common.model.CommonMetaData;
import org.container.platform.chaos.api.common.model.CommonSpec;
import org.container.platform.chaos.api.common.model.CommonStatus;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReplicaSetsListItem {
    private String name;
    private String namespace;
    private int runningPods;
    private int totalPods;
    private List<String> images;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return metadata.getName();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public int getRunningPods() {
        return status.getAvailableReplicas();
    }

    public int getTotalPods() {
        return status.getReplicas();
    }

    public Object getImages() {
        List<String> images = new ArrayList<>();
        List<CommonContainer> containers = new ArrayList<CommonContainer>();
        try {
            containers = spec.getTemplate().getSpec().getContainers();
            for (CommonContainer c : containers) {
                images.add(CommonUtils.procReplaceNullValue(c.getImage()));
            }
        } catch (Exception e) {
            return CommonUtils.procReplaceNullValue(images);
        }
        return CommonUtils.procReplaceNullValue(images);
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }
}
