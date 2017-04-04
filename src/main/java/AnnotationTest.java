import clustering.ClusterType;
import clustering.Clustering;
import clustering.DurationUnit;
import clustering.TrafficLoad;
import communication.ClusteringTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <strong>Test application for @clustering.Clustering</strong><br>
 * will be removed later
 */
public class AnnotationTest implements ClusteringTask {

    /**
     * @Clustering for methods
     */

    @Clustering(
            id="get",
            clusterType= ClusterType.SINGLE_INSTANCE,
            members={},
            expectedDuration=3,
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testGet() throws IOException {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());

        String url = "https://httpbin.org/get";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "CLUSTER");

        System.out.println("response: " + request.getResponse(connection));
    }

    @Clustering(
            id="create_file",
            clusterType=ClusterType.GROUPING,
            members={"nodes", "files"},
            expectedDuration=5,
            durationUnit=DurationUnit.SEC,
            expectedTraffic=TrafficLoad.MINOR)
    void testCreateFile() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        System.out.println("response: " + request.getResponse("POST /nodes/files"));
    }

    @Clustering(
            id="file_upload",
            clusterType=ClusterType.GROUPING,
            members={"nodes", "files", "uploads"},
            expectedDuration=10,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MAJOR)
    void testUploadFile() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        System.out.println("response: " + request.getResponse("PUT /nodes/files/uploads"));
    }

    @Clustering(
            id="get_config",
            clusterType=ClusterType.SINGLE_INSTANCE,
            members={"config"},
            expectedDuration=1,
            durationUnit=DurationUnit.MIN,
            expectedTraffic=TrafficLoad.MINOR)
    void testGetConfig() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        System.out.println("response: " + request.getResponse("GET /config"));
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
