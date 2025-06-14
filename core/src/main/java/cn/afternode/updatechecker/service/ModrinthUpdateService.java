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
 * Update checker uses <a href="https://api.modrinth.com/">Modrinth API</a>
 */
public class ModrinthUpdateService implements UpdateService<ModrinthUpdateService.Version> {
    private final String slug;
    private final String loader;
    private final String minecraft;

    private final Gson gson = new Gson();

    /**
     * @param slug Project's slug
     * @param loader Loader name like <code>fabric</code> <code>forge</code> <code>paper</code>
     * @param minecraft Minecraft version, may be null
     */
    public ModrinthUpdateService(String slug, String loader, String minecraft) {
        this.slug = slug;
        this.loader = loader;
        this.minecraft = minecraft;
    }

    @Override
    public Version findLatestVersion(HttpService http, boolean stable) throws IOException {
        // fetch versions
        StringBuilder url = new StringBuilder("https://api.modrinth.com/v2/project/")
                .append(this.slug)
                .append("/version")
                .append("?loader=")
                .append(this.loader);
        if (this.minecraft != null)
            url.append("&game_versions").append(this.minecraft);
        JsonArray result = gson.fromJson(http.fetch(url.toString()), JsonArray.class);
        if (result.isEmpty()) {
            return null;
        }

        for (JsonElement element : result) {
            JsonObject version = element.getAsJsonObject();
            boolean isStable = version.get("version_type").getAsString().equals("release");
            if (stable && !isStable)
                continue;   // unstable version

            String ver = version.get("version_number").getAsString();

            // find primary file
            String download = null;
            for (JsonElement f : version.getAsJsonArray("files")) {
                JsonObject file = f.getAsJsonObject();
                if (file.get("primary").getAsBoolean()) {
                    download = file.get("url").getAsString();
                    break;
                }
            }

            String changelog = version.has("changelog") ? version.get("changelog").getAsString() : null;

            return new Version(ver, download, changelog, isStable);
        }

        return null;
    }

    @Override
    public Version findNewerVersion(HttpService http, String current, VersionFormat format, boolean stable) throws IOException {
        Version latest = this.findLatestVersion(http, stable);

        return latest != null && format.isNewerThan(latest.version, current) ? latest : null;
    }

    /**
     * Modrinth version metadata
     * @param version version
     * @param url download URL
     * @param changelog changelog
     * @param stable stability
     */
    public record Version(
            @Override String version,
            @Override String url,
            @Override String changelog,

            @Override boolean stable
    ) implements VersionManifest {
    }
}

