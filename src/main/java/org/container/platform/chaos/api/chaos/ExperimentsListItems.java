package org.container.platform.chaos.api.chaos;

import lombok.Data;

import java.util.List;

@Data
public class ExperimentsListItems {
        private String kind;
        private Metadata metadata;
        private Status status;

        @Data
        public class Metadata {
            private String creationTimestamp;
            private String name;
            private String namespace;
            private String uid;
        }

        @Data
        public class Status {
            private List<Conditions> conditions;
            private Experiment experiment;
        }

        @Data
        public class Conditions {
            private String status;
            private String type;
        }

        @Data
        public class Experiment {
            private List<ContainerRecordsListItem> containerRecords;
        }

        @Data
        public class ContainerRecordsListItem {
            private String phase;
        }
    }


