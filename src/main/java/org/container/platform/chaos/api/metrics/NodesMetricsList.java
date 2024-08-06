package org.container.platform.chaos.api.metrics;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;


@Data
public class NodesMetricsList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<NodesMetricsItems> items;
}
