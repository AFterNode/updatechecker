package cn.afternode.updatechecker.http;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * HttpService implementation with Apache httpcomponents
 */
public class HCHttpService implements HttpService {
    private final HttpClient client;

    /**
     * Create with client
     * @param client client
     */
    public HCHttpService(HttpClient client) {
        this.client = client;
    }

    /**
     * Create with {@link HttpClients#createSystem()}
     */
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
