/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.data;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StringTag
implements CollectorTag {
    private final String tag;

    private StringTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StringTag stringTag = (StringTag)o;
        return this.tag != null ? this.tag.equals(stringTag.tag) : stringTag.tag == null;
    }

    public int hashCode() {
        return this.tag != null ? this.tag.hashCode() : 0;
    }

    @Nonnull
    public String toString() {
        return "StringTag{tag='" + this.tag + "'}";
    }

    @Nonnull
    public static StringTag of(String tag) {
        return new StringTag(tag);
    }
}

