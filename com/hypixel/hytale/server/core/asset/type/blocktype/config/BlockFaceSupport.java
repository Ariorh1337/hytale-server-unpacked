/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class BlockFaceSupport
implements NetworkSerializable<com.hypixel.hytale.protocol.BlockFaceSupport> {
    public static final BuilderCodec<BlockFaceSupport> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockFaceSupport.class, BlockFaceSupport::new).append(new KeyedCodec<String>("FaceType", Codec.STRING), (blockFaceSupport, s) -> {
        blockFaceSupport.faceType = s;
    }, blockFaceSupport -> blockFaceSupport.faceType).add()).documentation("Can be any string. Compared with FaceType in \"Support\". A LOT of blocks use 'Full'.")).append(new KeyedCodec<T[]>("Filler", new ArrayCodec<com.hypixel.hytale.math.vector.Vector3i>(com.hypixel.hytale.math.vector.Vector3i.CODEC, com.hypixel.hytale.math.vector.Vector3i[]::new)), (blockFaceSupport, o) -> {
        blockFaceSupport.filler = o;
    }, blockFaceSupport -> blockFaceSupport.filler).add()).build();
    public static final BlockFaceSupport ALL = new BlockFaceSupport();
    public static final String FULL_SUPPORTING_FACE = "Full";
    protected String faceType = "Full";
    protected com.hypixel.hytale.math.vector.Vector3i[] filler;

    public BlockFaceSupport() {
    }

    public BlockFaceSupport(String faceType, com.hypixel.hytale.math.vector.Vector3i[] filler) {
        this.faceType = faceType;
        this.filler = filler;
    }

    public String getFaceType() {
        return this.faceType;
    }

    public com.hypixel.hytale.math.vector.Vector3i[] getFiller() {
        return this.filler;
    }

    public boolean providesSupportFromFiller(com.hypixel.hytale.math.vector.Vector3i filler) {
        return this.filler == null || ArrayUtil.contains(this.filler, filler);
    }

    @Nonnull
    public String toString() {
        return "BlockFaceSupport{faceType='" + this.faceType + "', filler=" + Arrays.toString(this.filler) + "}";
    }

    @Nonnull
    public static BlockFaceSupport rotate(@Nonnull BlockFaceSupport original, @Nonnull Rotation rotationYaw, @Nonnull Rotation rotationPitch, @Nonnull Rotation roll) {
        if (original == ALL) {
            return ALL;
        }
        com.hypixel.hytale.math.vector.Vector3i[] rotatedFiller = ArrayUtil.copyAndMutate(original.filler, vector -> Rotation.rotate(vector, rotationYaw, rotationPitch, roll), com.hypixel.hytale.math.vector.Vector3i[]::new);
        return new BlockFaceSupport(original.faceType, rotatedFiller);
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.BlockFaceSupport toPacket() {
        com.hypixel.hytale.protocol.BlockFaceSupport protocolBlockFaceSupport = new com.hypixel.hytale.protocol.BlockFaceSupport();
        protocolBlockFaceSupport.faceType = this.faceType;
        if (this.filler != null) {
            Vector3i[] filler = new Vector3i[this.filler.length];
            for (int j = 0; j < this.filler.length; ++j) {
                com.hypixel.hytale.math.vector.Vector3i fillerVector = this.filler[j];
                filler[j] = new Vector3i(fillerVector.x, fillerVector.y, fillerVector.z);
            }
            protocolBlockFaceSupport.filler = filler;
        }
        return protocolBlockFaceSupport;
    }
}

