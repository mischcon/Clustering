package communication;

import com.google.gson.JsonObject;
import communication.util.HttpGetWithBody;
import communication.util.HttpPutWithBody;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * <strong>HTTP PUT request</strong>
 */
public class PutRequest extends HttpRequest implements Serializable {
    private HttpPutWithBody request;

    public PutRequest(String url) {
        super(RequestMethod.PUT, url);
        this.request = new HttpPutWithBody(url);
        this.request.addHeader("accept", "application/json");
    }

    public PutRequest(RestApiRequest req) {
        super(RequestMethod.valueOf(req.getMethod()), req.getUrl());
        this.request = new HttpPutWithBody(req.getUrl());
        this.request.addHeader("accept", "application/json");
        for (Map.Entry<String, String> header : req.getHeaders().entrySet())
            this.addHeader(header.getKey(), header.getValue());
        for (Map.Entry<String, String> param : req.getParams().entrySet())
            this.addParam(param.getKey(), param.getValue());
        this.addBody(req.getBody());
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

    @Override public PutRequest addBody(byte[] body) {
        this.request.setEntity(new ByteArrayEntity(body));
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
