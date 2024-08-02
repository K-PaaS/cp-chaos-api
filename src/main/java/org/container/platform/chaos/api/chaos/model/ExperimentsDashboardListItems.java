package org.container.platform.chaos.api.chaos.model;

import lombok.Data;

@Data
public class ExperimentsDashboardListItems {
    private String namespace;
    private String name;
    private String kind;
    private String uid;
    private String created_at;
    private String status;
}
