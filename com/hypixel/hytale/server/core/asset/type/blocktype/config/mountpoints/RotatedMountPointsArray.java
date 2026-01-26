/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.mountpoints;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.function.FunctionCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.mountpoints.BlockMountPoint;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import javax.annotation.Nullable;

public class RotatedMountPointsArray {
    private static final ArrayCodec<BlockMountPoint> CHILD = new ArrayCodec<BlockMountPoint>(BlockMountPoint.CODEC, BlockMountPoint[]::new);
    public static final Codec<RotatedMountPointsArray> CODEC = new FunctionCodec<BlockMountPoint[], RotatedMountPointsArray>(CHILD, RotatedMountPointsArray::new, RotatedMountPointsArray::getRaw);
    private BlockMountPoint[] raw;
    private transient BlockMountPoint[][] rotated;

    public RotatedMountPointsArray() {
    }

    public RotatedMountPointsArray(BlockMountPoint[] raw) {
        this.raw = raw;
    }

    public int size() {
        return this.raw == null ? 0 : this.raw.length;
    }

    public BlockMountPoint[] getRaw() {
        return this.raw;
    }

    @Nullable
    public BlockMountPoint[] getRotated(int rotationIndex) {
        BlockMountPoint[] value;
        if (this.raw == null || rotationIndex == 0) {
            return this.raw;
        }
        if (this.rotated == null) {
            this.rotated = new BlockMountPoint[RotationTuple.VALUES.length][];
        }
        if ((value = this.rotated[rotationIndex]) == null) {
            RotationTuple rotation = RotationTuple.get(rotationIndex);
            ObjectArrayList list = new ObjectArrayList();
            for (BlockMountPoint mountPoint : this.raw) {
                BlockMountPoint rotated = mountPoint.rotate(rotation.yaw(), rotation.pitch(), rotation.roll());
                list.add(rotated);
            }
            value = (BlockMountPoint[])list.toArray(BlockMountPoint[]::new);
            this.rotated[rotationIndex] = value;
        }
        return value;
    }
}

