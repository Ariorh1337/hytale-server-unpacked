/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.common.util.StringCompareUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MatchResult
implements Comparable<MatchResult> {
    public static final MatchResult NONE = new MatchResult(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    public static final MatchResult EXACT = new MatchResult(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final int NAME = 0;
    public static final int ALIAS = 1;
    public static final int USAGE_ARG = 3;
    public static final int DESCRIPTION = 4;
    public static final int USAGE_DESCRIPTION = 5;
    private final int term;
    private final int depth;
    private final int type;
    private final int match;

    @Nonnull
    public static MatchResult of(int termDepth, int depth, int type, @Nonnull String text, @Nonnull String search) {
        return new MatchResult(termDepth, depth, type, StringCompareUtil.getLevenshteinDistance(text, search));
    }

    public MatchResult(int term, int depth, int type, int match) {
        this.term = term;
        this.depth = depth;
        this.type = type;
        this.match = match;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getType() {
        return this.type;
    }

    public int getMatch() {
        return this.match;
    }

    @Nonnull
    public MatchResult min(@Nonnull MatchResult other) {
        if (this.term < other.term) {
            return this;
        }
        if (this.term > other.term) {
            return other;
        }
        if (this.type < other.type) {
            return this;
        }
        if (this.type > other.type) {
            return other;
        }
        if (this.depth < other.depth) {
            return this;
        }
        if (this.depth > other.depth) {
            return other;
        }
        if (this.match < other.match) {
            return this;
        }
        if (this.match > other.match) {
            return other;
        }
        return this;
    }

    @Override
    public int compareTo(@Nonnull MatchResult o) {
        if (o.term != this.term) {
            return this.term - o.term;
        }
        if (o.type != this.type) {
            return this.type - o.type;
        }
        if (o.depth != this.depth) {
            return this.depth - o.depth;
        }
        return this.match - o.match;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MatchResult that = (MatchResult)o;
        if (this.depth != that.depth) {
            return false;
        }
        return this.match == that.match;
    }

    public int hashCode() {
        int result = this.depth;
        result = 31 * result + this.match;
        return result;
    }

    @Nonnull
    public String toString() {
        return "MatchResult{depth=" + this.depth + ", match=" + this.match + "}";
    }
}

