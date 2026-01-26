/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.physics.util;

import com.hypixel.hytale.server.core.modules.physics.util.ForceAccumulator;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsBodyState;

public interface ForceProvider {
    public void update(PhysicsBodyState var1, ForceAccumulator var2, boolean var3);
}

