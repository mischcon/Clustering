package communication;

import com.google.gson.JsonObject;
import communication.util.HttpPutWithBody;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * <strong>HTTP PUT request</strong>
 */
public class PutRequest extends HttpRequest {
    private HttpPutWithBody request;

    public PutRequest(String url) {
        super(RequestMethod.PUT, url);
        this.request = new HttpPutWithBody(url);
    }

    /**
     * Constructor for the conversion between {@link RestApiRequest} and HTTP PUT request.
     * @param req {@link RestApiRequest}
     */
    public PutRequest(RestApiRequest req) {
        /*
            if req.getMethod() is not PUT, it does not matter
            PUT request will be created anyway
         */
        super(RequestMethod.valueOf(req.getMethod()), req.getUrl());
        this.request = new HttpPutWithBody(req.getUrl());
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
            System.err.println(String.format("[PutRequest]: Invalid parameter: %s=%s", name, value));
            e.printStackTrace();
        }
        return this;
    }

    @Override public PutRequest addBody(byte[] body) {
        if (body != null)
            this.request.setEntity(new ByteArrayEntity(body));
        return this;
    }

    @Override public PutRequest addBody(String body) {
        if (body != null)
            this.request.setEntity(new ByteArrayEntity(body.getBytes(getCHARSET())));
        return this;
    }

    @Override public PutRequest addBody(JsonObject body) {
        if (body != null)
            this.request.setEntity(new StringEntity(body.toString(), getCHARSET()));
        return this;
    }
}
