import java.lang.reflect.Method;

/**
 * <strong>Test application for @Clustering</strong><br>
 * will be removed later
 */
public class AnnotationTest {

    HttpRequest httpRequest = new HttpRequest();

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public Method getTestMethod() throws NoSuchMethodException {
        try {
            httpRequest.getResponse("GET", "http://httpbin.org/get");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getClass().getMethod("getTestMethod");
    }

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
        try {
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.getResponse("GET", "http://httpbin.org/get");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("testGetNodes");
    }

    @Clustering(
            id="create_file",
            clusterType=ClusterType.GROUPING,
            members={"nodes", "files"},
            expectedDuration=5,
            durationUnit=DurationUnit.SEC,
            expectedTraffic=TrafficLoad.MINOR)
    void testCreateFile() { System.out.println("testCreateFile"); }

    @Clustering(
            id="file_upload",
            clusterType=ClusterType.GROUPING,
            members={"nodes", "files", "uploads"},
            expectedDuration=10,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MAJOR)
    void testUploadFile() { System.out.println("testUploadFile"); }

    @Clustering(
            id="get_config",
            clusterType=ClusterType.SINGLE_INSTANCE,
            members={"config"},
            expectedDuration=1,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MINOR)
    void testGetConfig() { System.out.println("testGetConfig"); }

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

    public static void main(String[] args) {
        AnnotationTest test = new AnnotationTest();

        test.testGetNodes();
        test.testCreateFile();
        test.testUploadFile();
        test.testGetConfig();

        TestGroups groups = new TestGroups();

        groups.setup();
        groups.testGetGroup();
        groups.teardown();

        groups.setup();
        groups.testCreateGroup();
        groups.teardown();

        groups.setup();
        groups.testDeleteGroup();
        groups.teardown();
    }
}
