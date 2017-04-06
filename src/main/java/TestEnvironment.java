import clustering.ClusterType;
import clustering.Clustering;
import clustering.DurationUnit;
import clustering.TrafficLoad;
import com.google.gson.JsonObject;
import clustering.ClusteringTask;
import communication.*;

public class TestEnvironment implements ClusteringTask {

    /**
     * @Clustering for methods
     */

    @Clustering(
            id="get",
            clusterType= ClusterType.SINGLE_INSTANCE,
            expectedDuration=2,
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testGet() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        JsonObject json = new JsonObject();
        json.addProperty("test", "get");
        HttpResponse response = (HttpResponse) request.getResponse(
                new GetRequest("https://httpbin.org/get")
                        .addParam("param_1", "1")
                        .addBody(json)
                        .addHeader("Content-Type", "application/json"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
    }

    @Clustering(
            id="utf-8",
            clusterType= ClusterType.SINGLE_INSTANCE,
            expectedDuration=2,
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testUTF8() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        HttpResponse response = (HttpResponse) request.getResponse(
                new GetRequest("https://httpbin.org/encoding/utf8"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
    }

    @Clustering(
            id="status_code",
            clusterType= ClusterType.SINGLE_INSTANCE,
            expectedDuration=2,
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testStatusCode() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        HttpResponse response = (HttpResponse) request.getResponse(
                new GetRequest("https://httpbin.org/status/201"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
        response = (HttpResponse) request.getResponse(
                new GetRequest("https://httpbin.org/status/204"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
        response = (HttpResponse) request.getResponse(
                new GetRequest("https://httpbin.org/status/400"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
        response = (HttpResponse) request.getResponse(
                new GetRequest("https://httpbin.org/status/418"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
        response = (HttpResponse) request.getResponse(
                new GetRequest("https://httpbin.org/status/500"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
    }

    @Clustering(
            id="post",
            clusterType= ClusterType.SINGLE_INSTANCE,
            expectedDuration=2,
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testPost() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        JsonObject json = new JsonObject();
        json.addProperty("test", "post");
        HttpResponse response = (HttpResponse) request.getResponse(
                new PostRequest("https://httpbin.org/post")
                        .addParam("param_1", "1")
                        .addBody(json)
                        .addHeader("Content-Type", "application/json"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
    }

    @Clustering(
            id="put",
            clusterType= ClusterType.SINGLE_INSTANCE,
            expectedDuration=2,
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testPut() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        JsonObject json = new JsonObject();
        json.addProperty("test", "put");
        HttpResponse response = (HttpResponse) request.getResponse(
                new PutRequest("https://httpbin.org/put")
                        .addParam("param_1", "1")
                        .addBody(json)
                        .addHeader("Content-Type", "application/json"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
    }

    @Clustering(
            id="delete",
            clusterType= ClusterType.SINGLE_INSTANCE,
            members={},
            expectedDuration=2,
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testDelete() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        JsonObject json = new JsonObject();
        json.addProperty("test", "delete");
        HttpResponse response = (HttpResponse) request.getResponse(
                new DeleteRequest("https://httpbin.org/delete")
                        .addParam("param_1", "1")
                        .addBody(json)
                        .addHeader("Content-Type", "application/json"));
        System.out.println("response: " + response.getStatusCode() + "\n" + response.getBody());
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
