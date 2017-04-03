import java.awt.*;
import java.lang.reflect.Method;

/**
 * <strong>Test application for @Clustering</strong><br>
 * will be removed later
 */
public class AnnotationTest {

    private ProxyRequest<String> proxyRequest = new ProxyRequest<>();

    /**
     * @Clustering for methods
     */

    @Clustering(
            id="get_nodes",
            clusterType=ClusterType.GROUPING,
            members={"nodes"},
            expectedDuration=3,
            durationUnit=DurationUnit.SEC,
            expectedTraffic=TrafficLoad.MINOR)
    void testGetNodes() {
        System.out.println("testGetNodes");
        System.out.println("response: " + proxyRequest.getResponse("GET /nodes"));
    }

    @Clustering(
            id="create_file",
            clusterType=ClusterType.GROUPING,
            members={"nodes", "files"},
            expectedDuration=5,
            durationUnit=DurationUnit.SEC,
            expectedTraffic=TrafficLoad.MINOR)
    void testCreateFile() {
        System.out.println("testCreateFile");
        System.out.println("response: " + proxyRequest.getResponse("POST /nodes/files"));
    }

    @Clustering(
            id="file_upload",
            clusterType=ClusterType.GROUPING,
            members={"nodes", "files", "uploads"},
            expectedDuration=10,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MAJOR)
    void testUploadFile() {
        System.out.println("testUploadFile");
        System.out.println("response: " + proxyRequest.getResponse("PUT /nodes/files/uploads"));
    }

    @Clustering(
            id="get_config",
            clusterType=ClusterType.SINGLE_INSTANCE,
            members={"config"},
            expectedDuration=1,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MINOR)
    void testGetConfig() {
        System.out.println("testGetConfig");
        System.out.println("response: " + proxyRequest.getResponse("GET /config"));
    }

    /**
     * @Clustering for classes
     */

    @Clustering(
            id="groups",
            clusterType=ClusterType.GROUPING,
            members={"groups"},
            expectedDuration=5,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MINOR
    )
    public static class TestGroups {
        void setup() { System.out.println("setup"); }
        void teardown() { System.out.println("teardown"); }
        void testGetGroup() { System.out.println("testGetGroup"); }
        void testCreateGroup() { System.out.println("testCreateGroup"); }
        void testDeleteGroup() { System.out.println("testDeleteGroup"); }
    }
}
