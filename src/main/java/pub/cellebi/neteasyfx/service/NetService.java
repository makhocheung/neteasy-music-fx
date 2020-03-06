package pub.cellebi.neteasyfx.service;


import pub.cellebi.neteasyfx.Handler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class NetService {

    public static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static CompletableFuture<Void> get(String url, Handler<String> consumer) {
        var req = HttpRequest.newBuilder(URI.create(url)).build();
        return HTTP_CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(s -> {
                    try {
                        consumer.handle(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
    }
}
