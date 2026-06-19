package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class SpawnNpcEffect extends TriggerEffect {
   private static final int MAX_COUNT = 64;
   @Nonnull
   public static final BuilderCodec<SpawnNpcEffect> CODEC = BuilderCodec.builder(SpawnNpcEffect.class, SpawnNpcEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("NpcType", Codec.STRING), (effect, npcType) -> effect.npcType = npcType, effect -> effect.npcType)
      .add()
      .append(new KeyedCodec<>("GroupType", Codec.STRING, false), (effect, groupType) -> effect.groupType = groupType, effect -> effect.groupType)
      .add()
      .append(
         new KeyedCodec<>("Origin", new EnumCodec<>(SpawnNpcEffect.Origin.class), false), (effect, origin) -> effect.origin = origin, effect -> effect.origin
      )
      .add()
      .append(new KeyedCodec<>("Offset", Vector3dUtil.CODEC, false), (effect, offset) -> effect.offset = offset, effect -> effect.offset)
      .add()
      .append(new KeyedCodec<>("Count", Codec.INTEGER, false), (effect, count) -> effect.count = count, effect -> effect.count)
      .add()
      .append(new KeyedCodec<>("Yaw", Codec.FLOAT, false), (effect, yaw) -> effect.yaw = yaw, effect -> effect.yaw)
      .add()
      .build();
   @Nullable
   private String npcType;
   @Nullable
   private String groupType;
   @Nonnull
   private SpawnNpcEffect.Origin origin = SpawnNpcEffect.Origin.VOLUME_ORIGIN;
   @Nonnull
   private Vector3d offset = new Vector3d();
   private int count = 1;
   private float yaw;

   @Nonnull
   public static SpawnNpcEffect create(@Nonnull TriggerEventType eventType, @Nonnull String npcType, @Nonnull SpawnNpcEffect.Origin origin) {
      SpawnNpcEffect effect = new SpawnNpcEffect();
      effect.setEventType(eventType);
      effect.npcType = npcType;
      effect.origin = origin;
      return effect;
   }

   @Override
   public void execute(@Nonnull TriggerContext context) {
      if (this.npcType != null && !this.npcType.isBlank()) {
         Store<EntityStore> store = context.getStore();
         Vector3d position = this.resolvePosition(context);
         Rotation3f rotation = new Rotation3f(0.0F, (float)Math.toRadians(this.yaw), 0.0F);
         String spawnGroup = this.groupType != null && !this.groupType.isBlank() ? this.groupType : null;
         int spawnCount = Math.min(Math.max(this.count, 1), 64);

         for (int spawned = 0; spawned < spawnCount; spawned++) {
            NPCPlugin.get().spawnNPC(store, this.npcType, spawnGroup, position, rotation);
         }
      }
   }

   @Nonnull
   private Vector3d resolvePosition(@Nonnull TriggerContext context) {
      return switch (this.origin) {
         case VOLUME_ORIGIN -> new Vector3d(context.getVolume().getPosition()).add(this.offset);
         case ENTITY -> {
            Vector3d actorPosition = context.getActorPosition();
            Vector3d base = actorPosition != null ? actorPosition : new Vector3d(context.getVolume().getPosition());
            yield base.add(this.offset);
         }
         case WORLD_ABSOLUTE -> new Vector3d(this.offset);
      };
   }

   public enum Origin {
      VOLUME_ORIGIN,
      ENTITY,
      WORLD_ABSOLUTE;
   }
}
