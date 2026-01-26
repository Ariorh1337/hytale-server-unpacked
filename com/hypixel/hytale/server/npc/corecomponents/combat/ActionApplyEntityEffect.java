/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.combat;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.builders.BuilderActionApplyEntityEffect;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionApplyEntityEffect
extends ActionBase {
    protected final int entityEffectId;
    protected final boolean useTarget;

    public ActionApplyEntityEffect(@Nonnull BuilderActionApplyEntityEffect builder, @Nonnull BuilderSupport support) {
        super(builder);
        this.entityEffectId = builder.getEntityEffect(support);
        this.useTarget = builder.isUseTarget(support);
    }

    @Override
    public boolean canExecute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nullable InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        return super.canExecute(ref, role, sensorInfo, dt, store) && (!this.useTarget || sensorInfo != null && sensorInfo.hasPosition());
    }

    @Override
    public boolean execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nonnull InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        EffectControllerComponent effectControllerComponent;
        Ref<EntityStore> targetRef;
        super.execute(ref, role, sensorInfo, dt, store);
        Ref<EntityStore> ref2 = targetRef = this.useTarget ? sensorInfo.getPositionProvider().getTarget() : ref;
        if (targetRef == null || !targetRef.isValid()) {
            return true;
        }
        EntityEffect entityEffect = EntityEffect.getAssetMap().getAsset(this.entityEffectId);
        if (entityEffect != null && (effectControllerComponent = store.getComponent(targetRef, EffectControllerComponent.getComponentType())) != null) {
            effectControllerComponent.addEffect(targetRef, this.entityEffectId, entityEffect, store);
        }
        return true;
    }
}

