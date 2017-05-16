package de.oth.clustering.java.communication;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <strong>Serializable container class for {@link org.apache.http.HttpResponse}</strong><br><br>
 *
 * Implementation of REST API responses in relation to {@link ProxyRequest}.
 */
@SuppressWarnings("WeakerAccess")
public class RestApiResponse implements Serializable {
    private Integer statusCode;
    private String statusText;
    private String body = null;
    private HashMap<String, String> headers = new HashMap<>();
    private Locale locale;
    private ProtocolVersion protocolVersion;

    public RestApiResponse(HttpResponse response) {
        this.statusText = response.getStatusLine().toString();
        this.statusCode = response.getStatusLine().getStatusCode();
        for (Header header : response.getAllHeaders())
            this.headers.put(header.getName(), header.getValue());
        this.locale = response.getLocale();
        this.protocolVersion = response.getProtocolVersion();
        BufferedReader in = null;
        try {
            /*
                might be null e.g. response code 204
            */
            if (response.getEntity() != null) {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                    result.append("\n");
                }
                this.body = result.toString();
            }
        } catch (IOException e) {
            System.err.println(String.format(
                    "[RestApiResponse]: Could not read the body of response: %s", response.getStatusLine()));
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.err.println("[RestApiResponse]: Could not clean up the stream");
                e.printStackTrace();
            }
        }
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getBody() {
        return body;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public Locale getLocale() {
        return locale;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "\n======================= RESPONSE [CODE: %d] =======================\n\n", getStatusCode()));
        sb.append("  ");
        sb.append(getStatusText());
        sb.append("\n\n====================================================================\n");
        sb.append("|                             HEADERS                              |");
        sb.append("\n====================================================================");
        for (Map.Entry<String, String> header : getHeaders().entrySet())
            sb.append(String.format("\n  %s : %s", header.getKey(), header.getValue()));
        sb.append("\n====================================================================\n");
        sb.append("|                               BODY                               |");
        sb.append("\n====================================================================\n");
        sb.append(getBody());
        sb.append("====================================================================\n");
        return sb.toString();
    }
}
