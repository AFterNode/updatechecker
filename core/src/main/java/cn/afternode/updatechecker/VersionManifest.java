package cn.afternode.updatechecker;

public interface VersionManifest {
    String version();
    String url();
    String changelog();

    boolean stable();

    record Basic(
            String version,
            String url,
            String changelog,
            boolean stable
    ) implements VersionManifest {}
}
