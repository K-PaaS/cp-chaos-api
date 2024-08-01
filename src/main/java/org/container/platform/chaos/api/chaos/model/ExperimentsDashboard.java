package org.container.platform.chaos.api.chaos.model;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;

@Data
public class ExperimentsDashboard {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private String status;
    private String uid;




}
