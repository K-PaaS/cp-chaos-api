package org.container.platform.chaos.api.chaos.model;

import lombok.Data;

@Data
public class ExperimentsListItems {
    private String kind;
    private Metadata metadata;

    @Data
    public static class Metadata {
        private String creationTimestamp;
        private String name;
        private String namespace;
        private String uid;
    }

}


