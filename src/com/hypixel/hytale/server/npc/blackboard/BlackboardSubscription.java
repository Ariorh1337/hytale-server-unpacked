package com.hypixel.hytale.server.npc.blackboard;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.EventSlotMapper;
import com.hypixel.hytale.server.npc.blackboard.view.blocktype.BlockTypeView;
import com.hypixel.hytale.server.npc.blackboard.view.event.EntityEventNotification;
import com.hypixel.hytale.server.npc.blackboard.view.event.EventNotification;
import com.hypixel.hytale.server.npc.blackboard.view.event.block.BlockEventType;
import com.hypixel.hytale.server.npc.blackboard.view.event.block.BlockEventView;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventType;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventView;
import com.hypixel.hytale.server.npc.components.messaging.EntityEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.EventSupport;
import com.hypixel.hytale.server.npc.components.messaging.NPCBlockEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.NPCEntityEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.PlayerBlockEventSupport;
import com.hypixel.hytale.server.npc.components.messaging.PlayerEntityEventSupport;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlackboardSubscription implements Component<EntityStore> {
   @Nullable
   private BlockTypeView blockTypeView;
   @Nullable
   private IntList blockTypeSets;
   @Nullable
   private Map<BlockEventType, IntSet> blockChangeSets;
   @Nullable
   private Map<EntityEventType, IntSet> entityEventSets;

   @Nonnull
   public static ComponentType<EntityStore, BlackboardSubscription> getComponentType() {
      return NPCPlugin.get().getBlackboardSubscriptionComponentType();
   }

   @Nonnull
   public BlockTypeView getBlockTypeBlackboardView(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
      if (this.blockTypeView == null) {
         this.initBlockTypeView(ref, store);
      }

      if (this.blockTypeView.isOutdated(ref, store)) {
         this.blockTypeView = this.blockTypeView.getUpdatedView(ref, store);
      }

      return this.blockTypeView;
   }

   @Nullable
   public BlockTypeView removeBlockTypeBlackboardView() {
      BlockTypeView view = this.blockTypeView;
      this.blockTypeView = null;
      return view;
   }

   public void initBlockTypeView(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      if (this.blockTypeSets != null) {
         this.blockTypeView = accessor.getResource(Blackboard.getResourceType()).getView(BlockTypeView.class, ref, accessor);
         this.blockTypeView.initialiseEntity(ref, this);
      }
   }

   @Nullable
   public IntList getBlockTypeSets() {
      return this.blockTypeSets;
   }

   public void initEventViews(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      if (this.blockChangeSets != null) {
         BlockEventView blockChangeView = accessor.getResource(Blackboard.getResourceType()).getView(BlockEventView.class, ref, accessor);
         blockChangeView.initialiseEntity(ref, this);
      }

      if (this.entityEventSets != null) {
         EntityEventView entityEventView = accessor.getResource(Blackboard.getResourceType()).getView(EntityEventView.class, ref, accessor);
         entityEventView.initialiseEntity(ref, this);
      }
   }

   @Nullable
   public IntSet getBlockChangeSet(@Nonnull BlockEventType type) {
      return this.blockChangeSets == null ? null : this.blockChangeSets.getOrDefault(type, null);
   }

   public static void notifyBlockChange(
      @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull BlockEventType type, @Nonnull EventNotification notification
   ) {
      Ref<EntityStore> initiator = notification.getInitiator();
      boolean isPlayer = accessor.getArchetype(initiator).contains(Player.getComponentType());
      EventSupport<BlockEventType, EventNotification> support;
      if (isPlayer) {
         support = accessor.getComponent(ref, PlayerBlockEventSupport.getComponentType());
      } else {
         support = accessor.getComponent(ref, NPCBlockEventSupport.getComponentType());
      }

      if (support != null) {
         support.postMessage(type, notification, ref, accessor);
      }
   }

   @Nullable
   public IntSet getEntityEventSet(@Nonnull EntityEventType type) {
      return this.entityEventSets == null ? null : this.entityEventSets.getOrDefault(type, null);
   }

   public static void notifyEntityEvent(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ComponentAccessor<EntityStore> accessor,
      @Nonnull EntityEventType type,
      @Nonnull EntityEventNotification notification
   ) {
      Ref<EntityStore> initiator = notification.getInitiator();
      boolean isPlayer = accessor.getArchetype(initiator).contains(Player.getComponentType());
      EntityEventSupport support;
      if (isPlayer) {
         support = accessor.getComponent(ref, PlayerEntityEventSupport.getComponentType());
      } else {
         support = accessor.getComponent(ref, NPCEntityEventSupport.getComponentType());
      }

      if (support != null) {
         support.postMessage(type, notification, ref, accessor);
      }
   }

   private void addBlockTypeSets(@Nonnull IntList sets) {
      this.blockTypeSets = sets;
   }

   private void addBlockChangeSets(@Nonnull BlockEventType type, @Nonnull IntSet sets) {
      if (this.blockChangeSets == null) {
         this.blockChangeSets = new EnumMap<>(BlockEventType.class);
      }

      this.blockChangeSets.put(type, sets);
   }

   private void addEntityEventSets(@Nonnull EntityEventType type, @Nonnull IntSet sets) {
      if (this.entityEventSets == null) {
         this.entityEventSets = new EnumMap<>(EntityEventType.class);
      }

      this.entityEventSets.put(type, sets);
   }

   private boolean hasAnySubscription() {
      return this.blockTypeSets != null || this.blockChangeSets != null || this.entityEventSets != null;
   }

   public static void buildAndAttach(@Nonnull Holder<EntityStore> holder, @Nonnull BuilderSupport support) {
      BlackboardSubscription subscription = new BlackboardSubscription();
      if (support.requiresBlockTypeBlackboard()) {
         subscription.addBlockTypeSets(support.getBlockTypeBlackboardBlockSets());
      }

      if (support.hasBlockEventSupport()) {
         EventSlotMapper<BlockEventType> playerEventSlotMapper = support.getPlayerBlockEventSlotMapper();
         if (playerEventSlotMapper != null) {
            PlayerBlockEventSupport playerBlockEventSupport = new PlayerBlockEventSupport();
            playerBlockEventSupport.initialise(
               playerEventSlotMapper.getEventSlotMappings(), playerEventSlotMapper.getEventSlotRanges(), playerEventSlotMapper.getEventSlotCount()
            );
            holder.putComponent(PlayerBlockEventSupport.getComponentType(), playerBlockEventSupport);
         }

         EventSlotMapper<BlockEventType> npcEventSlotMapper = support.getNPCBlockEventSlotMapper();
         if (npcEventSlotMapper != null) {
            NPCBlockEventSupport npcBlockEventSupport = new NPCBlockEventSupport();
            npcBlockEventSupport.initialise(
               npcEventSlotMapper.getEventSlotMappings(), npcEventSlotMapper.getEventSlotRanges(), npcEventSlotMapper.getEventSlotCount()
            );
            holder.putComponent(NPCBlockEventSupport.getComponentType(), npcBlockEventSupport);
         }

         for (BlockEventType type : BlockEventType.VALUES) {
            IntSet sets = support.getBlockChangeSets(type);
            if (sets != null) {
               subscription.addBlockChangeSets(type, sets);
            }
         }
      }

      if (support.hasEntityEventSupport()) {
         EventSlotMapper<EntityEventType> playerEventSlotMapper = support.getPlayerEntityEventSlotMapper();
         if (playerEventSlotMapper != null) {
            PlayerEntityEventSupport playerEntityEventSupport = new PlayerEntityEventSupport();
            playerEntityEventSupport.initialise(
               playerEventSlotMapper.getEventSlotMappings(), playerEventSlotMapper.getEventSlotRanges(), playerEventSlotMapper.getEventSlotCount()
            );
            holder.putComponent(PlayerEntityEventSupport.getComponentType(), playerEntityEventSupport);
         }

         EventSlotMapper<EntityEventType> npcEventSlotMapper = support.getNPCEntityEventSlotMapper();
         if (npcEventSlotMapper != null) {
            NPCEntityEventSupport npcEntityEventSupport = new NPCEntityEventSupport();
            npcEntityEventSupport.initialise(
               npcEventSlotMapper.getEventSlotMappings(), npcEventSlotMapper.getEventSlotRanges(), npcEventSlotMapper.getEventSlotCount()
            );
            holder.putComponent(NPCEntityEventSupport.getComponentType(), npcEntityEventSupport);
         }

         for (EntityEventType type : EntityEventType.VALUES) {
            IntSet sets = support.getEventNPCGroups(type);
            if (sets != null) {
               subscription.addEntityEventSets(type, sets);
            }
         }
      }

      if (subscription.hasAnySubscription()) {
         holder.putComponent(getComponentType(), subscription);
      }
   }

   @Nonnull
   @Override
   public Component<EntityStore> clone() {
      return new BlackboardSubscription();
   }
}
