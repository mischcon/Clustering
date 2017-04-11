package communication;


import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;

public class ApiRequestContainer implements Serializable {
    RequestMethod method;
    String api;
    HashMap<String, String> headers;
    HashMap<String, String> params;
    byte[] body;
    transient Charset charset = Charset.forName("UTF-8");
}
