import clustering.Clustering;
import clustering.ClusteringTask;
import com.google.gson.JsonObject;
import communication.RequestMethod;
import communication.RestApiRequest;
import communication.RestApiResponse;
import org.junit.*;


public class JUnitTests implements ClusteringTask {

    @BeforeClass
    public static void setUpClass() {
        System.out.println("setUpClass()");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("tearDownClass()");
    }

    @Before
    public void setUp() {
        System.out.println("setUp()");
    }

    @After
    public void tearDown() {
        System.out.println("tearDown()");
    }

    @Test
    @Clustering(id="testMethod", expectedDuration=2, members={"tests"})
    public void testMethod() {
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
}
