/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class HeadRotation
implements Component<EntityStore> {
    public static final BuilderCodec<HeadRotation> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(HeadRotation.class, HeadRotation::new).append(new KeyedCodec<Vector3f>("Rotation", Vector3f.ROTATION), (o, i) -> o.rotation.assign((Vector3f)i), o -> o.rotation).add()).build();
    private final Vector3f rotation = new Vector3f();

    public static ComponentType<EntityStore, HeadRotation> getComponentType() {
        return EntityModule.get().getHeadRotationComponentType();
    }

    public HeadRotation() {
    }

    public HeadRotation(@Nonnull Vector3f rotation) {
        this.rotation.assign(rotation);
    }

    @Nonnull
    public Vector3f getRotation() {
        return this.rotation;
    }

    public void setRotation(@Nonnull Vector3f rotation) {
        this.rotation.assign(rotation);
    }

    public Vector3d getDirection() {
        return HeadRotation.getDirection(this.rotation.getPitch(), this.rotation.getYaw(), new Vector3d());
    }

    @Nonnull
    public Vector3i getAxisDirection() {
        return HeadRotation.getAxisDirection(this.rotation.getPitch(), this.rotation.getYaw(), new Vector3i());
    }

    @Nonnull
    public Vector3i getAxisDirection(@Nonnull Vector3i result) {
        return HeadRotation.getAxisDirection(this.rotation.getPitch(), this.rotation.getYaw(), result);
    }

    @Nonnull
    public Vector3i getHorizontalAxisDirection() {
        return HeadRotation.getAxisDirection(0.0f, this.rotation.getYaw(), new Vector3i());
    }

    @Nonnull
    public Axis getAxis() {
        Vector3i axisDirection = this.getAxisDirection();
        if (axisDirection.getX() != 0) {
            return Axis.X;
        }
        if (axisDirection.getY() != 0) {
            return Axis.Y;
        }
        return Axis.Z;
    }

    @Nonnull
    public static Vector3i getAxisDirection(float pitch, float yaw, @Nonnull Vector3i result) {
        if (Float.isNaN(pitch)) {
            throw new IllegalStateException("Pitch can't be NaN");
        }
        if (Float.isNaN(yaw)) {
            throw new IllegalStateException("Yaw can't be NaN");
        }
        double len = TrigMathUtil.cos(pitch);
        double x = len * (double)(-TrigMathUtil.sin(yaw));
        double y = TrigMathUtil.sin(pitch);
        double z = len * (double)(-TrigMathUtil.cos(yaw));
        return result.assign((int)Math.round(x), (int)Math.round(y), (int)Math.round(z));
    }

    @Nonnull
    private static Vector3d getDirection(float pitch, float yaw, @Nonnull Vector3d result) {
        if (Float.isNaN(pitch)) {
            throw new IllegalStateException("Pitch can't be NaN");
        }
        if (Float.isNaN(yaw)) {
            throw new IllegalStateException("Yaw can't be NaN");
        }
        return result.assign(yaw, pitch);
    }

    public void teleportRotation(@Nonnull Vector3f rotation) {
        float roll;
        float pitch;
        float yaw = rotation.getYaw();
        if (!Float.isNaN(yaw)) {
            this.rotation.setYaw(yaw);
        }
        if (!Float.isNaN(pitch = rotation.getPitch())) {
            this.rotation.setPitch(pitch);
        }
        if (!Float.isNaN(roll = rotation.getRoll())) {
            this.rotation.setRoll(roll);
        }
    }

    @Nonnull
    public HeadRotation clone() {
        return new HeadRotation(this.rotation);
    }
}

