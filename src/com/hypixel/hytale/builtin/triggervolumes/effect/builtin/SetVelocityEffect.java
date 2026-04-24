package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SetVelocityEffect extends TriggerEffect {
   @Nonnull
   public static final BuilderCodec<SetVelocityEffect> CODEC = BuilderCodec.builder(SetVelocityEffect.class, SetVelocityEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("Velocity", Vector3dUtil.AS_ARRAY_CODEC), (e, v) -> e.velocity = v, e -> e.velocity)
      .add()
      .append(new KeyedCodec<>("Additive", Codec.BOOLEAN, false), (e, v) -> e.additive = v, e -> e.additive)
      .add()
      .build();
   private Vector3d velocity = new Vector3d(0.0, 0.0, 0.0);
   private boolean additive = false;

   @Nonnull
   public static SetVelocityEffect create(@Nonnull TriggerEventType eventType, @Nonnull Vector3d velocity, boolean additive) {
      SetVelocityEffect effect = new SetVelocityEffect();
      effect.setEventType(eventType);
      effect.velocity = velocity;
      effect.additive = additive;
      return effect;
   }

   @Override
   public void execute(@Nonnull TriggerContext context) {
      if (this.velocity != null) {
         Ref<EntityStore> entityRef = context.getEntityRef();
         Store<EntityStore> store = context.getStore();
         Velocity velocityComponent = store.getComponent(entityRef, Velocity.getComponentType());
         if (velocityComponent != null) {
            if (this.additive) {
               velocityComponent.addInstruction(this.velocity, null, ChangeVelocityType.Add);
            } else {
               velocityComponent.addInstruction(this.velocity, null, ChangeVelocityType.Set);
            }
         }
      }
   }
}
