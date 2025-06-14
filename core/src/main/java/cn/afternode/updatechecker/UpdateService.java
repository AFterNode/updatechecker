package cn.afternode.updatechecker;

import cn.afternode.updatechecker.http.HttpService;

import java.io.IOException;

public interface UpdateService<V extends VersionManifest> {
    /**
     * Fetch the latest version
     * @param http HTTP client
     * @param stable Search for stable versions only
     * @return Result version
     * @throws IOException HTTP error
     */
    V findLatestVersion(HttpService http, boolean stable) throws IOException;

    /**
     * Fetch and find the newer version
     * @param http HTTP client
     * @param current Current version
     * @param format Target version format
     * @param stable Search for stable versions only
     * @return Result version or null
     * @throws IOException HTTP error
     */
    V findNewerVersion(HttpService http, String current, VersionFormat format, boolean stable) throws IOException;
}
