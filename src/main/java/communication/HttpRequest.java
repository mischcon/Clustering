package communication;

import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.Serializable;

/**
 * <strong>Blueprint for CRUD requests</strong>
 */
public abstract class HttpRequest implements Serializable{
    HttpRequestBase request;
    RequestMethod method;
    String url;
    final String USER_AGENT = "TESTER";

    HttpRequest() {}

    HttpRequest(RequestMethod method, String url) {
        this.method = method;
        this.url = url;
    }

    public abstract HttpRequestBase getRequest();

    public abstract HttpRequest addHeader(String name, String value);

    public abstract HttpRequest addParam(String name, String value);

    public abstract HttpRequest addBody(String body);

    public abstract HttpRequest addBody(JsonObject body);

    public RequestMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getUSER_AGENT() {
        return USER_AGENT;
    }
}