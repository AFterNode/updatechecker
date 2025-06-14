package cn.afternode.updatechecker.service;

import cn.afternode.updatechecker.UpdateService;
import cn.afternode.updatechecker.VersionFormat;
import cn.afternode.updatechecker.VersionManifest;
import cn.afternode.updatechecker.http.HttpService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Update checker uses <a href="https://docs.minecraftforge.net/en/latest/misc/updatechecker/">MinecraftForge's update JSON format</a>
 */
public class ForgeUpdateService implements UpdateService<VersionManifest.Basic> {
    private final String url;
    private final String minecraft;

    private final String versionKeyL, versionKeyC;

    private final Gson gson = new Gson();

    /**
     * @param url updateJSONURL in <code>mods.toml</code>
     * @param minecraft Minecraft version, or any other platform version, may be null (disables changelog)
     */
    public ForgeUpdateService(String url, String minecraft) {
        this.url = url;
        this.minecraft = minecraft;
        this.versionKeyL = minecraft == null ? "latest" : minecraft + "-latest";
        this.versionKeyC = minecraft == null ? "recommended" : minecraft + "-recommended";
    }

    @Override
    public VersionManifest.Basic findLatestVersion(HttpService http, boolean stable) throws IOException {
        JsonObject resp = this.gson.fromJson(http.fetch(this.url), JsonObject.class);

        if (resp.has("promos")) {
            JsonObject promos = resp.getAsJsonObject("promos");
            String key = (stable ? this.versionKeyC : this.versionKeyL).formatted(this.minecraft);
            if (promos.has(key)) {  // version found
                String version = promos.get(key).getAsString();

                boolean isStable = stable;
                if (!isStable && promos.has(this.versionKeyC)) { // check if same as stable version
                    isStable = promos.get(this.versionKeyC).getAsString().equals(version);
                }

                // find changelog
                String changelog = null;
                if (this.minecraft != null && resp.has(this.minecraft)) {
                    JsonObject changelogs = resp.getAsJsonObject(this.minecraft);
                    if (changelogs.has(key))
                        changelog = changelogs.get(key).getAsString();
                }


                return new VersionManifest.Basic(
                        version,
                        resp.has("homepage") ? resp.get("homepage").getAsString() : null,
                        changelog,
                        isStable
                );
            }
        } else throw new IllegalArgumentException("Response does not contains 'promos' key");

        return null;
    }

    @Override
    public VersionManifest.Basic findNewerVersion(HttpService http, String current, VersionFormat format, boolean stable) throws IOException {
        VersionManifest.Basic latest = this.findLatestVersion(http, stable);

        return latest != null && format.isNewerThan(latest.version(), current) ? latest : null;
    }
}
