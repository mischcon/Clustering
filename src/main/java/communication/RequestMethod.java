package communication;

/**
 * <strong>HTTP request methods (CRUD)</strong>
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
public enum RequestMethod {
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
