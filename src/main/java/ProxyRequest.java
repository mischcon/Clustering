import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

class ProxyRequest<T> {
    private ActorRef vmProxy;
    private Timeout timeout;
    private Future<Object> future;

    ProxyRequest() {
        this.timeout = new Timeout(Duration.create(5, "seconds"));
    }

    ProxyRequest(Integer timeout) {
        this.timeout = new Timeout(Duration.create(timeout, "seconds"));
    }

    void send(T request) {
        this.future = Patterns.ask(vmProxy, request, timeout);
    }

    Object receive() {
        try {
            return Await.result(future, timeout.duration());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    Object getResponse(T request) {
        send(request);
        return receive();
    }
}
