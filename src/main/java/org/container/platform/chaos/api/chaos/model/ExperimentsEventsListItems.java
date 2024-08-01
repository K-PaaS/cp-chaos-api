package org.container.platform.chaos.api.chaos.model;

import lombok.Data;
import org.container.platform.chaos.api.common.Constants;

import java.time.Instant;
import java.util.List;

@Data
public class ExperimentsEventsListItems {
    private String id;
    private String object_id;
    private String created_at;
    private String namespace;
    private String name;
    private String kind;
    private String type;
    private String reason;
    private String message;



}
