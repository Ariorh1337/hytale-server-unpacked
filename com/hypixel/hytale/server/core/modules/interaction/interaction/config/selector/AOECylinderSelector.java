/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.SelectInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.AOECircleSelector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AOECylinderSelector
extends AOECircleSelector {
    @Nonnull
    public static final BuilderCodec<AOECylinderSelector> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(AOECylinderSelector.class, AOECylinderSelector::new, AOECircleSelector.CODEC).documentation("A selector that selects all entities within a given range and height.")).append(new KeyedCodec<Float>("Height", Codec.FLOAT), (aoeCircleEntitySelector, d) -> {
        aoeCircleEntitySelector.height = d.floatValue();
    }, aoeCircleEntitySelector -> Float.valueOf(aoeCircleEntitySelector.height)).documentation("The height of the area to search for targets in from the entity position.").add()).build();
    private final RuntimeSelector instance = new RuntimeSelector();
    protected float height;

    @Override
    @Nonnull
    public Selector newSelector() {
        return this.instance;
    }

    @Override
    public com.hypixel.hytale.protocol.Selector toPacket() {
        return new com.hypixel.hytale.protocol.AOECylinderSelector(this.range, this.height, this.getOffset());
    }

    private class RuntimeSelector
    implements Selector {
        private RuntimeSelector() {
        }

        @Override
        public void tick(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, float time, float runTime) {
            if (SelectInteraction.SHOW_VISUAL_DEBUG) {
                Vector3d position = AOECylinderSelector.this.selectTargetPosition(commandBuffer, ref);
                Vector3f color = new Vector3f((float)HashUtil.random(ref.getIndex(), this.hashCode(), 10L), (float)HashUtil.random(ref.getIndex(), this.hashCode(), 11L), (float)HashUtil.random(ref.getIndex(), this.hashCode(), 12L));
                DebugUtils.addSphere(commandBuffer.getExternalData().getWorld(), position, color, AOECylinderSelector.this.range * 2.0f, 5.0f);
            }
        }

        @Override
        public void selectTargetEntities(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, @Nonnull BiConsumer<Ref<EntityStore>, Vector4d> consumer, @Nullable Predicate<Ref<EntityStore>> filter) {
            Vector3d position = AOECylinderSelector.this.selectTargetPosition(commandBuffer, ref);
            Vector3d min = new Vector3d(position.x - (double)AOECylinderSelector.this.range, position.y, position.z - (double)AOECylinderSelector.this.range);
            Vector3d max = new Vector3d(position.x + (double)AOECylinderSelector.this.range, position.y + (double)AOECylinderSelector.this.height, position.z + (double)AOECylinderSelector.this.range);
            List<Ref<EntityStore>> entitiesInArea = TargetUtil.getAllEntitiesInBox(min, max, commandBuffer);
            for (Ref<EntityStore> targetRef : entitiesInArea) {
                if (!targetRef.isValid() || targetRef.equals(ref) || filter != null && !filter.test(targetRef)) continue;
                Archetype<EntityStore> archetype = commandBuffer.getArchetype(targetRef);
                boolean isDead = archetype.contains(DeathComponent.getComponentType());
                boolean isInvulnerable = archetype.contains(Invulnerable.getComponentType());
                if (isDead || isInvulnerable) continue;
                TransformComponent targetEntityTransformComponent = commandBuffer.getComponent(targetRef, TransformComponent.getComponentType());
                assert (targetEntityTransformComponent != null);
                Vector3d pos = targetEntityTransformComponent.getPosition();
                double dx = pos.x - position.x;
                double dy = pos.y - position.y;
                double dz = pos.z - position.z;
                if (!(dx * dx + dz * dz <= (double)(AOECylinderSelector.this.range * AOECylinderSelector.this.range)) || !(dy <= (double)AOECylinderSelector.this.height) || !(dy >= 0.0)) continue;
                consumer.accept(targetRef, new Vector4d(position.x, position.y, position.z, 1.0));
            }
        }

        @Override
        public void selectTargetBlocks(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, @Nonnull TriIntConsumer consumer) {
            Vector3d position = AOECylinderSelector.this.selectTargetPosition(commandBuffer, ref);
            int xStart = MathUtil.floor(-AOECylinderSelector.this.range);
            int yStart = 0;
            int zStart = MathUtil.floor(-AOECylinderSelector.this.range);
            int xEnd = MathUtil.floor(AOECylinderSelector.this.range);
            int yEnd = MathUtil.floor(AOECylinderSelector.this.height);
            int zEnd = MathUtil.floor(AOECylinderSelector.this.range);
            float squaredRange = AOECylinderSelector.this.range * AOECylinderSelector.this.range;
            for (int x = xStart; x < xEnd; ++x) {
                for (int y = yStart; y < yEnd; ++y) {
                    for (int z = zStart; z < zEnd; ++z) {
                        if (!((float)(x * x + z * z) <= squaredRange)) continue;
                        consumer.accept(MathUtil.floor(position.x + (double)x), MathUtil.floor(position.y + (double)y), MathUtil.floor(position.z + (double)z));
                    }
                }
            }
        }
    }
}

