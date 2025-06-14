package cn.afternode.updatechecker.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * HttpService implementation using {@link java.net.http.HttpClient}
 */
public class SimpleHttpService implements HttpService {
    private final HttpClient client;

    public SimpleHttpService(HttpClient client) {
        this.client = client;
    }

    public SimpleHttpService() {
        this(HttpClient.newHttpClient());
    }

    @Override
    public String fetch(String url) throws IOException {
        try {
            return this.client.send(HttpRequest.newBuilder()
                            .uri(new URI(url))
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build(), HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
