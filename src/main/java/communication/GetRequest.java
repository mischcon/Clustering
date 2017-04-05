package communication;

import com.google.gson.JsonObject;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * <strong>HTTP GET request</strong>
 */
public class GetRequest extends HttpRequest {
    private HttpGetWithBody request;

    public GetRequest(String url) {
        super(RequestMethod.GET, url);
        this.request = new HttpGetWithBody(url);
        request.addHeader("User-Agent", getUSER_AGENT());
        request.addHeader("accept", "application/json");
    }

    @Override public HttpGetWithBody getRequest() {
        return this.request;
    }

    @Override public GetRequest addHeader(String name, String value) {
        this.request.addHeader(name, value);
        return this;
    }

    @Override public GetRequest addParam(String name, String value) {
        try {
            URI uri = new URIBuilder(this.request.getURI()).addParameter(name, value).build();
            this.request.setURI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override public GetRequest addBody(String body) {
        this.request.setEntity(new ByteArrayEntity(body.getBytes(Charset.forName("UTF-8"))));
        return this;
    }

    @Override public GetRequest addBody(JsonObject body) {
        this.request.setEntity(new StringEntity(body.toString(), Charset.forName("UTF-8")));
        return this;
    }
}
