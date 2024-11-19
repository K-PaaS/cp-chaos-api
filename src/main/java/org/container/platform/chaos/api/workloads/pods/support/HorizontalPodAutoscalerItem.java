package org.container.platform.chaos.api.workloads.pods.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonMetaData;

/**
 * HorizontalPodAutoscalerItem 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-11-19
 */
@Data
public class HorizontalPodAutoscalerItem {
    @JsonIgnore
    private CommonMetaData metadata;
    @JsonIgnore
    private Spec spec;

    @Data
    public class Spec {
        private ScaleTargetRef scaleTargetRef;
    }

    @Data
    public class ScaleTargetRef {
        private String kind;
        private String name;
        private String apiVersion;
    }

}

