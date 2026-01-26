/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.collision.BasicCollisionData;
import javax.annotation.Nonnull;

public class BoxCollisionData
extends BasicCollisionData {
    public double collisionEnd;
    public final Vector3d collisionNormal = new Vector3d();

    public void setEnd(double collisionEnd, @Nonnull Vector3d collisionNormal) {
        this.collisionEnd = collisionEnd;
        this.collisionNormal.assign(collisionNormal);
    }
}

