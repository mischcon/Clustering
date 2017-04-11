package communication;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.io.Serializable;
import java.net.URI;

/**
 * <strong>HTTP GET request w/ the opportunity of setting body</strong>
 */
class HttpGetWithBody extends HttpEntityEnclosingRequestBase implements Serializable {
    static final String METHOD_NAME = "GET";
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
        return METHOD_NAME;
    }
}