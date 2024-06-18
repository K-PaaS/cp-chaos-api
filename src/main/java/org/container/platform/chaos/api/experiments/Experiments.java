package org.container.platform.chaos.api.experiments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

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

//    @JsonIgnore
//    private CommonMetaData metadata;

    private String name;
    private String namespace;
    private String kind;
    private String status;
    private String uid;
    private String creationTimestamp;


}