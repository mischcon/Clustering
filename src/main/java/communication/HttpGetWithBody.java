package communication;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import java.net.URI;

/**
 * <strong>HTTP GET request w/ the opportunity of setting body</strong>
 */
class HttpGetWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = RequestMethod.GET.toString();

    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpGetWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpGetWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpGetWithBody() {
        super();
    }
}