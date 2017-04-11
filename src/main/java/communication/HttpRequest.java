package communication;

import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.io.*;

/**
 * <strong>Blueprint for CRUD requests</strong>
 */
public abstract class HttpRequest implements Serializable {
    private RequestMethod method;
    private String url;

    HttpRequest() {}

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
}