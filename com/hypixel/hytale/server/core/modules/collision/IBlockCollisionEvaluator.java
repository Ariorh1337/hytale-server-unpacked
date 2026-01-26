/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.server.core.modules.collision.BlockCollisionData;
import com.hypixel.hytale.server.core.modules.collision.CollisionConfig;

public interface IBlockCollisionEvaluator {
    public double getCollisionStart();

    public void setCollisionData(BlockCollisionData var1, CollisionConfig var2, int var3);
}

