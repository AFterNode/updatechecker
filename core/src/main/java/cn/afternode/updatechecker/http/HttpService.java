package cn.afternode.updatechecker.http;

import java.io.IOException;

/**
 * HTTP client base
 */
public interface HttpService {
    /**
     * GET from url
     * @param url url
     * @return response body
     * @throws IOException request failed
     */
    String fetch(String url) throws IOException;
}
