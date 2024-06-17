package org.container.platform.chaos.api.experiments;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;

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
    private CommonItemMetaData itemMetaData;
    private List<ExperimentsListItem> items;
}

@Data
class ExperimentsListItem {
    private String name;
    private String namespace;
    private String kind;
    private String status;
    private String resourceUid;
    private String creationTimestamp;

}
