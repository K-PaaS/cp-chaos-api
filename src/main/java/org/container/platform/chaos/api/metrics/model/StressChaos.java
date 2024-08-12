package org.container.platform.chaos.api.metrics.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

import java.util.List;


/**
 * Chaos Model 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.09
 **/

@Data
public class StressChaos {

    private long chaosId;
    private String chaosName;
    private String namespaces;
    private String creationTime;
    private String endTime;
    private String duration;
}
