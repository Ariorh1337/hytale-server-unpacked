package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.util.IComponentExecutionControl;
import com.hypixel.hytale.server.npc.util.expression.StdLib;
import com.hypixel.hytale.server.npc.util.expression.StdScope;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public class EntitySupport implements Component<EntityStore> {
   protected StdScope sensorScope;
   protected final List<IComponentExecutionControl> delayingComponents = new ObjectArrayList<>();
   protected final List<EntitySupport.DeferredAction> deferredActions = new ObjectArrayList<>();

   @Nonnull
   public static ComponentType<EntityStore, EntitySupport> getComponentType() {
      return NPCPlugin.get().getEntitySupportComponentType();
   }

   @Nonnull
   public static EntitySupport get(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      EntitySupport support = accessor.getComponent(ref, getComponentType());
      assert support != null : "Missing EntitySupport on entity " + ref;
      return support;
   }

   public StdScope getSensorScope() {
      return this.sensorScope;
   }

   public void postRoleBuilt(@Nonnull BuilderSupport builderSupport) {
      this.sensorScope = builderSupport.getSensorScope();
   }

   public void tick(float dt) {
      int i = 0;

      while (i < this.delayingComponents.size()) {
         IComponentExecutionControl component = this.delayingComponents.get(i);
         if (component.processDelay(dt)) {
            this.delayingComponents.remove(i);
         } else {
            i++;
         }
      }
   }

   public void registerDelay(@Nonnull IComponentExecutionControl component) {
      this.delayingComponents.add(component);
   }

   public void addDeferredAction(@Nonnull EntitySupport.DeferredAction handler) {
      this.deferredActions.add(handler);
   }

   public void tickDeferredActions(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      int i = 0;

      while (i < this.deferredActions.size()) {
         EntitySupport.DeferredAction action = this.deferredActions.get(i);
         if (action.tick(ref, executionSupport, dt, store)) {
            this.deferredActions.remove(i);
         } else {
            i++;
         }
      }
   }

   public void unloaded() {
      this.deferredActions.clear();
   }

   @Nonnull
   public static StdScope createScope() {
      return new StdScope(StdLib.getInstance());
   }

   @Nonnull
   public static StdScope createScope(@Nonnull NPCEntity entity) {
      StdScope scope = new StdScope(StdLib.getInstance());
      scope.addSupplier("blocked", () -> entity.getRole().getActiveMotionController().isObstructed());
      scope.addSupplier("health", () -> {
         EntityStatValue healthStat = EntityStatsModule.get(entity).get(DefaultEntityStatTypes.getHealth());
         return Objects.requireNonNull(healthStat).asPercentage();
      });
      return scope;
   }

   @Override
   public Component<EntityStore> clone() {
      return this;
   }

   @FunctionalInterface
   public interface DeferredAction {
      boolean tick(@Nonnull Ref<EntityStore> var1, @Nonnull ExecutionSupport var2, double var3, @Nonnull Store<EntityStore> var5);
   }
}
