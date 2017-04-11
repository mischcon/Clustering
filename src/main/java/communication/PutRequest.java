package communication;

import com.google.gson.JsonObject;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * <strong>HTTP PUT request</strong>
 */
public class PutRequest extends HttpRequest implements Serializable {
    private HttpPutWithBody request;

    public PutRequest(String url) {
        super(RequestMethod.PUT, url);
        this.request = new HttpPutWithBody(url);
        this.request.addHeader("User-Agent", getUSER_AGENT());
        this.request.addHeader("accept", "application/json");
    }

    @Override public HttpPutWithBody getRequest() {
        return this.request;
    }

    @Override public PutRequest addHeader(String name, String value) {
        this.request.addHeader(name, value);
        return this;
    }

    @Override public PutRequest addParam(String name, String value) {
        try {
            URI uri = new URIBuilder(this.request.getURI()).addParameter(name, value).build();
            this.request.setURI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override public PutRequest addBody(String body) {
        this.request.setEntity(new ByteArrayEntity(body.getBytes(Charset.forName("UTF-8"))));
        return this;
    }

    @Override public PutRequest addBody(JsonObject body) {
        this.request.setEntity(new StringEntity(body.toString(), Charset.forName("UTF-8")));
        return this;
    }
}
