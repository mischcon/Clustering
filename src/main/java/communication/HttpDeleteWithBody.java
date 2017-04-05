package communication;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import java.net.URI;

/**
 * <strong>HTTP DELETE request w/ the opportunity of setting body</strong>
 */
class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = RequestMethod.DELETE.toString();

    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpDeleteWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteWithBody() {
        super();
    }
}