import akka.actor.ActorRef;
import utils.db.GetTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



abstract class HttpRequest implements ClusteringCommunication {
    private ActorRef sender;
    private ActorRef vmProxy;

    @Override
    public Object send(Object obj) {
        return null;
    }

    HttpResponse getResponse(String method, String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod(method);
        connection.setRequestProperty("User-Agent", "CLUSTER");

        vmProxy.tell(new GetTask("test"), sender);

        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new HttpResponse(response.toString());
    }
}
