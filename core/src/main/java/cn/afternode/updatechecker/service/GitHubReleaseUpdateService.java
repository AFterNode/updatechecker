package cn.afternode.updatechecker.service;

import cn.afternode.updatechecker.UpdateService;
import cn.afternode.updatechecker.VersionFormat;
import cn.afternode.updatechecker.VersionManifest;
import cn.afternode.updatechecker.http.HttpService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Update checker uses GitHub Release API, with release page as URL
 */
public class GitHubReleaseUpdateService implements UpdateService<VersionManifest.Basic> {
    private final String owner, repository;
    private final int maxRetries;

    private final Gson gson = new Gson();

    /**
     * constructor
     * @param owner Repository owner
     * @param repository Repository name
     * @param maxRetries Max searched pages
     */
    public GitHubReleaseUpdateService(String owner, String repository, int maxRetries) {
        this.owner = owner;
        this.repository = repository;
        this.maxRetries = maxRetries;
    }

    public GitHubReleaseUpdateService(String owner, String repository) {
        this(owner, repository, 5);
    }

    @Override
    public VersionManifest.Basic findLatestVersion(HttpService http, boolean stable) throws IOException {
        int page = 0;
        VersionManifest.Basic result = null;

        while (result == null && page <= maxRetries) {
            result = this.fetch0(http, stable, page++);
        }

        return result;
    }

    private VersionManifest.Basic fetch0(HttpService http, boolean stable, int page) throws IOException {
        String fetch = http.fetch("https://api.github.com/repos/%s/%s/releases?per_page=1&page=%s".formatted(this.owner, this.repository, page));
        JsonArray versions = this.gson.fromJson(fetch, JsonArray.class);

        for (JsonElement json : versions) {
            JsonObject obj = json.getAsJsonObject();
            boolean isStable =  !obj.get("prerelease").getAsBoolean();
            if (!isStable && stable)    // filter stable versions
                continue;

            String name = obj.get("name").getAsString();
            String body = obj.get("body").getAsString();
            String url = obj.get("html_url").getAsString();

            return new VersionManifest.Basic(name, url, body, isStable);
        }

        return null;
    }

    @Override
    public VersionManifest.Basic findNewerVersion(HttpService http, String current, VersionFormat format, boolean stable) throws IOException {
        VersionManifest.Basic latest = this.findLatestVersion(http, stable);
        if (latest != null && format.isNewerThan(latest.version(), current))
            return latest;

        return null;
    }
}
