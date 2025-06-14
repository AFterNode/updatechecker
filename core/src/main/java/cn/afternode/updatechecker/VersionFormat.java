package cn.afternode.updatechecker;

/**
 * Version format base
 */
public interface VersionFormat {
    /**
     * Check if A is newer than B
     * @param a version A
     * @param b version B
     * @return result
     */
    boolean isNewerThan(String a, String b);

    /**
     * Check if version is unstable
     * @param version version
     * @return result
     */
    boolean isUnstable(String version);
}
