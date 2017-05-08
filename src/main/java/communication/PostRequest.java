package communication;

import com.google.gson.JsonObject;
import communication.util.HttpPostWithBody;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * <strong>HTTP POST request</strong>
 */
public class PostRequest extends HttpRequest {
    private HttpPostWithBody request;

    public PostRequest(String url) {
        super(RequestMethod.POST, url);
        this.request = new HttpPostWithBody(url);
    }

    /**
     * Constructor for the conversion between {@link RestApiRequest} and HTTP POST request.
     * @param req {@link RestApiRequest}
     */
    public PostRequest(RestApiRequest req) {
        /*
            if req.getMethod() is not POST, it does not matter
            POST request will be created anyway
         */
        super(RequestMethod.valueOf(req.getMethod()), req.getUrl());
        this.request = new HttpPostWithBody(req.getUrl());
        for (Map.Entry<String, String> header : req.getHeaders().entrySet())
            this.addHeader(header.getKey(), header.getValue());
        for (Map.Entry<String, String> param : req.getParams().entrySet())
            this.addParam(param.getKey(), param.getValue());
        this.addBody(req.getBody());
    }

    @Override public HttpPostWithBody getRequest() {
        return this.request;
    }

    @Override public PostRequest addHeader(String name, String value) {
        this.request.addHeader(name, value);
        return this;
    }

    @Override public PostRequest addParam(String name, String value) {
        try {
            URI uri = new URIBuilder(this.request.getURI()).addParameter(name, value).build();
            this.request.setURI(uri);
        } catch (URISyntaxException e) {
            System.err.println(String.format("[PostRequest]: Invalid parameter: %s=%s", name, value));
            e.printStackTrace();
        }
        return this;
    }

    @Override public PostRequest addBody(byte[] body) {
        this.request.setEntity(new ByteArrayEntity(body));
        return this;
    }

    @Override public PostRequest addBody(String body) {
        this.request.setEntity(new ByteArrayEntity(body.getBytes(getCHARSET())));
        return this;
    }

    @Override public PostRequest addBody(JsonObject body) {
        this.request.setEntity(new StringEntity(body.toString(), getCHARSET()));
        return this;
    }
}
