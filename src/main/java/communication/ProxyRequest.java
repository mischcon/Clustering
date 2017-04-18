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

/**
 * <strong>Java & Scala marriage happens w/ help of this class</strong>
 * @param <T> request type - Object in {@link clustering.ClusteringTask}
 */
public class ProxyRequest<T> {
    /**
     * If executed in cluster, this field will be <i>post-injected</i> w/ instance of
     * <code>vmProxyActor</code> by executing actor
     */
    private ActorRef vmProxy;
    private Future<Object> future;
    private Object response;
    private static final Timeout TIMEOUT = new Timeout(Duration.create(5, "seconds"));

    public ProxyRequest() {}

    private void execute(HttpRequest http) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        try {
            response = client.execute(http.getRequest());
            this.response = new RestApiResponse(response);
        } catch (IOException e) {
            System.err.println(String.format(
                    "[ProxyRequest]: Could not execute following request: %s", http.getRequest().getURI()));
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                client.close();
            } catch (IOException e) {
                System.err.println("[ProxyRequest]: Could not clean up the resources");
                e.printStackTrace();
            }
        }
    }

    private void send(T request) {
        /*
            vmProxy == null : task is executed locally
            vmProxy != null : task is executed in cluster
        */
        if (vmProxy != null) {
            this.future = Patterns.ask(vmProxy, request, TIMEOUT);
        }
        else {
            if (request instanceof RestApiRequest) {
                switch (((RestApiRequest) request).getMethod()) {
                    case "GET":    execute(new GetRequest((RestApiRequest) request));    break;
                    case "POST":   execute(new PostRequest((RestApiRequest) request));   break;
                    case "PUT":    execute(new PutRequest((RestApiRequest) request));    break;
                    case "DELETE": execute(new DeleteRequest((RestApiRequest) request)); break;
                    default: break;
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
                this.response = Await.result(future, TIMEOUT.duration());
                return this.response;
            } catch (Exception e) {
                System.err.println("[ProxyRequest]: Something went wrong during waiting for response");
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
