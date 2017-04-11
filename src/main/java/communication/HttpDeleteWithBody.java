package communication;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.Serializable;
import java.net.URI;

/**
 * <strong>HTTP DELETE request w/ the opportunity of setting body</strong>
 */
class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase implements Serializable {
    static final RequestMethod METHOD_NAME = RequestMethod.DELETE;
    private URI uri;

    public HttpDeleteWithBody() {
        super();
    }

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
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