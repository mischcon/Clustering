import clustering.Clustering;
import com.google.gson.JsonObject;
import clustering.ClusteringTask;
import communication.*;

/**
 * <strong>Contains test tasks for the cluster</strong>
 */
public class Tests implements ClusteringTask {

    @Clustering(id="get", members={"requests"}) public void testGet() {
        JsonObject json = new JsonObject();
        json.addProperty("method", "get");
        RestApiRequest req =
                new RestApiRequest(RequestMethod.GET, "https://sds.ssp-europe.eu:443/api/v4/auth/resources");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id="post", members={"requests"}) public void testPost() {
        JsonObject json = new JsonObject();
        json.addProperty("method", "post");
        RestApiRequest req = new RestApiRequest(RequestMethod.POST, "https://httpbin.org/post");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id="put", members={"requests"}) public void testPut() {
        JsonObject json = new JsonObject();
        json.addProperty("method", "put");
        RestApiRequest req = new RestApiRequest(RequestMethod.PUT, "https://httpbin.org/put");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id="delete", members={"requests"}) public void testDelete() {
        JsonObject json = new JsonObject();
        json.addProperty("test", "delete");
        RestApiRequest req = new RestApiRequest(RequestMethod.DELETE, "https://httpbin.org/delete");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }
}
