package org.container.platform.chaos.api.chaos;

import lombok.Data;



import java.util.List;

@Data
public class ExperimentsItem {
    private Metadata metadata;
    private String kind;

    private Spec spec;
    private Status status;

    @Data
    public class Metadata {
        private String creationTimestamp;
        private String name;
        private String namespace;
        private String uid;
    }

    @Data
    public class Spec {
        private String duration;
        private String action;
        private String gracePeriod;
        private String mode;
        private Selector selector;
        private Object stressors;
        private Delay delay;
    }

    @Data
    public class Delay  {
        private String latency;
    }

    @Data
    public class Stressors {
        private ChaosCPU cpu;
        private ChaosMemory memory;
    }

    @Data
    public class ChaosCPU {
        private String load;
        private String workers;
    }

    @Data
    public class ChaosMemory {
        private String size;
        private String workers;
    }

    @Data
    public class Selector {
        private Object labelSelectors;
        private List namespaces;
        private Object pods;
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
        private List<ContainerRecords> containerRecords;
        private String desiredPhase;
    }

    @Data
    class ContainerRecords {
        private String id;
        private int injectedCount;
        private String phase;
        private int recoveredCount;
        private List<Event> events;
        private String selectorKey;
    }

    @Data
    public static class Event {
        private String operation;
        private String timestamp;
        private String type;
    }
}
