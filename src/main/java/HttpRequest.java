import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import utils.db.GetTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



class HttpRequest implements ClusteringCommunication {
    @PostInject
    private ActorRef sender;
    @PostInject
    private ActorRef vmProxy;

    @Override
    public Object send(Object obj) {
        return null;
    }

    HttpResponse getResponse(String method, String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod(method);
        connection.setRequestProperty("User-Agent", "CLUSTER");

        // vmProxy.tell(new GetTask("test"), sender);

//        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
//        Future<Object> future = Patterns.ask(vmProxy, "HELLO", timeout);
//        String result = (String) Await.result(future, timeout.duration());
//        System.out.println(result);

//        int responseCode = connection.getResponseCode();
//        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String inputLine;
//        StringBuilder response = new StringBuilder();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();

        return new HttpResponse("TEST");//result);
    }
}
