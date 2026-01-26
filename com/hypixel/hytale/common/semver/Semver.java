/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.semver;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.function.FunctionCodec;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.common.util.StringUtil;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Semver
implements Comparable<Semver> {
    public static final Codec<Semver> CODEC = new FunctionCodec<String, Semver>(Codec.STRING, Semver::fromString, Semver::toString);
    private final long major;
    private final long minor;
    private final long patch;
    private final String[] preRelease;
    private final String build;

    public Semver(long major, long minor, long patch) {
        this(major, minor, patch, null, null);
    }

    public Semver(long major, long minor, long patch, String[] preRelease, String build) {
        if (major < 0L) {
            throw new IllegalArgumentException("Major must be a non-negative integers");
        }
        if (minor < 0L) {
            throw new IllegalArgumentException("Major must be a non-negative integers");
        }
        if (patch < 0L) {
            throw new IllegalArgumentException("Major must be a non-negative integers");
        }
        Semver.validatePreRelease(preRelease);
        Semver.validateBuild(build);
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.build = build;
    }

    public long getMajor() {
        return this.major;
    }

    public long getMinor() {
        return this.minor;
    }

    public long getPatch() {
        return this.patch;
    }

    public String[] getPreRelease() {
        return (String[])this.preRelease.clone();
    }

    public String getBuild() {
        return this.build;
    }

    public boolean satisfies(@Nonnull SemverRange range) {
        return range.satisfies(this);
    }

    @Override
    public int compareTo(@Nonnull Semver other) {
        int i;
        if (this.major != other.major) {
            return Long.compare(this.major, other.major);
        }
        if (this.minor != other.minor) {
            return Long.compare(this.minor, other.minor);
        }
        if (this.patch != other.patch) {
            return Long.compare(this.patch, other.patch);
        }
        if (this.preRelease != null && (other.preRelease == null || other.preRelease.length == 0)) {
            return -1;
        }
        if ((this.preRelease == null || this.preRelease.length == 0) && other.preRelease != null) {
            return 1;
        }
        if (this.preRelease == null) {
            return 0;
        }
        for (i = 0; i < this.preRelease.length && i < other.preRelease.length; ++i) {
            int compare;
            String pre = this.preRelease[i];
            String otherPre = other.preRelease[i];
            if (!(StringUtil.isNumericString(pre) && StringUtil.isNumericString(otherPre) ? (compare = Integer.compare(Integer.parseInt(pre), Integer.parseInt(otherPre))) != 0 : (compare = pre.compareTo(otherPre)) != 0)) continue;
            return compare;
        }
        if (this.preRelease.length > i) {
            return 1;
        }
        if (other.preRelease.length > i) {
            return -1;
        }
        return 0;
    }

    @Nonnull
    public String toString() {
        StringBuilder ver = new StringBuilder().append(this.major).append('.').append(this.minor).append('.').append(this.patch);
        if (this.preRelease != null && this.preRelease.length > 0) {
            ver.append('-').append(String.join((CharSequence)".", this.preRelease));
        }
        if (this.build != null && !this.build.isEmpty()) {
            ver.append('+').append(this.build);
        }
        return ver.toString();
    }

    @Nonnull
    public static Semver fromString(String str) {
        return Semver.fromString(str, false);
    }

    @Nonnull
    public static Semver fromString(String str, boolean strict) {
        long patch;
        Objects.requireNonNull(str, "String can't be null!");
        str = str.trim();
        if (str.isEmpty()) {
            throw new IllegalArgumentException("String is empty!");
        }
        if (str.charAt(0) == '=' || str.charAt(0) == 'v') {
            str = str.substring(1);
        }
        if (str.charAt(0) == '=' || str.charAt(0) == 'v') {
            str = str.substring(1);
        }
        if ((str = str.trim()).isEmpty()) {
            throw new IllegalArgumentException("String is empty!");
        }
        String build = null;
        if (str.contains("+")) {
            String[] buildSplit = str.split("\\+", 2);
            str = buildSplit[0];
            build = buildSplit[1];
            Semver.validateBuild(build);
        }
        String[] preRelease = null;
        if (str.contains("-")) {
            String[] preReleaseSplit = str.split("-", 2);
            str = preReleaseSplit[0];
            preRelease = preReleaseSplit[1].split("\\.");
            Semver.validatePreRelease(preRelease);
        }
        if (!(str.isEmpty() || str.charAt(0) != '.' && str.charAt(str.length() - 1) != '.')) {
            throw new IllegalArgumentException("Failed to parse digits (" + str + ")");
        }
        String[] split = str.split("\\.");
        if (split.length < 1) {
            throw new IllegalArgumentException("String doesn't match <major>.<minor>.<patch> (" + str + ")");
        }
        long major = Long.parseLong(split[0]);
        if (major < 0L) {
            throw new IllegalArgumentException("Major must be a non-negative integers (" + str + ")");
        }
        if (!strict && split.length == 1) {
            return new Semver(major, 0L, 0L, preRelease, build);
        }
        if (split.length < 2) {
            throw new IllegalArgumentException("String doesn't match <major>.<minor>.<patch> (" + str + ")");
        }
        long minor = Long.parseLong(split[1]);
        if (minor < 0L) {
            throw new IllegalArgumentException("Minor must be a non-negative integers (" + str + ")");
        }
        if (!strict && split.length == 2) {
            return new Semver(major, minor, 0L, preRelease, build);
        }
        if (split.length != 3) {
            throw new IllegalArgumentException("String doesn't match <major>.<minor>.<patch> (" + str + ")");
        }
        String patchStr = split[2];
        if (!strict && preRelease == null) {
            String pre = "";
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < patchStr.length(); ++i) {
                char c = patchStr.charAt(i);
                if (!Character.isDigit(c)) {
                    pre = patchStr.substring(i);
                    patchStr = s.toString();
                    break;
                }
                s.append(c);
            }
            if (!pre.trim().isEmpty()) {
                preRelease = pre.split("\\.");
                Semver.validatePreRelease(preRelease);
            }
        }
        if ((patch = Long.parseLong(patchStr)) < 0L) {
            throw new IllegalArgumentException("Patch must be a non-negative integers (" + str + ")");
        }
        return new Semver(major, minor, patch, preRelease, build);
    }

    private static void validateBuild(@Nullable String build) {
        if (build == null) {
            return;
        }
        if (build.isEmpty() || !StringUtil.isAlphaNumericHyphenString(build)) {
            throw new IllegalArgumentException("Build must only be alphanumeric (" + build + ")");
        }
    }

    private static void validatePreRelease(@Nullable String[] preRelease) {
        if (preRelease == null) {
            return;
        }
        for (String preReleasePart : preRelease) {
            if (!preReleasePart.isEmpty() && StringUtil.isAlphaNumericHyphenString(preReleasePart)) continue;
            throw new IllegalArgumentException("Pre-release must only be alphanumeric (" + Arrays.toString(preRelease) + ")");
        }
    }
}

