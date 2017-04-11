package communication;


import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * <strong>Simplified wrapper class for {@link org.apache.http.HttpResponse}</strong>
 */
public class RestApiResponse implements Serializable {
    private String statusCode;
    private String statusText;
    private String body = null;

    public RestApiResponse(org.apache.http.HttpResponse response) {
        this.statusText = response.getStatusLine().toString();
        this.statusCode = String.valueOf(response.getStatusLine().getStatusCode());

        try {
            /*
                might be null e.g. response code 204
            */
            if (response.getEntity() != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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

    public RestApiResponse(HttpStatus statusCode, String statusText) {
        this.statusCode = String.valueOf(statusCode);
        this.statusText = statusText;
        this.body = body;
    }

    public RestApiResponse(HttpStatus statusCode, String statusText, String body) {
        this.statusCode = String.valueOf(statusCode);
        this.statusText = statusText;
        this.body = body;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = String.valueOf(statusCode);
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
