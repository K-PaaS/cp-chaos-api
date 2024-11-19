package org.container.platform.chaos.api.workloads.replicaSets;

import lombok.Data;
import org.container.platform.chaos.api.common.model.*;

import java.util.List;
import java.util.Map;

/**
 * ReplicaSetsList 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-11-19
 */
@Data
public class ReplicaSetsList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<ReplicaSetsListItem> items;

}

