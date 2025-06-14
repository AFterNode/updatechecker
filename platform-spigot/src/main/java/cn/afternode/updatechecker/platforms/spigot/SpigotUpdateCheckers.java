package cn.afternode.updatechecker.platforms.spigot;

import cn.afternode.updatechecker.UpdateService;
import cn.afternode.updatechecker.VersionFormat;
import cn.afternode.updatechecker.VersionManifest;
import cn.afternode.updatechecker.http.HCHttpService;
import cn.afternode.updatechecker.http.HttpService;
import cn.afternode.updatechecker.service.ForgeUpdateService;
import cn.afternode.updatechecker.service.ModrinthUpdateService;
import cn.afternode.updatechecker.service.SpigetUpdateService;
import cn.afternode.updatechecker.version.SimpleSemVerFormat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Platform utilities for Spigot
 */
public class SpigotUpdateCheckers {
    /**
     * Create Modrinth checker with specified slug
     * @param slug project slug
     * @return service
     */
    public static ModrinthUpdateService newModrinthChecker(String slug) {
        return new ModrinthUpdateService(slug, "spigot", Bukkit.getServer().getVersion());
    }

    /**
     * Create Modrinth checker with plugin name in lowercase as slug
     * @param plugin plugin
     * @return service
     */
    public static ModrinthUpdateService newModrinthChecker(Plugin plugin) {
        return newModrinthChecker(plugin.getName().toLowerCase(Locale.ROOT));
    }

    /**
     * Create Spiget checker with specified resource ID
     * @param resource resource ID
     * @return service
     */
    public static SpigetUpdateService newSpigetChecker(int resource) {
        return new SpigetUpdateService(resource);
    }

    /**
     * Create Forge checker with specified URL
     * @param url url
     * @return service
     */
    public static ForgeUpdateService newForgeChecker(String url) {
        return new ForgeUpdateService(url, Bukkit.getServer().getVersion());
    }

    /**
     * Check update now on async scheduler
     * @param plugin Plugin instance for {@link org.bukkit.scheduler.BukkitScheduler#runTaskAsynchronously(Plugin, Runnable)}
     * @param versionFormat Version format for {@link UpdateService#findNewerVersion(HttpService, String, VersionFormat, boolean)}
     * @param service Service for checking
     * @param stable Check stable versions only
     * @param handler Result handler
     * @return Created bukkit task
     * @param <V> Version manifest type
     */
    public static <V extends VersionManifest> BukkitTask checkNow(Plugin plugin, VersionFormat versionFormat, UpdateService<V> service, boolean stable, Consumer<VersionManifest> handler) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                handler.accept(service.findNewerVersion(SpigotUpdateCheckers.newHttpService(), plugin.getDescription().getVersion(), versionFormat, stable));
            } catch (Throwable t) {
                plugin.getLogger().warning("Error checking update: " + t);
            }
        });
    }

    /**
     * Check stable version update now on async scheduler
     * @param plugin Plugin instance for {@link org.bukkit.scheduler.BukkitScheduler#runTaskAsynchronously(Plugin, Runnable)}
     * @param versionFormat Version format for {@link UpdateService#findNewerVersion(HttpService, String, VersionFormat, boolean)}
     * @param service Service for checking
     * @param handler Result handler
     * @return Created bukkit task
     * @param <V> Version manifest type
     */
    public static <V extends VersionManifest> BukkitTask checkNow(Plugin plugin, VersionFormat versionFormat, UpdateService<V> service, Consumer<VersionManifest> handler) {
        return SpigotUpdateCheckers.checkNow(plugin, versionFormat, service, true, handler);
    }

    /**
     * Run update checker with period
     * @param plugin Plugin instance for {@link org.bukkit.scheduler.BukkitScheduler#runTaskAsynchronously(Plugin, Runnable)}
     * @param versionFormat Version format for {@link UpdateService#findNewerVersion(HttpService, String, VersionFormat, boolean)}
     * @param service Service for checking
     * @param stable Check stable versions only
     * @param seconds Period seconds
     * @param handler  Result handler
     * @return Created bukkit task
     * @param <V> Version manifest type
     */
    public static <V extends VersionManifest> BukkitTask newPeriodCheck(Plugin plugin, VersionFormat versionFormat, UpdateService<V> service, boolean stable, long seconds, Consumer<VersionManifest> handler) {
        final HttpService http = SpigotUpdateCheckers.newHttpService();
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                handler.accept(service.findNewerVersion(http, plugin.getDescription().getVersion(), versionFormat, stable));
            } catch (Throwable t) {
                plugin.getLogger().warning("Error checking update: " + t);
            }
        }, seconds*20, 0);
    }

    /**
     * Run stable version update checker with period
     * @param plugin Plugin instance for {@link org.bukkit.scheduler.BukkitScheduler#runTaskAsynchronously(Plugin, Runnable)}
     * @param versionFormat Version format for {@link UpdateService#findNewerVersion(HttpService, String, VersionFormat, boolean)}
     * @param service Service for checking
     * @param seconds Period seconds
     * @param handler  Result handler
     * @return Created bukkit task
     * @param <V> Version manifest type
     */
    public static <V extends VersionManifest> BukkitTask newPeriodCheck(Plugin plugin, VersionFormat versionFormat, UpdateService<V> service, long seconds, Consumer<VersionManifest> handler) {
        return SpigotUpdateCheckers.newPeriodCheck(plugin, versionFormat, service, true, seconds, handler);
    }

    /**
     * Run stable version update checker with period with SemVer format
     * @param plugin Plugin instance for {@link org.bukkit.scheduler.BukkitScheduler#runTaskAsynchronously(Plugin, Runnable)}
     * @param service Service for checking
     * @param seconds Period seconds
     * @param handler  Result handler
     * @return Created bukkit task
     * @param <V> Version manifest type
     */
    public static <V extends VersionManifest> BukkitTask newPeriodCheck(Plugin plugin, UpdateService<V> service, long seconds, Consumer<VersionManifest> handler) {
        return SpigotUpdateCheckers.newPeriodCheck(plugin, new SimpleSemVerFormat(), service, seconds, handler);
    }

    /**
     * Create HttpService for this platform
     * @return Created service
     */
    public static HttpService newHttpService() {
        return new HCHttpService();
    }
}
