package org.container.platform.chaos.api.experiments;

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
public class Experiments {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;

    private List<ExperimentsItem> items;

    public Experiments() {
        this.items = new ArrayList<>();
    }

    public void addItem(ExperimentsItem item) {
        this.items.add(item);
    }

}