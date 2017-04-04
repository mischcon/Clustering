import clustering.ClusterType;
import clustering.Clustering;
import clustering.DurationUnit;
import clustering.TrafficLoad;
import communication.ClusteringTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * <strong>Test application for @Clustering</strong><br>
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
    void testGet() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        try {
            URL obj = new URL("https://httpbin.org/get");
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "CLUSTER");
            System.out.println("response: " + request.getResponse(connection));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Clustering(
            id="get",
            clusterType= ClusterType.SINGLE_INSTANCE,
            members={},
            expectedDuration=3,
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testPost() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        try {
            URL obj = new URL("https://httpbin.org/post");
            String urlParameters = "param1=a&param2=b&param3=c";
            byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;

            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "CLUSTER");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write( postData );
            }
            System.out.println("response: " + request.getResponse(connection));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
