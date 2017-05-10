import clustering.Clustering;
import clustering.ClusteringTask;
import com.google.gson.JsonObject;
import communication.RequestMethod;
import communication.RestApiRequest;
import communication.RestApiResponse;

/**
 * <strong>Contains test tasks for the cluster</strong>
 */
public class Test_long implements ClusteringTask {

    @Clustering(id = "0", members = {"long_run"})
    public void test0() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "1", members = {"long_run"})
    public void test1() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "2", members = {"long_run"})
    public void test2() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "3", members = {"long_run"})
    public void test3() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "4", members = {"long_run"})
    public void test4() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "5", members = {"long_run"})
    public void test5() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "6", members = {"long_run"})
    public void test6() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "7", members = {"long_run"})
    public void test7() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "8", members = {"long_run"})
    public void test8() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "9", members = {"long_run"})
    public void test9() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "10", members = {"long_run"})
    public void test10() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "11", members = {"long_run"})
    public void test11() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "12", members = {"long_run"})
    public void test12() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "13", members = {"long_run"})
    public void test13() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "14", members = {"long_run"})
    public void test14() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "15", members = {"long_run"})
    public void test15() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "16", members = {"long_run"})
    public void test16() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "17", members = {"long_run"})
    public void test17() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "18", members = {"long_run"})
    public void test18() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "19", members = {"long_run"})
    public void test19() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "20", members = {"long_run"})
    public void test20() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "21", members = {"long_run"})
    public void test21() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "22", members = {"long_run"})
    public void test22() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "23", members = {"long_run"})
    public void test23() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "24", members = {"long_run"})
    public void test24() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "25", members = {"long_run"})
    public void test25() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "26", members = {"long_run"})
    public void test26() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "27", members = {"long_run"})
    public void test27() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "28", members = {"long_run"})
    public void test28() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "29", members = {"long_run"})
    public void test29() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "30", members = {"long_run"})
    public void test30() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "31", members = {"long_run"})
    public void test31() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "32", members = {"long_run"})
    public void test32() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "33", members = {"long_run"})
    public void test33() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "34", members = {"long_run"})
    public void test34() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "35", members = {"long_run"})
    public void test35() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "36", members = {"long_run"})
    public void test36() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "37", members = {"long_run"})
    public void test37() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "38", members = {"long_run"})
    public void test38() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "39", members = {"long_run"})
    public void test39() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "40", members = {"long_run"})
    public void test40() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "41", members = {"long_run"})
    public void test41() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "42", members = {"long_run"})
    public void test42() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "43", members = {"long_run"})
    public void test43() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "44", members = {"long_run"})
    public void test44() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "45", members = {"long_run"})
    public void test45() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "46", members = {"long_run"})
    public void test46() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "47", members = {"long_run"})
    public void test47() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "48", members = {"long_run"})
    public void test48() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "49", members = {"long_run"})
    public void test49() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "50", members = {"long_run"})
    public void test50() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "51", members = {"long_run"})
    public void test51() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "52", members = {"long_run"})
    public void test52() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "53", members = {"long_run"})
    public void test53() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "54", members = {"long_run"})
    public void test54() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "55", members = {"long_run"})
    public void test55() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "56", members = {"long_run"})
    public void test56() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "57", members = {"long_run"})
    public void test57() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "58", members = {"long_run"})
    public void test58() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "59", members = {"long_run"})
    public void test59() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "60", members = {"long_run"})
    public void test60() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "61", members = {"long_run"})
    public void test61() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "62", members = {"long_run"})
    public void test62() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "63", members = {"long_run"})
    public void test63() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "64", members = {"long_run"})
    public void test64() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "65", members = {"long_run"})
    public void test65() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "66", members = {"long_run"})
    public void test66() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "67", members = {"long_run"})
    public void test67() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "68", members = {"long_run"})
    public void test68() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "69", members = {"long_run"})
    public void test69() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "70", members = {"long_run"})
    public void test70() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "71", members = {"long_run"})
    public void test71() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "72", members = {"long_run"})
    public void test72() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "73", members = {"long_run"})
    public void test73() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "74", members = {"long_run"})
    public void test74() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "75", members = {"long_run"})
    public void test75() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "76", members = {"long_run"})
    public void test76() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "77", members = {"long_run"})
    public void test77() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "78", members = {"long_run"})
    public void test78() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "79", members = {"long_run"})
    public void test79() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "80", members = {"long_run"})
    public void test80() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "81", members = {"long_run"})
    public void test81() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "82", members = {"long_run"})
    public void test82() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "83", members = {"long_run"})
    public void test83() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "84", members = {"long_run"})
    public void test84() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "85", members = {"long_run"})
    public void test85() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "86", members = {"long_run"})
    public void test86() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "87", members = {"long_run"})
    public void test87() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "88", members = {"long_run"})
    public void test88() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "89", members = {"long_run"})
    public void test89() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "90", members = {"long_run"})
    public void test90() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "91", members = {"long_run"})
    public void test91() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "92", members = {"long_run"})
    public void test92() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "93", members = {"long_run"})
    public void test93() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "94", members = {"long_run"})
    public void test94() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "95", members = {"long_run"})
    public void test95() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "96", members = {"long_run"})
    public void test96() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "97", members = {"long_run"})
    public void test97() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "98", members = {"long_run"})
    public void test98() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }

    @Clustering(id = "99", members = {"long_run"})
    public void test99() throws Exception {
        JsonObject json = new JsonObject();
        Thread.sleep(1000);
        json.addProperty("method", "get");
        RestApiRequest req = new RestApiRequest(RequestMethod.GET, "https://httpbin.org/deny");
        req.setBody(json);
        RestApiResponse response = (RestApiResponse) request.getResponse(req);
        System.out.println(response);
    }
}