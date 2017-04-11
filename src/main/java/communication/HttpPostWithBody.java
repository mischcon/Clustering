package communication;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.io.Serializable;
import java.net.URI;

/**
 * <strong>HTTP POST request w/ the opportunity of setting body</strong>
 */
class HttpPostWithBody extends HttpEntityEnclosingRequestBase implements Serializable {
    static final RequestMethod METHOD_NAME = RequestMethod.POST;
    private URI uri;

    public HttpPostWithBody() {
        super();
    }

    public HttpPostWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
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