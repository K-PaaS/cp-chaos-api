package org.container.platform.chaos.api.experiments;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;
import java.util.Map;

/**
 * ExperimentsList 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.06.07
 */
@Data
public class ExperimentsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<ExperimentsListItem> items;


}

@Data
class ExperimentsListItem {
    private Map podFaultData;
    private Map networkDelayData;
    private Map stressData;

    @Data
    public class ExperimentsItem {
        private String kind;
        private Metadata metadata;
        private Status status;
    }


    @Data
    public class Metadata {
        private String creationTimestamp;
        private String name;
        private String namespace;
        private String uid;
    }

    @Data
    public class Status {
        private Experiment experiment;
    }

    @Data
    public class Experiment {
        private List<ContainerRecordsListItem> containerRecords;
    }

    @Data
    class ContainerRecordsListItem {
        private String phase;
    }
}