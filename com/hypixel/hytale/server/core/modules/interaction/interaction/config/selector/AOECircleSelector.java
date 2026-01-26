/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.SelectInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.SelectorType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class AOECircleSelector
extends SelectorType {
    @Nonnull
    public static final BuilderCodec<AOECircleSelector> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(AOECircleSelector.class, AOECircleSelector::new, BASE_CODEC).documentation("A selector that selects all entities within a given range.")).appendInherited(new KeyedCodec<Float>("Range", Codec.FLOAT), (aoeCircleEntitySelector, d) -> {
        aoeCircleEntitySelector.range = d.floatValue();
    }, aoeCircleEntitySelector -> Float.valueOf(aoeCircleEntitySelector.range), (aoeCircleSelector, parent) -> {
        aoeCircleSelector.range = parent.range;
    }).addValidator(Validators.max(Float.valueOf(30.0f))).documentation("The range of the area to search for targets in.").add()).append(new KeyedCodec<Vector3d>("Offset", Vector3d.CODEC), (aoeCircleEntitySelector, i) -> {
        aoeCircleEntitySelector.offset = i;
    }, aoeCircleEntitySelector -> aoeCircleEntitySelector.offset).documentation("The offset of the area to search for targets in.").add()).build();
    @Nonnull
    private final RuntimeSelector instance = new RuntimeSelector();
    protected float range;
    @Nonnull
    protected Vector3d offset = Vector3d.ZERO;

    @Override
    @Nonnull
    public Selector newSelector() {
        return this.instance;
    }

    @Override
    public com.hypixel.hytale.protocol.Selector toPacket() {
        return new com.hypixel.hytale.protocol.AOECircleSelector(this.range, this.getOffset());
    }

    @Nonnull
    public Vector3f getOffset() {
        return new Vector3f((float)this.offset.x, (float)this.offset.y, (float)this.offset.z);
    }

    @Nonnull
    public Vector3d selectTargetPosition(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> attackerRef) {
        TransformComponent transformComponent = commandBuffer.getComponent(attackerRef, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d position = transformComponent.getPosition();
        if (this.offset.x != 0.0 || this.offset.y != 0.0 || this.offset.z != 0.0) {
            HeadRotation headRotationComponent = commandBuffer.getComponent(attackerRef, HeadRotation.getComponentType());
            assert (headRotationComponent != null);
            position = this.offset.clone();
            position.rotateY(headRotationComponent.getRotation().getYaw());
            position.add(transformComponent.getPosition());
        }
        return position;
    }

    private class RuntimeSelector
    implements Selector {
        private RuntimeSelector() {
        }

        @Override
        public void tick(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, float time, float runTime) {
            if (SelectInteraction.SHOW_VISUAL_DEBUG) {
                Vector3d position = AOECircleSelector.this.selectTargetPosition(commandBuffer, ref);
                com.hypixel.hytale.math.vector.Vector3f color = new com.hypixel.hytale.math.vector.Vector3f((float)HashUtil.random(ref.getIndex(), this.hashCode(), 10L), (float)HashUtil.random(ref.getIndex(), this.hashCode(), 11L), (float)HashUtil.random(ref.getIndex(), this.hashCode(), 12L));
                DebugUtils.addSphere(commandBuffer.getExternalData().getWorld(), position, color, AOECircleSelector.this.range * 2.0f, 5.0f);
            }
        }

        @Override
        public void selectTargetEntities(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> attacker, @Nonnull BiConsumer<Ref<EntityStore>, Vector4d> consumer, Predicate<Ref<EntityStore>> filter) {
            Vector3d position = AOECircleSelector.this.selectTargetPosition(commandBuffer, attacker);
            Selector.selectNearbyEntities(commandBuffer, position, (double)AOECircleSelector.this.range, (Ref<EntityStore> entity) -> consumer.accept((Ref<EntityStore>)entity, Vector4d.newPosition(position)), filter);
        }

        @Override
        public void selectTargetBlocks(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> attacker, @Nonnull TriIntConsumer consumer) {
            Vector3d position = AOECircleSelector.this.selectTargetPosition(commandBuffer, attacker);
            int radius = (int)AOECircleSelector.this.range;
            for (int x = -radius; x <= radius; ++x) {
                for (int y = -radius; y <= radius; ++y) {
                    for (int z = -radius; z <= radius; ++z) {
                        double d2 = x * x + y * y + z * z;
                        if (!(d2 <= (double)(radius * radius))) continue;
                        Vector3i p = new Vector3i((int)(position.x + (double)x) - 1, (int)(position.y + (double)y), (int)(position.z + (double)z));
                        consumer.accept(p.x, p.y, p.z);
                    }
                }
            }
        }
    }
}

