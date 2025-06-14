package cn.afternode.updatechecker.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OkHttpService implements HttpService {
    private final OkHttpClient client;

    public OkHttpService(OkHttpClient client) {
        this.client = client;
    }

    public OkHttpService() {
        this(new OkHttpClient.Builder().build());
    }

    @Override
    public String fetch(String url) throws IOException {
        try (Response resp = this.client.newCall(new Request.Builder()
                        .get()
                        .url(url)
                        .build())
                .execute()) {
            return resp.body().string();
        }
    }
}
