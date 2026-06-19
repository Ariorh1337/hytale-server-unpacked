package com.hypixel.hytale.server.npc.corecomponents.entity.filters;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.EntityFilterBase;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import javax.annotation.Nonnull;

public class EntityFilterDeath extends EntityFilterBase {
   public static final int COST = 0;
   protected static final ComponentType<EntityStore, DeathComponent> DEATH_COMPONENT_TYPE = DeathComponent.getComponentType();

   @Override
   public boolean matchesEntity(
      @Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull ExecutionSupport executionSupport, @Nonnull Store<EntityStore> store
   ) {
      return store.getArchetype(targetRef).contains(DEATH_COMPONENT_TYPE);
   }

   @Override
   public int cost() {
      return 0;
   }
}
