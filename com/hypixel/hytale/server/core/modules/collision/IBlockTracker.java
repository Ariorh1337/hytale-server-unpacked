/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.math.vector.Vector3i;

public interface IBlockTracker {
    public Vector3i getPosition(int var1);

    public int getCount();

    public boolean track(int var1, int var2, int var3);

    public void trackNew(int var1, int var2, int var3);

    public boolean isTracked(int var1, int var2, int var3);

    public void untrack(int var1, int var2, int var3);
}

