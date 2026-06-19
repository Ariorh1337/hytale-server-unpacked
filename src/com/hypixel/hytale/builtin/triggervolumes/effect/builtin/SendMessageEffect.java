package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerVolumeEntityQuery;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialData;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class SendMessageEffect extends TriggerEffect {
   @Nonnull
   public static final BuilderCodec<SendMessageEffect> CODEC = BuilderCodec.builder(SendMessageEffect.class, SendMessageEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("Message", Codec.STRING), (e, v) -> e.message = v, e -> e.message)
      .add()
      .append(
         new KeyedCodec<>("Recipient", new EnumCodec<>(SendMessageEffect.Recipient.class), false),
         (e, v) -> e.recipient = v == null ? SendMessageEffect.Recipient.TRIGGERING_PLAYER : v,
         e -> e.recipient
      )
      .add()
      .build();
   private String message;
   @Nonnull
   private SendMessageEffect.Recipient recipient = SendMessageEffect.Recipient.TRIGGERING_PLAYER;

   @Nonnull
   public static SendMessageEffect create(@Nonnull TriggerEventType eventType, @Nonnull String message) {
      SendMessageEffect effect = new SendMessageEffect();
      effect.setEventType(eventType);
      effect.message = message;
      return effect;
   }

   @Override
   public void execute(@Nonnull TriggerContext context) {
      if (this.message != null) {
         List<PlayerRef> recipients = this.resolveRecipients(context);
         if (!recipients.isEmpty()) {
            Message resolvedMessage = withTagParams(context, Message.translation(this.message));

            for (PlayerRef playerRef : recipients) {
               playerRef.sendMessage(resolvedMessage);
            }
         }
      }
   }

   @Nonnull
   private List<PlayerRef> resolveRecipients(@Nonnull TriggerContext context) {
      Store<EntityStore> store = context.getStore();

      return switch (this.recipient) {
         case TRIGGERING_PLAYER -> {
            PlayerRef playerRef = playerRefOf(store, context.getEntityRef());
            yield playerRef != null ? List.of(playerRef) : List.of();
         }
         case NEAREST_PLAYER -> nearestPlayer(store, context.getVolume().getPosition());
         case PLAYERS_IN_VOLUME -> playersInVolumes(store, context.getSpatialVolumes());
         case ALL_PLAYERS -> allPlayers(store);
      };
   }

   @Nonnull
   private static List<PlayerRef> nearestPlayer(@Nonnull Store<EntityStore> store, @Nonnull Vector3d position) {
      SpatialResource<Ref<EntityStore>, EntityStore> playerSpatial = store.getResource(EntityModule.get().getPlayerSpatialResourceType());
      if (playerSpatial == null) {
         return List.of();
      }

      SpatialData<Ref<EntityStore>> spatialData = playerSpatial.getSpatialData();
      PlayerRef nearest = null;
      double nearestDistanceSquared = Double.MAX_VALUE;

      for (int i = 0; i < spatialData.size(); i++) {
         double distanceSquared = spatialData.getVector(i).distanceSquared(position);
         if (!(distanceSquared >= nearestDistanceSquared)) {
            PlayerRef playerRef = playerRefOf(store, spatialData.getData(i));
            if (playerRef != null) {
               nearest = playerRef;
               nearestDistanceSquared = distanceSquared;
            }
         }
      }

      return nearest != null ? List.of(nearest) : List.of();
   }

   @Nonnull
   private static List<PlayerRef> playersInVolumes(@Nonnull Store<EntityStore> store, @Nonnull List<VolumeEntry> volumes) {
      List<Ref<EntityStore>> refs = TriggerVolumeEntityQuery.collectTargets(store, volumes, false, true, "");
      ArrayList<PlayerRef> players = new ArrayList<>(refs.size());

      for (Ref<EntityStore> ref : refs) {
         PlayerRef playerRef = playerRefOf(store, ref);
         if (playerRef != null) {
            players.add(playerRef);
         }
      }

      return players;
   }

   @Nonnull
   private static List<PlayerRef> allPlayers(@Nonnull Store<EntityStore> store) {
      SpatialResource<Ref<EntityStore>, EntityStore> playerSpatial = store.getResource(EntityModule.get().getPlayerSpatialResourceType());
      if (playerSpatial == null) {
         return List.of();
      }

      SpatialData<Ref<EntityStore>> spatialData = playerSpatial.getSpatialData();
      ArrayList<PlayerRef> players = new ArrayList<>(spatialData.size());

      for (int i = 0; i < spatialData.size(); i++) {
         PlayerRef playerRef = playerRefOf(store, spatialData.getData(i));
         if (playerRef != null) {
            players.add(playerRef);
         }
      }

      return players;
   }

   @Nullable
   private static PlayerRef playerRefOf(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
      return ref.isValid() ? store.getComponent(ref, PlayerRef.getComponentType()) : null;
   }

   public enum Recipient {
      TRIGGERING_PLAYER,
      NEAREST_PLAYER,
      PLAYERS_IN_VOLUME,
      ALL_PLAYERS;
   }
}
