/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.item;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

@Deprecated
public class ItemPhysicsComponent
implements Component<EntityStore> {
    public Vector3d scaledVelocity = new Vector3d();
    public CollisionResult collisionResult = new CollisionResult();

    public static ComponentType<EntityStore, ItemPhysicsComponent> getComponentType() {
        return EntityModule.get().getItemPhysicsComponentType();
    }

    public ItemPhysicsComponent() {
    }

    public ItemPhysicsComponent(Vector3d scaledVelocity, CollisionResult collisionResult) {
        this.scaledVelocity = scaledVelocity;
        this.collisionResult = collisionResult;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        return new ItemPhysicsComponent(this.scaledVelocity, this.collisionResult);
    }
}

