package org.container.platform.chaos.api.chaos;

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

    private Metadata metadata;
    private String creationTimestamp;

    @Data
    public static class Metadata {
        private String creationTimestamp;
        private String name;
        private String namespace;
    }

    public String getCreationTimestamp() {
        if (metadata != null) {
            return metadata.getCreationTimestamp();
        }
        return null;
    }

    public String getMetadataName() {
        if (metadata != null) {
            return metadata.getName();
        }
        return null;
    }
    public String getMetadataNamespace() {
        if (metadata != null) {
            return metadata.getNamespace();
        }
        return null;
    }

}
