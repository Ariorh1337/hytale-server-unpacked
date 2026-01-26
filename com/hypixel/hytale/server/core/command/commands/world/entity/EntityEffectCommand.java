/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.world.entity;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetEntityCommand;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import javax.annotation.Nonnull;

public class EntityEffectCommand
extends AbstractTargetEntityCommand {
    @Nonnull
    private final RequiredArg<EntityEffect> effectArg = this.withRequiredArg("effect", "server.commands.entity.effect.effect.desc", ArgTypes.EFFECT_ASSET);
    @Nonnull
    private final DefaultArg<Float> durationArg = (DefaultArg)this.withDefaultArg("duration", "server.commands.entity.effect.duration.desc", ArgTypes.FLOAT, Float.valueOf(100.0f), "server.commands.entity.effect.duration.default").addValidator(Validators.greaterThan(Float.valueOf(0.0f)));

    public EntityEffectCommand() {
        super("effect", "server.commands.entity.effect.desc");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull ObjectList<Ref<EntityStore>> entities, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        EntityEffect entityEffect = (EntityEffect)this.effectArg.get(context);
        float duration = ((Float)this.durationArg.get(context)).floatValue();
        for (Ref ref : entities) {
            EffectControllerComponent effectControllerComponent = store.getComponent(ref, EffectControllerComponent.getComponentType());
            if (effectControllerComponent == null) continue;
            effectControllerComponent.addEffect(ref, entityEffect, duration, OverlapBehavior.OVERWRITE, store);
        }
    }
}

