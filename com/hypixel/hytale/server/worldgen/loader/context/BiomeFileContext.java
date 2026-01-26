/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.context;

import com.hypixel.hytale.server.worldgen.loader.context.FileContext;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class BiomeFileContext
extends FileContext<ZoneFileContext> {
    private final Type type;

    public BiomeFileContext(int id, String name, Path filepath, Type type, ZoneFileContext parent) {
        super(id, name, filepath, parent);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Nonnull
    public static Type getBiomeType(@Nonnull Path path) {
        String name = path.getFileName().toString();
        for (Type type : Type.values()) {
            if (!name.startsWith(type.prefix) || !name.endsWith(type.suffix)) continue;
            return type;
        }
        throw new Error("Unable to determine biome Type from file: " + String.valueOf(path));
    }

    public static enum Type {
        Tile("Tile.", "Tile Biome"),
        Custom("Custom.", "Custom Biome");

        private final String prefix;
        private final String suffix;
        private final String displayName;

        private Type(String prefix, String displayName) {
            this(prefix, ".json", displayName);
        }

        private Type(String prefix, String suffix, String displayName) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.displayName = displayName;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public String getDisplayName() {
            return this.displayName;
        }
    }
}

