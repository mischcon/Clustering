package communication.util;

import communication.RequestMethod;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.Serializable;
import java.net.URI;

/**
 * <strong>HTTP GET request w/ the opportunity of setting body</strong>
 */
public class HttpGetWithBody extends HttpEntityEnclosingRequestBase implements Serializable {
    static final RequestMethod METHOD_NAME = RequestMethod.GET;
    private URI uri;

    public HttpGetWithBody() {
        super();
    }

    public HttpGetWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpGetWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    public URI getURI() {
        return this.uri;
    }

    public void setURI(final URI uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return METHOD_NAME.toString();
    }
}