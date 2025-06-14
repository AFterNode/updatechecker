package cn.afternode.updatechecker.http;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class HCHttpService implements HttpService {
    private final HttpClient client;

    public HCHttpService(HttpClient client) {
        this.client = client;
    }

    public HCHttpService() {
        this(HttpClients.createSystem());
    }

    @Override
    public String fetch(String url) throws IOException {
        return this.client.execute(new HttpGet(url), resp ->
            EntityUtils.toString(resp.getEntity())
        );
    }
}
