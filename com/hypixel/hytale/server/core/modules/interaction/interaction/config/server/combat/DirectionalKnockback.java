/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.Knockback;
import javax.annotation.Nonnull;

public class DirectionalKnockback
extends Knockback {
    public static final BuilderCodec<DirectionalKnockback> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DirectionalKnockback.class, DirectionalKnockback::new, Knockback.BASE_CODEC).append(new KeyedCodec<Double>("RelativeX", Codec.DOUBLE), (knockbackAttachment, d) -> {
        knockbackAttachment.relativeX = d.floatValue();
    }, knockbackAttachment -> knockbackAttachment.relativeX).add()).append(new KeyedCodec<Double>("VelocityY", Codec.DOUBLE), (knockbackAttachment, d) -> {
        knockbackAttachment.velocityY = d.floatValue();
    }, knockbackAttachment -> knockbackAttachment.velocityY).add()).append(new KeyedCodec<Double>("RelativeZ", Codec.DOUBLE), (knockbackAttachment, d) -> {
        knockbackAttachment.relativeZ = d.floatValue();
    }, knockbackAttachment -> knockbackAttachment.relativeZ).add()).build();
    protected float relativeX;
    protected float velocityY;
    protected float relativeZ;

    @Override
    @Nonnull
    public Vector3d calculateVector(@Nonnull Vector3d source, float yaw, @Nonnull Vector3d target) {
        Vector3d vector = source.clone().subtract(target);
        if (vector.squaredLength() <= 1.0E-8) {
            Vector3d lookVector = new Vector3d(0.0, 0.0, -1.0);
            lookVector.rotateY(yaw);
            vector.assign(lookVector);
        } else {
            vector.normalize();
        }
        if (this.relativeX != 0.0f || this.relativeZ != 0.0f) {
            Vector3d rotation = new Vector3d(this.relativeX, 0.0, this.relativeZ);
            rotation.rotateY(yaw);
            vector.add(rotation);
        }
        double x = vector.getX() * (double)this.force;
        double z = vector.getZ() * (double)this.force;
        double y = this.velocityY;
        return new Vector3d(x, y, z);
    }

    @Override
    @Nonnull
    public String toString() {
        return "DirectionalKnockback{relativeX=" + this.relativeX + ", relativeZ=" + this.relativeZ + "}";
    }
}

