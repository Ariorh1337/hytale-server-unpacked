/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.modules.collision.BoxCollisionData;
import com.hypixel.hytale.server.core.modules.collision.CollisionConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockCollisionData
extends BoxCollisionData {
    public int x;
    public int y;
    public int z;
    public int blockId;
    public int rotation;
    @Nullable
    public BlockType blockType;
    @Nullable
    public BlockMaterial blockMaterial;
    public int detailBoxIndex;
    public boolean willDamage;
    public int fluidId;
    @Nullable
    public Fluid fluid;
    public boolean touching;
    public boolean overlapping;

    public void setBlockData(@Nonnull CollisionConfig collisionConfig) {
        this.x = collisionConfig.blockX;
        this.y = collisionConfig.blockY;
        this.z = collisionConfig.blockZ;
        this.blockId = collisionConfig.blockId;
        this.rotation = collisionConfig.rotation;
        this.blockType = collisionConfig.blockType;
        this.blockMaterial = collisionConfig.blockMaterial;
        this.willDamage = (collisionConfig.blockMaterialMask & 0x10) != 0;
        this.fluidId = collisionConfig.fluidId;
        this.fluid = collisionConfig.fluid;
    }

    public void setDetailBoxIndex(int detailBoxIndex) {
        this.detailBoxIndex = detailBoxIndex;
    }

    public void setTouchingOverlapping(boolean touching, boolean overlapping) {
        this.touching = touching;
        this.overlapping = overlapping;
    }

    public void clear() {
        this.blockType = null;
        this.blockMaterial = null;
    }

    @Nonnull
    public String toString() {
        return "BlockCollisionData{x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", blockId=" + this.blockId + ", blockType=" + String.valueOf(this.blockType) + ", blockMaterial=" + String.valueOf((Object)this.blockMaterial) + ", collisionPoint=" + String.valueOf(this.collisionPoint) + ", collisionNormal=" + String.valueOf(this.collisionNormal) + ", collisionStart=" + this.collisionStart + ", collisionEnd=" + this.collisionEnd + ", detailBoxIndex=" + this.detailBoxIndex + "}";
    }
}

