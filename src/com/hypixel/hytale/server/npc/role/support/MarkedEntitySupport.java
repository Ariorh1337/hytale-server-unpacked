package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.group.EntityGroup;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class MarkedEntitySupport implements Component<EntityStore> {
   @Nonnull
   public static final BuilderCodec<MarkedEntitySupport> CODEC = BuilderCodec.builder(MarkedEntitySupport.class, MarkedEntitySupport::new)
      .append(
         new KeyedCodec<>("RebindTargets", new MapCodec<>(Codec.UUID_BINARY, HashMap::new, false)),
         (support, map) -> support.rebindUuids = map,
         support -> support.rebindUuids.isEmpty() ? null : support.rebindUuids
      )
      .add()
      .build();
   public static final String DEFAULT_TARGET_SLOT = "LockedTarget";
   @Nullable
   protected static final ComponentType<EntityStore, NPCEntity> NPC_COMPONENT_TYPE = NPCEntity.getComponentType();
   @Nonnull
   private static final Object2IntMap<String> EMPTY_TARGET_SLOT_MAP = new Object2IntOpenHashMap<>(0);
   protected Object2IntMap<String> targetSlotMappings;
   @Nullable
   protected Int2ObjectMap<String> slotToNameMap;
   protected Ref<EntityStore>[] entityTargets;
   @Nullable
   protected Vector3d[] storedPositions;
   @Nonnull
   protected Map<String, UUID> rebindUuids = new HashMap<>();
   protected int defaultTargetSlot;
   protected int targetSlotToIgnoreForAvoidance = Integer.MIN_VALUE;

   @Nonnull
   public static ComponentType<EntityStore, MarkedEntitySupport> getComponentType() {
      return NPCPlugin.get().getMarkedEntitySupportComponentType();
   }

   @Nonnull
   public static MarkedEntitySupport get(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      MarkedEntitySupport support = accessor.getComponent(ref, getComponentType());
      assert support != null : "Missing MarkedEntitySupport on entity " + ref;
      return support;
   }

   public MarkedEntitySupport() {
      this.initEmpty();
   }

   private void initEmpty() {
      this.targetSlotMappings = EMPTY_TARGET_SLOT_MAP;
      this.slotToNameMap = null;
      this.entityTargets = (Ref<EntityStore>[])Ref.EMPTY_ARRAY;
      this.defaultTargetSlot = Integer.MIN_VALUE;
      this.targetSlotToIgnoreForAvoidance = Integer.MIN_VALUE;
   }

   public Ref<EntityStore>[] getEntityTargets() {
      return this.entityTargets;
   }

   public void postRoleBuilder(@Nonnull BuilderSupport support) {
      Object2IntMap<String> slotMappings = support.getTargetSlotMappings();
      if (slotMappings != null) {
         this.targetSlotMappings = slotMappings;
         this.slotToNameMap = support.getTargetSlotToNameMap();
         this.entityTargets = new Ref[this.targetSlotMappings.size()];
         this.defaultTargetSlot = this.targetSlotMappings.getInt("LockedTarget");
      } else {
         this.targetSlotMappings = EMPTY_TARGET_SLOT_MAP;
         this.slotToNameMap = null;
         this.entityTargets = (Ref<EntityStore>[])Ref.EMPTY_ARRAY;
         this.defaultTargetSlot = Integer.MIN_VALUE;
      }

      this.storedPositions = support.allocatePositionSlots();
      this.targetSlotToIgnoreForAvoidance = this.defaultTargetSlot;
   }

   public void clearMarkedEntity(int targetSlot) {
      this.entityTargets[targetSlot] = null;
      this.removeRebind(targetSlot);
   }

   public void setMarkedEntity(String targetSlot, Ref<EntityStore> target) {
      int slot = this.targetSlotMappings.getInt(targetSlot);
      if (slot >= 0) {
         this.setMarkedEntity(slot, target);
      }
   }

   public void setMarkedEntity(int targetSlot, @Nullable Ref<EntityStore> target) {
      if (target != null && target.isValid()) {
         this.entityTargets[targetSlot] = target;
         this.removeRebind(targetSlot);
      } else {
         this.clearMarkedEntity(targetSlot);
      }
   }

   public void setMarkedEntity(String targetSlot, Ref<EntityStore> target, boolean rebind, @Nonnull ComponentAccessor<EntityStore> accessor) {
      int slot = this.targetSlotMappings.getInt(targetSlot);
      if (slot >= 0) {
         this.setMarkedEntity(slot, target, rebind, accessor);
      }
   }

   public void setMarkedEntity(int targetSlot, @Nullable Ref<EntityStore> target, boolean rebind, @Nonnull ComponentAccessor<EntityStore> accessor) {
      if (target != null && target.isValid()) {
         this.entityTargets[targetSlot] = target;
         if (rebind) {
            this.captureRebind(targetSlot, target, accessor);
         } else {
            this.removeRebind(targetSlot);
         }
      } else {
         this.clearMarkedEntity(targetSlot);
      }
   }

   private void captureRebind(int targetSlot, @Nonnull Ref<EntityStore> target, @Nonnull ComponentAccessor<EntityStore> accessor) {
      if (this.slotToNameMap != null) {
         String name = this.slotToNameMap.get(targetSlot);
         if (name != null) {
            UUIDComponent uuidComponent = accessor.getComponent(target, UUIDComponent.getComponentType());
            if (uuidComponent != null) {
               this.rebindUuids.put(name, uuidComponent.getUuid());
            }
         }
      }
   }

   private void removeRebind(int targetSlot) {
      if (!this.rebindUuids.isEmpty() && this.slotToNameMap != null) {
         String name = this.slotToNameMap.get(targetSlot);
         if (name != null) {
            this.rebindUuids.remove(name);
         }
      }
   }

   public boolean isRebindSlot(int targetSlot) {
      if (!this.rebindUuids.isEmpty() && this.slotToNameMap != null) {
         String name = this.slotToNameMap.get(targetSlot);
         return name != null && this.rebindUuids.containsKey(name);
      } else {
         return false;
      }
   }

   public void resolveRebinds(@Nonnull ComponentAccessor<EntityStore> accessor) {
      if (!this.rebindUuids.isEmpty() && this.slotToNameMap != null) {
         Iterator<Entry<String, UUID>> it = this.rebindUuids.entrySet().iterator();

         while (it.hasNext()) {
            Entry<String, UUID> entry = it.next();
            int slot = this.targetSlotMappings.getInt(entry.getKey());
            if (slot < 0) {
               it.remove();
            } else {
               Ref<EntityStore> current = this.entityTargets[slot];
               if (current == null || !current.isValid()) {
                  Ref<EntityStore> resolved = accessor.getExternalData().getRefFromUUID(entry.getValue());
                  if (resolved != null && resolved.isValid()) {
                     this.entityTargets[slot] = resolved;
                  } else {
                     this.entityTargets[slot] = null;
                     it.remove();
                  }
               }
            }
         }
      }
   }

   @Nullable
   public Ref<EntityStore> getMarkedEntityRef(String targetSlot) {
      int slot = this.targetSlotMappings.getInt(targetSlot);
      return slot >= 0 ? this.getMarkedEntityRef(slot) : null;
   }

   @Nullable
   public Ref<EntityStore> getMarkedEntityRef(int targetSlot) {
      Ref<EntityStore> ref = this.entityTargets[targetSlot];
      return ref != null && ref.isValid() ? ref : null;
   }

   public int getMarkedEntitySlotCount() {
      return this.entityTargets.length;
   }

   public Vector3d getStoredPosition(int slot) {
      return this.storedPositions[slot];
   }

   public boolean hasMarkedEntity(@Nonnull Ref<EntityStore> entityReference, int targetSlot) {
      return entityReference.equals(this.getMarkedEntityRef(targetSlot));
   }

   public boolean hasMarkedEntityInSlot(String targetSlot) {
      int slot = this.targetSlotMappings.getInt(targetSlot);
      return slot < 0 ? false : this.hasMarkedEntityInSlot(slot);
   }

   public boolean hasMarkedEntityInSlot(int targetSlot) {
      return this.getMarkedEntityRef(targetSlot) != null;
   }

   public void flockSetTarget(
      @Nonnull Ref<EntityStore> selfRef, @Nonnull String targetSlot, @Nullable Ref<EntityStore> targetRef, @Nonnull Store<EntityStore> store
   ) {
      Ref<EntityStore> flockReference = FlockPlugin.getFlockReference(selfRef, store);
      if (flockReference != null) {
         store.getComponent(flockReference, EntityGroup.getComponentType()).forEachMember((member, var1x, _target, _targetSlot) -> {
            NPCEntity npcComponent = member.getStore().getComponent(member, NPC_COMPONENT_TYPE);
            if (npcComponent != null) {
               npcComponent.onFlockSetTarget(_targetSlot, _target);
            }
         }, selfRef, targetRef, targetSlot);
      }
   }

   public void setTargetSlotToIgnoreForAvoidance(int targetSlotToIgnoreForAvoidance) {
      this.targetSlotToIgnoreForAvoidance = targetSlotToIgnoreForAvoidance >= 0 ? targetSlotToIgnoreForAvoidance : this.defaultTargetSlot;
   }

   @Nullable
   public Ref<EntityStore> getTargetReferenceToIgnoreForAvoidance() {
      return this.targetSlotToIgnoreForAvoidance < 0 ? null : this.getMarkedEntityRef(this.targetSlotToIgnoreForAvoidance);
   }

   public String getSlotName(int slot) {
      return this.slotToNameMap.get(slot);
   }

   public void unloaded() {
      Arrays.fill(this.entityTargets, null);
   }

   @Nonnull
   @Override
   public Component<EntityStore> clone() {
      MarkedEntitySupport copy = new MarkedEntitySupport();
      copy.rebindUuids = new HashMap<>(this.rebindUuids);
      return copy;
   }

   static {
      EMPTY_TARGET_SLOT_MAP.defaultReturnValue(Integer.MIN_VALUE);
   }
}
