package communication.util;

import communication.RequestMethod;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * <strong>HTTP POST request w/ body</strong><br><br>
 * This class is redundant. Made only for aesthetics.
 */
public class HttpPostWithBody extends HttpEntityEnclosingRequestBase {
    private static final RequestMethod METHOD_NAME = RequestMethod.POST;
    private URI uri;

    public HttpPostWithBody() {
        super();
    }

    public HttpPostWithBody(final String url) {
        super();
        setURI(URI.create(url));
    }

    public HttpPostWithBody(final URI uri) {
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