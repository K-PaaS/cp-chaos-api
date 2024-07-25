package org.container.platform.chaos.api.common;
import org.springframework.http.MediaType;
import java.util.*;

/**
 * Constants 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
public class Constants {

    public static final String RESULT_STATUS_SUCCESS = "SUCCESS";
    public static final String RESULT_STATUS_FAIL = "FAIL";

    public static final String CHECK_Y = "Y";
    public static final String CHECK_N = "N";

    public static final String CHECK_TRUE = "true";

    public static final String EMPTY_STRING ="";

    public static final String TARGET_CP_MASTER_API = "cpMasterApi/{cluster}";
    public static final String TARGET_COMMON_API = "commonApi";
    //FIXME!!
    public static final String TARGET_CHAOS_API = "chaosAPI";
    public static final String TARGET_CHAOS_DASHBOARD_API = "chaosDashboardAPI";

    public static final String TARGET_CHAOS_EVENT_API = "chaosEventAPI";

    public static final String CLUSTER_TYPE_SUB = "sub";

    public static final String AUTH_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String AUTH_CLUSTER_ADMIN = "CLUSTER_ADMIN";
    public static final String AUTH_NAMESPACE_ADMIN = "NAMESPACE_ADMIN";
    public static final String AUTH_USER = "USER";


    public static final String ALL_NAMESPACES = "all";
    public static final String DEFAULT_CLUSTER_ADMIN_ROLE = "cluster-admin"; // k8s default cluster role's name
    public static final String NOT_ALLOWED_POD_NAME_NODES = "nodes";
    public static final String NOT_ALLOWED_POD_NAME_RESOURCES= "resources";

    public static final String SUPPORTED_RESOURCE_STORAGE = "storage";


    public static final String CHAOS_MESH_KIND_POD_CHAOS = "PodChaos";
    public static final String CHAOS_MESH_KIND_NETWORK_CHAOS = "NetworkChaos";
    public static final String CHAOS_MESH_KIND_STRESS_CHAOS = "StressChaos";
    public static final String CHAOS_MESH_LABEL_SELECTOR = "    labelSelectors:" + "\r\n";
    public static final String CHAOS_MESH_NAMESPACES = "    namespaces:" + "\r\n";
    public static final String CHAOS_MESH_PODS = "    pods:" + "\r\n";
    public static final String CHAOS_MESH_STRESSORS = "stressors";
    public static final String CHAOS_MESH_STRESSORS_CPU = "cpu";
    public static final String CHAOS_MESH_STRESSORS_MEMORY = "memory";
    public static final String NEW_LINE = "\r\n";



    public static final List<String> NOT_ALLOWED_POD_NAME_LIST = Collections.unmodifiableList(new ArrayList<String>(){
        {
            add(NOT_ALLOWED_POD_NAME_NODES);
            add(NOT_ALLOWED_POD_NAME_RESOURCES);
        }
    });


    static final String STRING_DATE_TYPE = "yyyy-MM-dd HH:mm:ss";
    static final String STRING_ORIGINAL_DATE_TYPE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    static final String STRING_TIME_ZONE_ID = "Asia/Seoul";

    static final String ACCEPT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;

    public static final String URI_SIGN_UP = "/signUp";
    public static final String URI_LOGIN = "/login";


    public static final String URI_CHECK_REGISTERED_USER = "/check/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userId:.+}";

    public static final String[] PERMIT_PATH_LIST = new String[]{ URI_SIGN_UP, URI_LOGIN, URI_CHECK_REGISTERED_USER};

    public static final String ENDS_WITH_SES = "ses";
    public static final String ENDS_WITH_S = "s";


    public static final String URI_COMMON_API_USERS_LIST_BY_CLUSTER = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/usersList";


    public static final String URI_COMMON_API_USER_DETAIL_LOGIN =  "/login/users/{userId:.+}";
    public static final String URI_COMMON_API_USERS_LIST_BY_NAMESPACE = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users";
    public static final String URI_COMMON_API_USER_DELETE = "/users/";
    public static final String URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_USER_AUTH_ID = "/clusters/{cluster:.+}/users/{userAuthId:.+}";

    public static final String URI_COMMON_API_CLUSTER_ADMIN_LIST = "/cluster/{cluster:.+}/admin?searchName={searchName:.+}";
    public static final String URI_COMMON_API_CLUSTER_USER_DETAILS = "/clusters/{cluster:.+}/users/{userAuthId:.+}/details";
    public static final String URI_COMMON_API_CLUSTER_INFO_USER_DETAILS = "/cluster/info/all/user/details?userAuthId={userAuthId:.+}&cluster={cluster:.+}&userType={userType:.+}&namespace={namespace:.+}";
    public static final String URI_COMMON_API_CLUSTER_LIST_BY_USER = "/users/{userAuthId:.+}/clustersList?userType={userType:.+}";
    public static final String URI_COMMON_API_DELETE_USER = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userAuthId:.+}/{userType:.+}";
    public static final String URI_COMMON_API_CLUSTER_AND_NAMESPACE_LIST_BY_USER = "/users/{userAuthId:.+}/clustersAndNamespacesList?userType={userType:.+}";




    /** 서비스 요청시 처리 메소드 kind 매핑 정보 */
    public static final String RESOURCE_ENDPOINTS = "Endpoints";
    public static final String RESOURCE_EVENTS = "Events";

    //cluster
    public static final String RESOURCE_NAMESPACE = "Namespace";
    public static final String RESOURCE_NODE = "Node";

    //workload
    public static final String RESOURCE_DEPLOYMENT = "Deployment";
    public static final String RESOURCE_POD = "Pod";
    public static final String RESOURCE_REPLICASET = "ReplicaSet";

    //storage
    public static final String RESOURCE_PERSISTENTVOLUMECLAIM = "PersistentVolumeClaim";
    public static final String RESOURCE_PERSISTENTVOLUME = "PersistentVolume";
    public static final String RESOURCE_STORAGECLASS = "StorageClass";

    //management
    public static final String RESOURCE_LIMITRANGE = "LimitRange";
    public static final String RESOURCE_RESOURCEQUOTA = "ResourceQuota";
    public static final String RESOURCE_ROLE = "Role";

    public static final String RESOURCE_NAME = "name";
    public static final String RESOURCE_CREATIONTIMESTAMP = "creationTimestamp";
    public static final String RESOURCE_CREATED_AT = "created_at";

    public static final String RESOURCE_CREATED = "created";
    public static final String RESOURCE_METADATA = "metadata";
    public static final String RESOURCE_NS = "namespace";
    public static final String RESOURCE_ANNOTATIONS = "annotations";




    public static final String noName = "[-]";
    public static final String NULL_REPLACE_TEXT = "-";



    public static final String PARAM_QUERY_FIRST ="?" ;

    public static final String U_LANG_KO = "ko";
    public static final String U_LANG_KO_START_WITH = "ko_";
    public static final String U_LANG_ENG = "en";


    /** 서비스 클래스의 Package */
    public static final String SERVICE_PACKAGE = "org.container.platform.chaos.api.";

    public static final Map<String, String> RESOURCE_SERVICE_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put(RESOURCE_ENDPOINTS, SERVICE_PACKAGE + "endpoints:EndpointsService");     // Endpoints 서비스
            put(RESOURCE_EVENTS, SERVICE_PACKAGE + "events:EventsService");     // Endpoints 서비스
            put(RESOURCE_NAMESPACE, SERVICE_PACKAGE + "clusters.namespaces:NamespacesService");     // Namespace 서비스
            put(RESOURCE_NODE, SERVICE_PACKAGE + "clusters.nodes:NodesService");     // Node 서비스
            put(RESOURCE_DEPLOYMENT, SERVICE_PACKAGE + "workloads.deployments:DeploymentsService");     // Deployment 서비스
            put(RESOURCE_POD, SERVICE_PACKAGE + "workloads.pods:PodsService");     // Pod 서비스
            put(RESOURCE_REPLICASET, SERVICE_PACKAGE + "workloads.pods:ReplicaSetsService");     // ReplicaSet 서비스
            put(RESOURCE_PERSISTENTVOLUMECLAIM, SERVICE_PACKAGE + "storages.persistentVolumeClaims:PersistentVolumeClaimsService");     // PersistentVolumeClaim 서비스
            put(RESOURCE_PERSISTENTVOLUME, SERVICE_PACKAGE + "storages.persistentVolumes:PersistentVolumesService");     // PersistentVolume 서비스
            put(RESOURCE_STORAGECLASS, SERVICE_PACKAGE + "storages.storageClasses:StorageClassesService");     // StorageClass 서비스
            put(RESOURCE_RESOURCEQUOTA, SERVICE_PACKAGE + "clusters.resourceQuotas:ResourceQuotasService");     // ResourceQuota 서비스
            put(RESOURCE_LIMITRANGE, SERVICE_PACKAGE + "clusters.limitRanges:LimitRangesService");     // LimitRange 서비스
            put(RESOURCE_ROLE, SERVICE_PACKAGE + "roles:RolesService"); // Role 서비스
        }

    });


    public Constants() {
        throw new IllegalStateException();
    }

    /**
     * The enum List object type
     */
    public enum ListObjectType {
        LIMIT_RANGES_ITEM,
        COMMON_OWNER_REFERENCES,
        STRING
    }



    public enum ContextType {
        CLUSTER,
        NAMESPACE
    }

    public enum ClusterStatus {
        ACTIVE("A"),
        CREATING("C"),
        DISABLED("D");

        private final String initial;
        ClusterStatus(String initial) {
            this.initial = initial;
        }
        public String getInitial() {
            return initial;
        }

    }

    public static final String CLUSTER_ROLE_BINDING_NAME = "-cluster-admin-binding";
    public static final String SA_TOKEN_NAME= "{username}-token";


    public static final String USAGE = "usage";
    public static final String PERCENT = "percent";


}