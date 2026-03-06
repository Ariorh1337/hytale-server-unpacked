package com.hypixel.hytale.server.core.inventory;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemUtility;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemWeapon;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.StatModifiersManager;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventorySystems {
   public static class ArmorChangeEventSystem extends InventorySystems.InventoryChangeEventSystem<InventoryComponent.Armor> {
      public ArmorChangeEventSystem() {
         super(InventoryComponent.Armor.getComponentType());
      }
   }

   public static class BackpackChangeEventSystem extends InventorySystems.InventoryChangeEventSystem<InventoryComponent.Backpack> {
      public BackpackChangeEventSystem() {
         super(InventoryComponent.Backpack.getComponentType());
      }
   }

   public static class HotbarChangeEventSystem extends InventorySystems.InventoryChangeEventSystem<InventoryComponent.Hotbar> {
      public HotbarChangeEventSystem() {
         super(InventoryComponent.Hotbar.getComponentType());
      }
   }

   public abstract static class InventoryChangeEventSystem<Inv extends InventoryComponent> extends EntityTickingSystem<EntityStore> {
      protected final ComponentType<EntityStore, Inv> componentType;

      protected InventoryChangeEventSystem(ComponentType<EntityStore, Inv> componentType) {
         this.componentType = componentType;
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Inv inventory = archetypeChunk.getComponent(index, this.componentType);
         assert inventory != null;
         ConcurrentLinkedQueue<ItemContainer.ItemContainerChangeEvent> changeEvents = inventory.getChangeEvents();
         ItemContainer.ItemContainerChangeEvent changeEvent = null;
         Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

         while ((changeEvent = changeEvents.poll()) != null) {
            InventoryChangeEvent event = new InventoryChangeEvent(this.componentType, inventory, changeEvent.container(), changeEvent.transaction());
            commandBuffer.invoke(ref, event);
         }
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return this.componentType;
      }
   }

   @Deprecated(forRemoval = true)
   public static class LegacyArmorChangeStatSystem extends EntityTickingSystem<EntityStore> {
      private final Query<EntityStore> query = Query.and(InventoryComponent.Armor.getComponentType(), AllLegacyLivingEntityTypesQuery.INSTANCE);
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.BEFORE, InventorySystems.ArmorChangeEventSystem.class));

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
         assert entity != null;
         InventoryComponent.Armor inventory = archetypeChunk.getComponent(index, InventoryComponent.Armor.getComponentType());
         assert inventory != null;
         ConcurrentLinkedQueue<ItemContainer.ItemContainerChangeEvent> changeEvents = inventory.getChangeEvents();
         if (!changeEvents.isEmpty()) {
            entity.invalidateEquipmentNetwork();
            entity.getStatModifiersManager().setRecalculate(true);
         }
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }
   }

   @Deprecated(forRemoval = true)
   public static class LegacyHotbarChangeStatSystem extends EntityTickingSystem<EntityStore> {
      private final Query<EntityStore> query = Query.and(InventoryComponent.Hotbar.getComponentType(), AllLegacyLivingEntityTypesQuery.INSTANCE);
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.BEFORE, InventorySystems.HotbarChangeEventSystem.class));

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
         assert entity != null;
         InventoryComponent.Hotbar inventory = archetypeChunk.getComponent(index, InventoryComponent.Hotbar.getComponentType());
         assert inventory != null;
         ConcurrentLinkedQueue<ItemContainer.ItemContainerChangeEvent> changeEvents = inventory.getChangeEvents();
         if (!changeEvents.isEmpty()) {
            boolean changed = false;
            byte activeHotbarSlot = inventory.getActiveSlot();

            for (ItemContainer.ItemContainerChangeEvent event : changeEvents) {
               if (activeHotbarSlot != -1
                  && event.transaction().wasSlotModified(activeHotbarSlot)
                  && !(
                     event.transaction() instanceof SlotTransaction slot
                        && slot.getSlotAfter() != null
                        && ItemStack.isEquivalentType(slot.getSlotBefore(), slot.getSlotAfter())
                  )) {
                  changed = true;
               }
            }

            if (changed) {
               StatModifiersManager statModifiersManager = entity.getStatModifiersManager();
               entity.invalidateEquipmentNetwork();
               statModifiersManager.setRecalculate(true);
               ItemStack itemStack = inventory.getActiveItem();
               if (itemStack != null) {
                  ItemWeapon itemWeapon = itemStack.getItem().getWeapon();
                  if (itemWeapon != null) {
                     int[] entityStatsToClear = itemWeapon.getEntityStatsToClear();
                     if (entityStatsToClear != null) {
                        statModifiersManager.queueEntityStatsToClear(entityStatsToClear);
                     }
                  }
               }
            }
         }
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }
   }

   @Deprecated(forRemoval = true)
   public static class LegacyUtilityChangeStatSystem extends EntityTickingSystem<EntityStore> {
      private final Query<EntityStore> query = Query.and(InventoryComponent.Utility.getComponentType(), AllLegacyLivingEntityTypesQuery.INSTANCE);
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.BEFORE, InventorySystems.UtilityChangeEventSystem.class));

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
         assert entity != null;
         InventoryComponent.Utility inventory = archetypeChunk.getComponent(index, InventoryComponent.Utility.getComponentType());
         assert inventory != null;
         ConcurrentLinkedQueue<ItemContainer.ItemContainerChangeEvent> changeEvents = inventory.getChangeEvents();
         if (!changeEvents.isEmpty()) {
            boolean changed = false;
            byte activeHotbarSlot = inventory.getActiveSlot();

            for (ItemContainer.ItemContainerChangeEvent event : changeEvents) {
               if (activeHotbarSlot != -1
                  && event.transaction().wasSlotModified(activeHotbarSlot)
                  && !(
                     event.transaction() instanceof SlotTransaction slot
                        && slot.getSlotAfter() != null
                        && ItemStack.isEquivalentType(slot.getSlotBefore(), slot.getSlotAfter())
                  )) {
                  changed = true;
               }
            }

            if (changed) {
               StatModifiersManager statModifiersManager = entity.getStatModifiersManager();
               entity.invalidateEquipmentNetwork();
               statModifiersManager.setRecalculate(true);
               ItemStack itemStack = inventory.getActiveItem();
               if (itemStack != null) {
                  ItemUtility itemUtility = itemStack.getItem().getUtility();
                  if (itemUtility != null) {
                     int[] entityStatsToClear = itemUtility.getEntityStatsToClear();
                     if (entityStatsToClear != null) {
                        statModifiersManager.queueEntityStatsToClear(entityStatsToClear);
                     }
                  }
               }
            }
         }
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }
   }

   public static class StorageChangeEventSystem extends InventorySystems.InventoryChangeEventSystem<InventoryComponent.Storage> {
      public StorageChangeEventSystem() {
         super(InventoryComponent.Storage.getComponentType());
      }
   }

   public static class ToolChangeEventSystem extends InventorySystems.InventoryChangeEventSystem<InventoryComponent.Tool> {
      public ToolChangeEventSystem() {
         super(InventoryComponent.Tool.getComponentType());
      }
   }

   public static class UtilityChangeEventSystem extends InventorySystems.InventoryChangeEventSystem<InventoryComponent.Utility> {
      public UtilityChangeEventSystem() {
         super(InventoryComponent.Utility.getComponentType());
      }
   }
}
