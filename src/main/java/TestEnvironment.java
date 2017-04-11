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
            members={"requests"},
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testGet() {
        JsonObject json = new JsonObject();
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/get");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println("[TestEnvironment]: response: " + response.getStatusCode() + "\n" + response.getBody());
    }

    @Clustering(
            id="post",
            clusterType= ClusterType.SINGLE_INSTANCE,
            expectedDuration=2,
            members={"requests"},
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testPost() {
        JsonObject json = new JsonObject();
        json.addProperty("method", "post");
        RestApiRequest req = new RestApiRequest(RequestMethod.POST, "https://httpbin.org/post");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println("[TestEnvironment]: response: " + response.getStatusCode() + "\n" + response.getBody());
    }

    @Clustering(
            id="put",
            clusterType= ClusterType.SINGLE_INSTANCE,
            expectedDuration=2,
            members={"requests"},
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testPut() {
        JsonObject json = new JsonObject();
        json.addProperty("method", "put");
        RestApiRequest req = new RestApiRequest(RequestMethod.PUT, "https://httpbin.org/put");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println("[TestEnvironment]: response: " + response.getStatusCode() + "\n" + response.getBody());
    }

    @Clustering(
            id="delete",
            clusterType= ClusterType.SINGLE_INSTANCE,
            expectedDuration=2,
            members={"requests"},
            durationUnit= DurationUnit.SEC,
            expectedTraffic= TrafficLoad.MINOR)
    void testDelete() {
        JsonObject json = new JsonObject();
        json.addProperty("test", "delete");
        RestApiRequest req = new RestApiRequest(RequestMethod.DELETE, "https://httpbin.org/delete");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println("[TestEnvironment]: response: " + response.getStatusCode() + "\n" + response.getBody());
    }
}
