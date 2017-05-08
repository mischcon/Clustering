package clustering;

import communication.ProxyRequest;

/**
 * <strong>Main clustering interface</strong><br><br>
 * Every test class (w/ network communication) must implement it.<br>
 * Establishes the communication between task & proxy (cluster¹ or local VM²).<br>
 * <p>e.g. scenario for HTTP requests</p>
 * <ul>
 *     <li><pre>cluster¹  : Java <-----> Scala <-----> VM <-----> Internet 🌐</pre></li>
 *     <li><pre>local VM² : Java <-------------------> VM <-----> Internet 🌐</pre></li>
 * </ul>
 * Usage:<br><br>
 * Call <i>request.getResponse('request object')</i> to execute your request and getting a response.
 */
public interface ClusteringTask  {
    ProxyRequest<Object> request = new ProxyRequest<>();
}