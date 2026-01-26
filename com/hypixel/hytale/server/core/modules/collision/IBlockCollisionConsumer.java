/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.collision.BlockContactData;
import com.hypixel.hytale.server.core.modules.collision.BlockData;

public interface IBlockCollisionConsumer {
    public Result onCollision(int var1, int var2, int var3, Vector3d var4, BlockContactData var5, BlockData var6, Box var7);

    public Result probeCollisionDamage(int var1, int var2, int var3, Vector3d var4, BlockContactData var5, BlockData var6);

    public void onCollisionDamage(int var1, int var2, int var3, Vector3d var4, BlockContactData var5, BlockData var6);

    public Result onCollisionSliceFinished();

    public void onCollisionFinished();

    public static enum Result {
        CONTINUE,
        STOP,
        STOP_NOW;

    }
}

