package communication;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.io.Serializable;
import java.net.URI;

/**
 * <strong>HTTP PUT request w/ the opportunity of setting body</strong>
 */
class HttpPutWithBody extends HttpEntityEnclosingRequestBase implements Serializable {
    static final String METHOD_NAME = "PUT";
    private URI uri;

    public HttpPutWithBody() {
        super();
    }

    public HttpPutWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
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
        return METHOD_NAME;
    }
}