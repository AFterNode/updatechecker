package cn.afternode.updatechecker.version;

import cn.afternode.updatechecker.VersionFormat;

import java.util.Locale;

/**
 * A simple <a href="https://semver.org/">SemVer</a> format parser
 */
public class SimpleSemVerFormat implements VersionFormat {
    @Override
    public boolean isNewerThan(String a, String b) {
        Parsed pa = this.parse(a);
        Parsed pb = this.parse(b);

        if (pa.major > pb.major) {
            return true;
        } else if (pa.major == pb.major) {
            if (pa.minor > pb.minor) {
                return true;
            } else if (pa.minor == pb.minor) {
                return pa.patch > pb.patch;
            }
        }

        return false;
    }

    /**
     * Parse SemVer string
     * @param version string
     * @return result
     */
    protected Parsed parse(String version) {
        String extras = null;
        int extrasSplit = version.indexOf('-');
        if (extrasSplit > 0) {
            extras = version.substring(extrasSplit + 1);
            version = version.substring(0, extrasSplit);
        }

        String[] parts = version.split("\\.");
        if (parts.length < 2)
            throw new IllegalArgumentException("SemVer too short");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int z = 0;
        if (parts.length >= 3)
            z = Integer.parseInt(parts[2]);

        return new Parsed(x, y, z, extras);
    }

    @Override
    public boolean isUnstable(String version) {
        version = version.toLowerCase(Locale.ROOT);
        return version.contains("snapshot") || version.contains("alpha") || version.contains("beta");
    }

    /**
     * SemVer data
     * @param major major
     * @param minor minor
     * @param patch patch
     * @param extras extra strings
     */
    protected record Parsed(
            int major,
            int minor,
            int patch,
            String extras
    ) {}
}
