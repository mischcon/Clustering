package communication;

import java.io.*;

/**
 * <strong>Simplified wrapper class for {@link org.apache.http.HttpResponse}</strong>
 */
public class HttpResponse implements Serializable {
    /**
     * Original response class from {@link org.apache.http.impl.client.HttpClients}
     */
    private Integer statusCode;
    private String statusText;
    private String body;

    public HttpResponse(org.apache.http.HttpResponse response) {
        this.statusText = response.getStatusLine().toString();
        this.statusCode = response.getStatusLine().getStatusCode();

        try {
            /*
                might be null e.g. response code 204
            */
            if (response.getEntity() != null) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                    result.append("\n");
                }
                in.close();
                this.body = result.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
}
