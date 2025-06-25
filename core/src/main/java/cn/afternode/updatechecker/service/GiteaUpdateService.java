package cn.afternode.updatechecker.service;

import cn.afternode.updatechecker.UpdateService;
import cn.afternode.updatechecker.VersionFormat;
import cn.afternode.updatechecker.VersionManifest;
import cn.afternode.updatechecker.http.HttpService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Update checker uses Gitea repositories API, also supports Forgejo, with release page as URL, tag name as version name
 */
public class GiteaUpdateService implements UpdateService<VersionManifest.Basic> {
    private final Gson gson = new Gson();

    private final String root, owner, repo;

    /**
     * constructor
     * @param root Gitea instance root URL e.g. <code>https://codeberg.org/</code>
     * @param owner Repository owner
     * @param repo Repository name
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    public GiteaUpdateService(String root, String owner, String repo) {
        this.root = (root.endsWith("/") ? root : root + '/') + "api/v1/";
        this.owner = owner;
        this.repo = repo;
    }

    /**
     * Create a Codeberg checker
     * @param owner Repository owner
     * @param repo Repository name
     * @return created service
     */
    public static GiteaUpdateService codeberg(String owner, String repo) {
        return new GiteaUpdateService("https://codeberg.org/", owner, repo);
    }

    @Override
    public VersionManifest.Basic findLatestVersion(HttpService http, boolean stable) throws IOException {
        if (stable) {
            String url = root +
                    "repos/" + this.owner +
                    "/" + this.repo +
                    "/releases/latest";
            String fetch = http.fetch(url);
            return this.parse(gson.fromJson(fetch, JsonObject.class));
        } else {    // all versions
            String url = root +
                    "repos/" + this.owner +
                    "/" + this.repo +
                    "/releases?limit=1";
            JsonArray versions = gson.fromJson(http.fetch(url), JsonArray.class);
            if (!versions.isEmpty()) {
                return this.parse(versions.get(0).getAsJsonObject());
            }
        }

        return null;
    }

    private VersionManifest.Basic parse(JsonObject obj) {
        try {
            String url = obj.get("html_url").getAsString();
            boolean stable = !obj.get("prerelease").getAsBoolean();
            String body = obj.get("body").getAsString();
            String tag = obj.get("tag_name").getAsString();

            return new VersionManifest.Basic(
                    tag,
                    url,
                    body,
                    stable
            );
        } catch (NullPointerException npe) {
            return null;
        }
    }

    @Override
    public VersionManifest.Basic findNewerVersion(HttpService http, String current, VersionFormat format, boolean stable) throws IOException {
        VersionManifest.Basic latest = this.findLatestVersion(http, stable);
        if (latest == null)
            return null;


        return format.isNewerThan(latest.version(), current) ? latest : null;
    }
}
