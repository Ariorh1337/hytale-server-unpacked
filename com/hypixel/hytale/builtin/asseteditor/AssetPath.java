/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor;

import com.hypixel.hytale.common.util.PathUtil;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public record AssetPath(@Nonnull String packId, @Nonnull Path path) {
    public static final AssetPath EMPTY_PATH = new AssetPath("", Path.of("", new String[0]));

    public AssetPath(com.hypixel.hytale.protocol.packets.asseteditor.AssetPath assetPath) {
        this(assetPath.pack, Path.of(assetPath.path, new String[0]));
    }

    public com.hypixel.hytale.protocol.packets.asseteditor.AssetPath toPacket() {
        return new com.hypixel.hytale.protocol.packets.asseteditor.AssetPath(this.packId, PathUtil.toUnixPathString(this.path));
    }
}

