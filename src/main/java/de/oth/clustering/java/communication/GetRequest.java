package de.oth.clustering.java.communication;

import com.google.gson.JsonObject;
import de.oth.clustering.java.communication.util.HttpGetWithBody;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * <strong>HTTP GET request</strong>
 */
public class GetRequest extends HttpRequest {
    private HttpGetWithBody request;

    public GetRequest(String url) {
        super(RequestMethod.GET, url);
        this.request = new HttpGetWithBody(url);
    }

    /**
     * Constructor for the conversion between {@link RestApiRequest} and HTTP GET request.
     * @param req {@link RestApiRequest}
     */
    public GetRequest(RestApiRequest req) {
        /*
            if req.getMethod() is not GET, it does not matter
            GET request will be created anyway
         */
        super(RequestMethod.valueOf(req.getMethod()), req.getUrl());
        this.request = new HttpGetWithBody(req.getUrl());
        for (Map.Entry<String, String> header : req.getHeaders().entrySet())
            this.addHeader(header.getKey(), header.getValue());
        for (Map.Entry<String, String> param : req.getParams().entrySet())
            this.addParam(param.getKey(), param.getValue());
        this.addBody(req.getBody());
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
            System.err.println(String.format("[GetRequest]: Invalid parameter: %s=%s", name, value));
            e.printStackTrace();
        }
        return this;
    }

    @Override public GetRequest addBody(byte[] body) {
        if (body != null)
            this.request.setEntity(new ByteArrayEntity(body));
        return this;
    }

    @Override public GetRequest addBody(String body) {
        if (body != null)
            this.request.setEntity(new ByteArrayEntity(body.getBytes(getCHARSET())));
        return this;
    }

    @Override public GetRequest addBody(JsonObject body) {
        if (body != null)
            this.request.setEntity(new StringEntity(body.toString(), getCHARSET()));
        return this;
    }
}
