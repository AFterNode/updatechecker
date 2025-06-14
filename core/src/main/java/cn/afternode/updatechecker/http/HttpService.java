package cn.afternode.updatechecker.http;

import java.io.IOException;

public interface HttpService {
    String fetch(String url) throws IOException;
}
