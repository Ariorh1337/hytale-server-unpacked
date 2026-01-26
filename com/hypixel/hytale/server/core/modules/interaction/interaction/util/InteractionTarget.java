/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.util;

import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum InteractionTarget {
    USER,
    OWNER,
    TARGET;

    public static final EnumCodec<InteractionTarget> CODEC;

    @Nullable
    public Ref<EntityStore> getEntity(@Nonnull InteractionContext ctx, @Nonnull Ref<EntityStore> ref) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> ctx.getEntity();
            case 1 -> ctx.getOwningEntity();
            case 2 -> ctx.getTargetEntity();
        };
    }

    @Nullable
    @Deprecated
    public <T extends Entity> T getEntity(@Nonnull InteractionContext ctx, @Nonnull Ref<EntityStore> ref, @Nonnull Class<T> clazz) {
        Ref<EntityStore> e = this.getEntity(ctx, ref);
        Entity legacy = EntityUtils.getEntity(e, ctx.getCommandBuffer());
        if (clazz.isInstance(legacy)) {
            return (T)legacy;
        }
        return null;
    }

    @Nonnull
    public com.hypixel.hytale.protocol.InteractionTarget toProtocol() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> com.hypixel.hytale.protocol.InteractionTarget.User;
            case 1 -> com.hypixel.hytale.protocol.InteractionTarget.Owner;
            case 2 -> com.hypixel.hytale.protocol.InteractionTarget.Target;
        };
    }

    static {
        CODEC = new EnumCodec<InteractionTarget>(InteractionTarget.class).documentKey(USER, "Causes the interaction to target the entity that triggered/owns the interaction chain.").documentKey(TARGET, "Causes the interaction to target the entity that is the target of the interaction chain.");
    }
}

