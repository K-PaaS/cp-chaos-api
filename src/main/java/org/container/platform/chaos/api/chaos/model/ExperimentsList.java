package org.container.platform.chaos.api.chaos.model;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;
import java.util.List;


@Data
public class ExperimentsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<ExperimentsListItems> items;
}




