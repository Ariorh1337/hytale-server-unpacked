package com.hypixel.hytale.builtin.triggervolumes.effect;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import java.util.UUID;
import javax.annotation.Nonnull;

public abstract class TriggerEffect {
   @Nonnull
   public static final CodecMapCodec<TriggerEffect> CODEC = new CodecMapCodec<>("Type");
   @Nonnull
   public static final BuilderCodec<TriggerEffect> BASE_CODEC = BuilderCodec.abstractBuilder(TriggerEffect.class)
      .append(
         new KeyedCodec<>("Event", new EnumCodec<>(TriggerEventType.class, EnumCodec.EnumStyle.LEGACY)),
         TriggerEffect::setEventType,
         TriggerEffect::getEventType
      )
      .add()
      .append(new KeyedCodec<>("Interval", Codec.FLOAT, false), (e, v) -> e.interval = v, e -> e.interval)
      .add()
      .append(new KeyedCodec<>("Delay", Codec.FLOAT, false), (e, v) -> e.delay = v, e -> e.delay)
      .add()
      .build();
   private TriggerEventType eventType;
   private float interval = 0.0F;
   private float delay = 0.0F;

   @Nonnull
   public TriggerEventType getEventType() {
      return this.eventType;
   }

   public void setEventType(@Nonnull TriggerEventType eventType) {
      this.eventType = eventType;
   }

   public float getInterval() {
      return this.interval;
   }

   public void setInterval(float interval) {
      this.interval = interval;
   }

   public float getDelay() {
      return this.delay;
   }

   public void setDelay(float delay) {
      this.delay = delay;
   }

   public abstract void execute(@Nonnull TriggerContext var1);

   public void onEntityExit(@Nonnull UUID entityUuid) {
   }
}
