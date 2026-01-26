/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;

public class IntCoord {
    private final int value;
    private final boolean height;
    private final boolean relative;
    private final boolean chunk;

    public IntCoord(int value, boolean height, boolean relative, boolean chunk) {
        this.value = value;
        this.height = height;
        this.relative = relative;
        this.chunk = chunk;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isNotBase() {
        return !this.height && !this.relative && !this.chunk;
    }

    public boolean isHeight() {
        return this.height;
    }

    public boolean isRelative() {
        return this.relative;
    }

    public boolean isChunk() {
        return this.chunk;
    }

    public int resolveXZ(int base) {
        return this.resolve(base);
    }

    public int resolveYAtWorldCoords(int base, @Nonnull ChunkStore chunkStore, int x, int z) {
        if (this.height) {
            long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
            Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
            if (chunkRef == null || !chunkRef.isValid()) {
                return this.resolve(base);
            }
            WorldChunk worldChunkComponent = chunkStore.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
            assert (worldChunkComponent != null);
            return this.resolve(worldChunkComponent.getHeight(x, z) + 1);
        }
        return this.resolve(base);
    }

    protected int resolve(int base) {
        int val = this.chunk ? this.value * 32 : this.value;
        return this.relative ? val + base : val;
    }

    /*
     * Unable to fully structure code
     */
    @Nonnull
    public static IntCoord parse(@Nonnull String str) {
        height = false;
        relative = false;
        chunk = false;
        index = 0;
        block5: while (true) lbl-1000:
        // 4 sources

        {
            switch (str.charAt(index)) {
                case '_': {
                    height = true;
                    if (str.length() != ++index) ** GOTO lbl-1000
                    return new IntCoord(0, true, relative, chunk);
                }
                case '~': {
                    relative = true;
                    if (str.length() != ++index) ** GOTO lbl-1000
                    return new IntCoord(0, height, true, chunk);
                }
                case 'c': {
                    chunk = true;
                    if (str.length() != ++index) continue block5;
                    return new IntCoord(0, height, relative, true);
                }
            }
            break;
        }
        rest = str.substring(index);
        return new IntCoord(Integer.parseInt(rest), height, relative, chunk);
    }
}

