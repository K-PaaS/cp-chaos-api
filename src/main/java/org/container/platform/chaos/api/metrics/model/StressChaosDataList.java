package org.container.platform.chaos.api.metrics.model;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * StressChaosList Model 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.14
 **/

@Data
public class StressChaosDataList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private StressChaos stressChaos;
    private List<ChaosResource> chaosResource;

}
