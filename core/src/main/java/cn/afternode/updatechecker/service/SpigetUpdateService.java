package cn.afternode.updatechecker.service;

import cn.afternode.updatechecker.UpdateService;
import cn.afternode.updatechecker.VersionFormat;
import cn.afternode.updatechecker.VersionManifest;
import cn.afternode.updatechecker.http.HttpService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.UUID;

/**
 * Update checker uses <a href="https://spiget.org">Spiget</a>
 */
public class SpigetUpdateService implements UpdateService<SpigetUpdateService.Version> {
    private final Gson gson = new Gson();

    private final int resource;

    public SpigetUpdateService(int resource) {
        this.resource = resource;
    }

    @Override
    public Version findLatestVersion(HttpService http, boolean stable) throws IOException {
        String fetch = http.fetch("https://api.spiget.org/v2/resources/%s/versions/latest".formatted(this.resource));
        JsonObject response = this.gson.fromJson(fetch, JsonObject.class);

        return new Version(
                response.get("name").getAsString(),
                UUID.fromString(response.get("uuid").getAsString())
        );
    }

    @Override
    public Version findNewerVersion(HttpService http, String current, VersionFormat format, boolean stable) throws IOException {
        Version latest = this.findLatestVersion(http, stable);
        if (format.isNewerThan(latest.version, current))
            return latest;
        return null;
    }

    public String getDownloadUrl(HttpService http, Version version) {
        return "https://spiget.org/resources/%s/versions/%s/download".formatted(this.resource, version.uuid);
    }

    public String getLatestDownloadUrl(HttpService http) {
        return "https://spiget.org/resources/%s/versions/latest/download".formatted(this.resource);
    }

    public record Version(String version, UUID uuid) implements VersionManifest {
        @Override
        public String url() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String changelog() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean stable() {
            return true;
        }
    }
}
