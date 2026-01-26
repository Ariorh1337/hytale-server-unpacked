/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.plugin;

import com.hypixel.hytale.common.plugin.PluginManifest;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PluginIdentifier {
    @Nonnull
    private final String group;
    @Nonnull
    private final String name;

    public PluginIdentifier(@Nonnull String group, @Nonnull String name) {
        this.group = group;
        this.name = name;
    }

    public PluginIdentifier(@Nonnull PluginManifest manifest) {
        this(manifest.getGroup(), manifest.getName());
    }

    @Nonnull
    public String getGroup() {
        return this.group;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public int hashCode() {
        int result = this.group.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PluginIdentifier that = (PluginIdentifier)o;
        if (!Objects.equals(this.group, that.group)) {
            return false;
        }
        return Objects.equals(this.name, that.name);
    }

    @Nonnull
    public String toString() {
        return this.group + ":" + this.name;
    }

    @Nonnull
    public static PluginIdentifier fromString(@Nonnull String str) {
        String[] split = str.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("String does not match <group>:<name>");
        }
        return new PluginIdentifier(split[0], split[1]);
    }
}

