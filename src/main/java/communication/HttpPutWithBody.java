package communication;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import java.net.URI;

/**
 * <strong>HTTP PUT request w/ the opportunity of setting body</strong>
 */
class HttpPutWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = RequestMethod.PUT.toString();

    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpPutWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpPutWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpPutWithBody() {
        super();
    }
}