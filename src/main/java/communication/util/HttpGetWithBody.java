package communication.util;

import communication.RequestMethod;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * <strong>HTTP GET request w/ body</strong>
 */
public class HttpGetWithBody extends HttpEntityEnclosingRequestBase {
    private static final RequestMethod METHOD_NAME = RequestMethod.GET;
    private URI uri;

    public HttpGetWithBody() {
        super();
    }

    public HttpGetWithBody(final String url) {
        super();
        setURI(URI.create(url));
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