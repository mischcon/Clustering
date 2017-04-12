package communication;

import com.google.gson.JsonObject;
import communication.util.HttpDeleteWithBody;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * <strong>HTTP DELETE request</strong>
 */
public class DeleteRequest extends HttpRequest {
    private HttpDeleteWithBody request;

    public DeleteRequest(String url) {
        super(RequestMethod.DELETE, url);
        this.request = new HttpDeleteWithBody(url);
    }

    public DeleteRequest(RestApiRequest req) {
        super(RequestMethod.valueOf(req.getMethod()), req.getUrl());
        this.request = new HttpDeleteWithBody(req.getUrl());
        for (Map.Entry<String, String> header : req.getHeaders().entrySet())
            this.addHeader(header.getKey(), header.getValue());
        for (Map.Entry<String, String> param : req.getParams().entrySet())
            this.addParam(param.getKey(), param.getValue());
        this.addBody(req.getBody());
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
            System.err.println(String.format("[DeleteRequest]: Invalid parameter: %s=%s", name, value));
            e.printStackTrace();
        }
        return this;
    }

    @Override public DeleteRequest addBody(byte[] body) {
        this.request.setEntity(new ByteArrayEntity(body));
        return this;
    }

    @Override public DeleteRequest addBody(String body) {
        this.request.setEntity(new ByteArrayEntity(body.getBytes(getCHARSET())));
        return this;
    }

    @Override public DeleteRequest addBody(JsonObject body) {
        this.request.setEntity(new StringEntity(body.toString(), getCHARSET()));
        return this;
    }
}
