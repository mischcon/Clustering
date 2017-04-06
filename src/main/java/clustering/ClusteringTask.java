package clustering;

import communication.ProxyRequest;

/**
 * <strong>Main clustering interface</strong><br>
 * Every task class (w/ network communication) should implement it.<br>
 * Establishes the communication between task & proxy <i>cluster or local VM</i>.
 */
public interface ClusteringTask  {
    ProxyRequest<Object> request = new ProxyRequest<>();
}