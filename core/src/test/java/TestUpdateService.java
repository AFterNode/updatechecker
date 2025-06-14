import cn.afternode.updatechecker.VersionManifest;
import cn.afternode.updatechecker.http.SimpleHttpService;
import cn.afternode.updatechecker.service.ForgeUpdateService;
import cn.afternode.updatechecker.service.ModrinthUpdateService;
import cn.afternode.updatechecker.service.SpigetUpdateService;
import cn.afternode.updatechecker.version.SimpleSemVerFormat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;

public class TestUpdateService {
    private final SimpleHttpService http = new SimpleHttpService(HttpClient.newHttpClient());

    @Test
    public void testModrinth() throws IOException {
        System.out.println("=== Modrinth ===");
        ModrinthUpdateService service = new ModrinthUpdateService("chunky", "paper", "1.21.5");
        ModrinthUpdateService.Version latestVersion = service.findLatestVersion(http, true);
        System.out.println(latestVersion);
        System.out.println(service.findNewerVersion(http, "1.4.23", new SimpleSemVerFormat(), true));
    }

    @Test
    public void testSpiget() throws IOException {
        System.out.println("=== Spiget ===");
        SpigetUpdateService spiget = new SpigetUpdateService(125992);
        SpigetUpdateService.Version version = spiget.findLatestVersion(http, true);
        System.out.println(version);
        System.out.println(spiget.findNewerVersion(http, "2.5", new SimpleSemVerFormat(), true));
    }

    @Test
    public void testForge() throws IOException {
        System.out.println("=== Forge ===");
        ForgeUpdateService service = new ForgeUpdateService("https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json", "1.21.4");
        VersionManifest.Basic version = service.findLatestVersion(http, true);
        System.out.println(version);
        System.out.println(service.findNewerVersion(http, version.version(), new SimpleSemVerFormat(), false));
    }
}
