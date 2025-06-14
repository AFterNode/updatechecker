package cn.afternode.updatechecker;

public interface VersionFormat {
    boolean isNewerThan(String a, String b);

    boolean isUnstable(String version);
}
