package org.container.platform.chaos.api.chaos;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;

@Data
public class ExperimentsDashboardList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<ExperimentsStatusListItems> items;
}
