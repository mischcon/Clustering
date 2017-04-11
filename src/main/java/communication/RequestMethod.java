package communication;

import java.io.Serializable;

/**
 * <strong>HTTP request methods</strong>
 * <ul>
 *     <li>
 *         GET
 *     </li>
 *     <li>
 *         POST
 *     </li>
 *     <li>
 *         PUT
 *     </li>
 *     <li>
 *         DELETE
 *     </li>
 * </ul>
 */
public enum RequestMethod implements Serializable {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String method;

    RequestMethod(final String method) {
        this.method = method;
    }

    @Override public String toString() {
        return method;
    }
}
