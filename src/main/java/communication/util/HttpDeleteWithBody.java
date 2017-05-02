package communication.util;

import communication.RequestMethod;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * <strong>HTTP DELETE request w/ body</strong><br><br>
 * Check <a href="https://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-19#section-6.7">RFC 2616</a> before using it.<br>
 * Make sure that server have an implementation for DELETE requests w/ body.<br>
 * Otherwise request might be rejected.
 */
public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
    private static final RequestMethod METHOD_NAME = RequestMethod.DELETE;
    private URI uri;

    public HttpDeleteWithBody() {
        super();
    }

    public HttpDeleteWithBody(final String url) {
        super();
        setURI(URI.create(url));
    }

    public HttpDeleteWithBody(final URI uri) {
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