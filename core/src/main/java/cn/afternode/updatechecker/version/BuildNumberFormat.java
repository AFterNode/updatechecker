package cn.afternode.updatechecker.version;

import cn.afternode.updatechecker.VersionFormat;

/**
 * A build number version parser for <code>b123-alpha</code> like versions
 */
public class BuildNumberFormat implements VersionFormat {
    @Override
    public boolean isNewerThan(String a, String b) {
        return parse(a) > parse(b);
    }

    /**
     * Parse integer only
     * @param version version string
     * @return result
     */
    protected int parse(String version) {
        StringBuilder digits = new StringBuilder();
        boolean abPassed = false;
        for (char c : version.toCharArray()) {
            try {
                Integer.parseInt(String.valueOf(c));
                digits.append(c);
            } catch (NumberFormatException e) {
                if (abPassed)
                    break;
                abPassed = true;
            }
        }

        return Integer.parseInt(digits.toString());
    }

    @Override
    public boolean isUnstable(String version) {
        return version.contains("b") || version.contains("a");
    }
}
