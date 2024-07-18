package org.container.platform.chaos.api.chaos;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;
import org.springframework.http.HttpStatus;

import java.util.List;


@Data
public class ExperimentsEventsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<ExperimentsEventsListItems> items;
}
