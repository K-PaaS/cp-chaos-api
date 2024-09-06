package org.container.platform.chaos.api.chaos.model;

import lombok.Data;
import org.container.platform.chaos.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * StressChaos Resources DataList Model 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.14
 **/

@Data
public class StressChaosResourcesDataList {
    private String resultCode;
    private String resultMessage;
    private List<Long> resultList;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private StressChaos stressChaos;
    private List<ChaosResource> chaosResource;

}
