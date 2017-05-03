package communication;

import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.nio.charset.Charset;


/**
 * <strong>Blueprint for CRUD requests</strong>
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public abstract class HttpRequest {
    private RequestMethod method;
    private String url;
    private static final Charset CHARSET = Charset.forName("UTF-8");

    HttpRequest(RequestMethod method, String url) {
        this.method = method;
        this.url = url;
    }

    public abstract HttpEntityEnclosingRequestBase getRequest();

    public abstract HttpRequest addHeader(String name, String value);

    public abstract HttpRequest addParam(String name, String value);

    public abstract HttpRequest addBody(byte[] body);

    public abstract HttpRequest addBody(String body);

    public abstract HttpRequest addBody(JsonObject body);

    public RequestMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public static Charset getCHARSET() {
        return CHARSET;
    }
}