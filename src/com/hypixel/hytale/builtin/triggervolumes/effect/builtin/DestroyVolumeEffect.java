package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class DestroyVolumeEffect extends TriggerEffect {
   @Nonnull
   public static final BuilderCodec<DestroyVolumeEffect> CODEC = BuilderCodec.builder(DestroyVolumeEffect.class, DestroyVolumeEffect::new, BASE_CODEC).build();

   @Nonnull
   public static DestroyVolumeEffect create(@Nonnull TriggerEventType eventType) {
      DestroyVolumeEffect effect = new DestroyVolumeEffect();
      effect.setEventType(eventType);
      return effect;
   }

   @Override
   public void execute(@Nonnull TriggerContext context) {
      context.getVolume().markPendingDestroy();
   }
}
