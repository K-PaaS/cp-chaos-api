package org.container.platform.chaos.api.chaos;

import lombok.Data;

import java.util.List;

@Data
public class ExperimentsListItems {
        private String kind;
        private Metadata metadata;
        private Status status;

        @Data
        public static class Metadata {
            private String creationTimestamp;
            private String name;
            private String namespace;
            private String uid;
        }

        @Data
        public static class Status {
            private List<Conditions> conditions;
            private Experiment experiment;
        }

        @Data
        public static class Conditions {
            private String status;
            private String type;
        }

        @Data
        public static class Experiment {
            private List<ContainerRecordsListItem> containerRecords;
        }

        @Data
        public static class ContainerRecordsListItem {
            private String phase;
        }
    }


