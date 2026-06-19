package com.hypixel.hytale.server.npc.corecomponents.world;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.world.builders.BuilderSensorEvent;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.role.support.MarkedEntitySupport;
import com.hypixel.hytale.server.npc.sensorinfo.EntityPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SensorEvent extends SensorBase {
   protected static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
   protected final double range;
   protected final SensorEvent.EventSearchType searchType;
   protected final int lockOnTargetSlot;
   protected final boolean lockRebind;
   protected int playerEventMessageSlot;
   protected int npcEventMessageSlot;
   protected final EntityPositionProvider positionProvider = new EntityPositionProvider();

   public SensorEvent(@Nonnull BuilderSensorEvent builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.range = builder.getRange(support);
      this.searchType = builder.getEventSearchType(support);
      this.lockOnTargetSlot = builder.getLockOnTargetSlot(support);
      this.lockRebind = builder.isLockRebind(support);
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      if (!super.matches(ref, executionSupport, dt, store)) {
         this.positionProvider.clear();
         return false;
      }

      switch (this.searchType) {
         case PlayerFirst:
            Ref<EntityStore> playerFirstTarget = this.getPlayerTarget(ref, store);
            if (playerFirstTarget == null) {
               playerFirstTarget = this.getNpcTarget(ref, store);
            }

            return this.setTarget(executionSupport.getMarkedEntitySupport(), playerFirstTarget, store);
         case PlayerOnly:
            return this.setTarget(executionSupport.getMarkedEntitySupport(), this.getPlayerTarget(ref, store), store);
         case NpcFirst:
            Ref<EntityStore> npcFirstTarget = this.getNpcTarget(ref, store);
            if (npcFirstTarget == null) {
               npcFirstTarget = this.getPlayerTarget(ref, store);
            }

            return this.setTarget(executionSupport.getMarkedEntitySupport(), npcFirstTarget, store);
         case NpcOnly:
            return this.setTarget(executionSupport.getMarkedEntitySupport(), this.getNpcTarget(ref, store), store);
         default:
            return false;
      }
   }

   @Override
   public InfoProvider getSensorInfo() {
      return this.positionProvider;
   }

   protected boolean setTarget(
      @Nonnull MarkedEntitySupport support, @Nullable Ref<EntityStore> target, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (target == null) {
         this.positionProvider.clear();
         return false;
      }

      Ref<EntityStore> set = this.positionProvider.setTarget(target, componentAccessor);
      if (set == null) {
         return false;
      }

      if (this.lockOnTargetSlot >= 0) {
         support.setMarkedEntity(this.lockOnTargetSlot, set, this.lockRebind, componentAccessor);
      }

      return true;
   }

   @Nullable
   protected abstract Ref<EntityStore> getPlayerTarget(@Nonnull Ref<EntityStore> var1, @Nonnull Store<EntityStore> var2);

   @Nullable
   protected abstract Ref<EntityStore> getNpcTarget(@Nonnull Ref<EntityStore> var1, @Nonnull Store<EntityStore> var2);

   public enum EventSearchType implements Supplier<String> {
      PlayerFirst("search for events triggered by players first"),
      PlayerOnly("search only for events triggered by players"),
      NpcFirst("search for events triggered by npcs first"),
      NpcOnly("search only for events triggered by npcs");

      private final String description;

      EventSearchType(String description) {
         this.description = description;
      }

      public String get() {
         return this.description;
      }
   }
}
