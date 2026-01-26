/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.Knockback;
import javax.annotation.Nonnull;

public class ForceKnockback
extends Knockback {
    public static final BuilderCodec<ForceKnockback> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ForceKnockback.class, ForceKnockback::new, Knockback.BASE_CODEC).appendInherited(new KeyedCodec<Vector3d>("Direction", Vector3d.CODEC), (o, i) -> {
        o.direction = i;
    }, o -> o.direction, (o, p) -> {
        o.direction = p.direction;
    }).addValidator(Validators.nonNull()).add()).afterDecode(i -> i.direction.normalize())).build();
    private Vector3d direction = Vector3d.UP;

    @Override
    @Nonnull
    public Vector3d calculateVector(Vector3d source, float yaw, Vector3d target) {
        Vector3d vel = this.direction.clone();
        vel.rotateY(yaw);
        vel.scale(this.force);
        return vel;
    }

    @Override
    @Nonnull
    public String toString() {
        return "ForceKnockback{direction=" + String.valueOf(this.direction) + "} " + super.toString();
    }
}

