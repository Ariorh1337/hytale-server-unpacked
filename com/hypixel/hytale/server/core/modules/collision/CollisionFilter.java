/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.server.core.modules.collision.CollisionConfig;

@FunctionalInterface
public interface CollisionFilter<D, T> {
    public boolean test(T var1, int var2, D var3, CollisionConfig var4);
}

