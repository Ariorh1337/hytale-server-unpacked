package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class PlaySoundEffect extends TriggerEffect {
   @Nonnull
   public static final BuilderCodec<PlaySoundEffect> CODEC = BuilderCodec.builder(PlaySoundEffect.class, PlaySoundEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("SoundEvent", Codec.STRING), (e, v) -> e.soundEventId = v, e -> e.soundEventId)
      .add()
      .append(new KeyedCodec<>("Volume", Codec.FLOAT, false), (e, v) -> e.volumeModifier = v, e -> e.volumeModifier)
      .add()
      .append(new KeyedCodec<>("Pitch", Codec.FLOAT, false), (e, v) -> e.pitchModifier = v, e -> e.pitchModifier)
      .add()
      .append(new KeyedCodec<>("Location", new EnumCodec<>(PlaySoundEffect.PlayLocation.class), false), (e, v) -> e.location = v, e -> e.location)
      .add()
      .append(new KeyedCodec<>("Offset", Vector3dUtil.CODEC, false), (e, v) -> e.offset = v, e -> e.offset)
      .add()
      .build();
   private String soundEventId;
   private float volumeModifier = 1.0F;
   private float pitchModifier = 1.0F;
   @Nonnull
   private PlaySoundEffect.PlayLocation location = PlaySoundEffect.PlayLocation.VOLUME_CENTER;
   @Nonnull
   private Vector3d offset = new Vector3d();

   @Nonnull
   public static PlaySoundEffect create(@Nonnull TriggerEventType eventType, @Nonnull String soundEventId) {
      PlaySoundEffect effect = new PlaySoundEffect();
      effect.setEventType(eventType);
      effect.soundEventId = soundEventId;
      return effect;
   }

   @Override
   public void execute(@Nonnull TriggerContext context) {
      if (this.soundEventId != null) {
         int soundEventIndex = SoundEvent.getAssetMap().getIndex(this.soundEventId);
         if (soundEventIndex != Integer.MIN_VALUE && soundEventIndex != 0) {
            Store<EntityStore> store = context.getStore();
            if (this.location == PlaySoundEffect.PlayLocation.PLAYER) {
               SoundUtil.playSoundEvent2d(context.getEntityRef(), soundEventIndex, SoundCategory.SFX, this.volumeModifier, this.pitchModifier, store);
            } else {
               Vector3d position;
               if (this.location == PlaySoundEffect.PlayLocation.ENTITY) {
                  Vector3d actorPosition = context.getActorPosition();
                  if (actorPosition == null) {
                     return;
                  }

                  position = actorPosition.add(this.offset);
               } else {
                  position = new Vector3d(context.getVolume().getPosition()).add(this.offset);
               }

               SoundUtil.playSoundEvent3d(
                  soundEventIndex, SoundCategory.SFX, position.x(), position.y(), position.z(), this.volumeModifier, this.pitchModifier, store
               );
            }
         }
      }
   }

   public enum PlayLocation {
      VOLUME_CENTER,
      ENTITY,
      PLAYER;
   }
}
