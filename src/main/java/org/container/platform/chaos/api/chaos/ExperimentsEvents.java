package org.container.platform.chaos.api.chaos;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * ExperimentsList 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.06.18
 */

@Data
public class ExperimentsEvents {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;

    private List<ExperimentsItem> items;

    public ExperimentsEvents() {
        this.items = new ArrayList<>();
    }
    
}