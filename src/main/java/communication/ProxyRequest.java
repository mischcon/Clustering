package communication;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.io.Serializable;

/**
 * <strong>Java & Scala marriage happens w/ help of this class</strong>
 * @param <T> request type - Object in {@link clustering.ClusteringTask}
 */
public class ProxyRequest<T> implements Serializable {
    /**
     * If executed in cluster, this field will be <i>post-injected</i> w/ instance of
     * <code>vmProxyActor</code> by executing actor
     */
    private ActorRef vmProxy;
    private Timeout timeout;
    private Future<Object> future;
    private Object response;

    public ProxyRequest() {
        this.timeout = new Timeout(Duration.create(5, "seconds"));
    }

    ProxyRequest(Integer timeout) {
        this.timeout = new Timeout(Duration.create(timeout, "seconds"));
    }

    private void send(T request) {
        /*
            vmProxy == null : task is executed locally
            vmProxy != null : task is executed in cluster
        */
        if (vmProxy != null)
            this.future = Patterns.ask(vmProxy, request, timeout);
        else {
            if (request instanceof HttpRequest) {
                CloseableHttpClient client;
                CloseableHttpResponse response;
                try {
                    client = HttpClientBuilder.create().build();
                    response = client.execute(((HttpRequest) request).getRequest());
                    this.response = new communication.HttpResponse(response);
                    response.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /* expand if needed
            else if (request instanceof ...) {
                ...
            }
            */
            else {
                System.err.println("[ProxyRequest]: unsupported request type : " + request.getClass());
            }
        }
    }

    private Object receive() {
        if (vmProxy != null) {
            try {
                this.response = Await.result(future, timeout.duration());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        else {
            return this.response;
        }
    }

    public Object getResponse(T request) {
        send(request);
        return receive();
    }
}
