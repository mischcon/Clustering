package communication.util;

import communication.RequestMethod;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * <strong>HTTP PUT request w/ body</strong><br><br>
 * This class is made only for aesthetics.
 */
public class HttpPutWithBody extends HttpEntityEnclosingRequestBase {
    private static final RequestMethod METHOD_NAME = RequestMethod.PUT;
    private URI uri;

    public HttpPutWithBody() {
        super();
    }

    public HttpPutWithBody(final String url) {
        super();
        setURI(URI.create(url));
    }

    public HttpPutWithBody(final URI uri) {
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