package communication;

import com.google.gson.JsonObject;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * <strong>HTTP DELETE request</strong>
 */
public class DeleteRequest extends HttpRequest {
    private HttpDeleteWithBody request;

    public DeleteRequest(String url) {
        super(RequestMethod.DELETE, url);
        this.request = new HttpDeleteWithBody(url);
        this.request.addHeader("User-Agent", getUSER_AGENT());
        this.request.addHeader("accept", "application/json");
    }

    @Override public HttpDeleteWithBody getRequest() {
        return this.request;
    }

    @Override public DeleteRequest addHeader(String name, String value) {
        this.request.addHeader(name, value);
        return this;
    }

    @Override public DeleteRequest addParam(String name, String value) {
        try {
            URI uri = new URIBuilder(this.request.getURI()).addParameter(name, value).build();
            this.request.setURI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override public DeleteRequest addBody(String body) {
        this.request.setEntity(new ByteArrayEntity(body.getBytes(Charset.forName("UTF-8"))));
        return this;
    }

    @Override public DeleteRequest addBody(JsonObject body) {
        this.request.setEntity(new StringEntity(body.toString(), Charset.forName("UTF-8")));
        return this;
    }
}
