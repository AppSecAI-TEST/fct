package com.fct.web.admin.http.support.version;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ningyang
 */
public final class Version implements Comparable<Version> {
    int major;
    int minor;
    int build;

    public Version(String version, boolean isFrom) {
        if (StringUtils.equals("0", version)) {
            this.major = 0;
            this.minor = 0;
            this.build = 0;
        } else if (StringUtils.equals("999", version)) {
            this.major = 999;
            this.minor = 999;
            this.build = 999;
        } else {
            String[] versionArray = StringUtils.split(version, "\\.");
            if (versionArray.length < 2) {
                throw new RuntimeException("wrong version syntax, version must like x.y or x.y.z");
            }
            major = Integer.valueOf(versionArray[0]);
            minor = Integer.valueOf(versionArray[1]);
            if (versionArray.length == 3) {
                build = Integer.valueOf(versionArray[2]);
            } else {
                build = isFrom ? 0 : 999;
            }
        }
    }

    @Override
    public int compareTo(Version that) {
        Preconditions.checkNotNull(that);
        return ComparisonChain.start()
                .compare(this.major, that.major)
                .compare(this.minor, that.minor)
                .compare(this.build, that.build)
                .result();
    }

    public int compareTo(String that) {
        return this.compareTo(new Version(that, true));// deal with 2.3 as 2.3.0
    }
}
