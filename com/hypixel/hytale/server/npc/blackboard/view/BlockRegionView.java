/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.npc.blackboard.view.IBlackboardView;
import javax.annotation.Nonnull;

public abstract class BlockRegionView<ViewType extends IBlackboardView<ViewType>>
implements IBlackboardView<ViewType> {
    public static final int BITS = 7;
    public static final int SIZE = 128;
    public static final int SIZE_MASK = 127;
    public static final int BITS2 = 14;

    public static int toRegionalBlackboardCoordinate(int pos) {
        return pos >> 7;
    }

    public static int toWorldCoordinate(int pos) {
        return pos << 7;
    }

    public static int chunkToRegionalBlackboardCoordinate(int pos) {
        return pos >> 2;
    }

    public static long indexView(int x, int z) {
        return ChunkUtil.indexChunk(x, z);
    }

    public static int indexSection(int y) {
        return y >> 7;
    }

    public static int xOfViewIndex(long index) {
        return ChunkUtil.xOfChunkIndex(index);
    }

    public static int zOfViewIndex(long index) {
        return ChunkUtil.zOfChunkIndex(index);
    }

    public static long indexViewFromChunkCoordinates(int x, int z) {
        return BlockRegionView.indexView(BlockRegionView.toRegionalBlackboardCoordinate(x), BlockRegionView.toRegionalBlackboardCoordinate(z));
    }

    public static long indexViewFromWorldPosition(@Nonnull Vector3d pos) {
        int blackboardX = BlockRegionView.toRegionalBlackboardCoordinate(MathUtil.floor(pos.getX()));
        int blackboardZ = BlockRegionView.toRegionalBlackboardCoordinate(MathUtil.floor(pos.getZ()));
        return BlockRegionView.indexView(blackboardX, blackboardZ);
    }

    public static int indexBlock(int x, int y, int z) {
        return (y & 0x7F) << 14 | (z & 0x7F) << 7 | x & 0x7F;
    }

    public static int xFromIndex(int index) {
        return index & 0x7F;
    }

    public static int yFromIndex(int index) {
        return index >> 14 & 0x7F;
    }

    public static int zFromIndex(int index) {
        return index >> 7 & 0x7F;
    }
}

