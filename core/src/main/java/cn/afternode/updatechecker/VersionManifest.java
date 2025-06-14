package cn.afternode.updatechecker;

/**
 * Version manifest base
 */
public interface VersionManifest {
    /**
     * Version code
     * @return code
     */
    String version();

    /**
     * Download or information URL
     * @return url
     */
    String url();

    /**
     * Changelog content
     * @return changelog
     */
    String changelog();

    /**
     * Stability
     * @return stability
     */
    boolean stable();

    /**
     * Basic version manifest
     * @param version version
     * @param url url
     * @param changelog changelog
     * @param stable stable
     */
    record Basic(
            String version,
            String url,
            String changelog,
            boolean stable
    ) implements VersionManifest {}
}
