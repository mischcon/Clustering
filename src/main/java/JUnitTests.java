import clustering.Clustering;
import clustering.ClusteringTask;
import com.google.gson.JsonObject;
import communication.RequestMethod;
import communication.RestApiRequest;
import communication.RestApiResponse;
import org.junit.*;


public class JUnitTests implements ClusteringTask {

    private static RestApiRequest req;
    private RestApiResponse res;


    @BeforeClass
    public static void setUpClass() {
        System.out.println("setUpClass()");

        JsonObject json = new JsonObject();
        json.addProperty("method", "get");
        req = new RestApiRequest(RequestMethod.GET, "https://sds.ssp-europe.eu:443/api/v4/auth/resources");
        req.addHeader("Content-Type", "application/json");
        req.addParam("param", "1");
        req.setBody(json);
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("tearDownClass()");
    }

    @Before
    public void setUp() {
        System.out.println("setUp()");

        res = (RestApiResponse) request.getResponse(req);
        System.out.println(res);
    }

    @After
    public void tearDown() {
        System.out.println("tearDown()");
    }

    @Test
    @Clustering(id="get_success", members={"requests"})
    public void testGetSuccess() {
        Assert.assertEquals(res.getStatusText(), "HTTP/1.1 200 OK");
    }

    @Ignore
    @Test
    @Clustering(id="get_failure", members={"requests"})
    public void testGetFailure() {
        Assert.assertEquals(res.getStatusText(), "HTTP/1.1 201 OK");
    }

    @Test
    @Clustering(id="post_failure", members={"requests"})
    public void testPostFailure() {
        Assert.assertEquals(res.getStatusText(), "HTTP/1.1 201 OK");
    }
}