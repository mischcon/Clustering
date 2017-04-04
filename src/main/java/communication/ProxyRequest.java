package communication;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProxyRequest<T> {
    private ActorRef vmProxy;
    private Timeout timeout;
    private Future<Object> future;
    private Object response;

    ProxyRequest() {
        this.timeout = new Timeout(Duration.create(5, "seconds"));
    }

    ProxyRequest(Integer timeout) {
        this.timeout = new Timeout(Duration.create(timeout, "seconds"));
    }

    private void send(T request) {
        if (vmProxy != null)
            this.future = Patterns.ask(vmProxy, request, timeout);
        else {
            if (request instanceof HttpsURLConnectionImpl) {
                try {
                    HttpURLConnection httpRequest = (HttpURLConnection) request;
                    int responseCode = httpRequest.getResponseCode();
                    System.out.println(
                            "sending '" + httpRequest.getRequestMethod() + "' request to URL : " + httpRequest.getURL());
                    System.out.println("response code : " + responseCode);
                    BufferedReader in = null;
                    if (200 <= responseCode && responseCode <= 299)
                        in = new BufferedReader(new InputStreamReader(httpRequest.getInputStream()));
                    else
                        in = new BufferedReader(new InputStreamReader(httpRequest.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null)
                        response.append(inputLine + "\n");
                    this.response = response.toString();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("unsupported request type : " + request.getClass());
            }
        }

    }

    private Object receive() {
        if (vmProxy != null) {
            try {
                this.response = Await.result(future, timeout.duration());
                return response;
            }
            catch (Exception e) {
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
