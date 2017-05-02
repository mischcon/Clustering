package communication;


import com.google.gson.JsonObject;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * <strong>Serializable container class for {@link org.apache.http.HttpRequest}</strong>
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class RestApiRequest implements Serializable {
    private String method;
    private String url;
    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> params = new HashMap<>();
    private byte[] body = null;
    private String charset = Charset.forName("UTF-8").toString();

    public RestApiRequest(RequestMethod method, String url) {
        this.method = method.toString();
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method.toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public void addParam(String key, String value) {
        this.params.put(key, value);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(String body) {
        this.body = body.getBytes(Charset.forName(charset));
    }

    public void setBody(JsonObject body) {
        this.body = body.toString().getBytes(Charset.forName(charset));
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset.toString();
    }
}
