package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerCondition;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.conditions.PlayerCountCondition;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ActiveSlotInventoryComponent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;

public class ItemCondition extends TriggerCondition {
   @Nonnull
   public static final BuilderCodec<ItemCondition> CODEC = BuilderCodec.builder(ItemCondition.class, ItemCondition::new, BASE_CODEC)
      .append(new KeyedCodec<>("Item", Codec.STRING), (condition, itemId) -> condition.itemId = itemId, condition -> condition.itemId)
      .add()
      .append(
         new KeyedCodec<>("Location", new EnumCodec<>(ItemCondition.Location.class), false),
         (condition, location) -> condition.location = location,
         condition -> condition.location
      )
      .add()
      .append(new KeyedCodec<>("Quantity", Codec.INTEGER, false), (condition, quantity) -> condition.quantity = quantity, condition -> condition.quantity)
      .add()
      .append(new KeyedCodec<>("Consume", Codec.BOOLEAN, false), (condition, consume) -> condition.consume = consume, condition -> condition.consume)
      .add()
      .append(
         new KeyedCodec<>("Comparison", new EnumCodec<>(PlayerCountCondition.Comparison.class), false),
         (condition, comparison) -> condition.comparison = comparison,
         condition -> condition.comparison
      )
      .add()
      .append(
         new KeyedCodec<>("EmptyInventory", Codec.BOOLEAN, false),
         (condition, emptyInventory) -> condition.emptyInventory = emptyInventory,
         condition -> condition.emptyInventory
      )
      .add()
      .append(
         new KeyedCodec<>("MetadataKey", Codec.STRING, false),
         (condition, metadataKey) -> condition.metadataKey = metadataKey,
         condition -> condition.metadataKey
      )
      .add()
      .append(
         new KeyedCodec<>("MetadataValue", Codec.STRING, false),
         (condition, metadataValue) -> condition.metadataValue = metadataValue,
         condition -> condition.metadataValue
      )
      .add()
      .build();
   @Nullable
   private String itemId;
   @Nonnull
   private ItemCondition.Location location = ItemCondition.Location.IN_HAND;
   private int quantity = 1;
   private boolean consume;
   @Nonnull
   private PlayerCountCondition.Comparison comparison = PlayerCountCondition.Comparison.AT_LEAST;
   private boolean emptyInventory;
   @Nonnull
   private String metadataKey = "";
   @Nonnull
   private String metadataValue = "";

   @Nonnull
   public static ItemCondition create(@Nonnull TriggerEventType eventType, @Nonnull String itemId, @Nonnull ItemCondition.Location location, int quantity) {
      ItemCondition condition = new ItemCondition();
      condition.setEventType(eventType);
      condition.itemId = itemId;
      condition.location = location;
      condition.quantity = quantity;
      return condition;
   }

   @Override
   public boolean test(@Nonnull TriggerContext context) {
      Store<EntityStore> store = context.getStore();
      Ref<EntityStore> ref = context.getEntityRef();
      if (store.getComponent(ref, Player.getComponentType()) == null) {
         return false;
      }

      if (this.emptyInventory) {
         CombinedItemContainer combined = InventoryComponent.getCombined(store, ref, InventoryComponent.EVERYTHING);
         return combined.countItemStacks(stack -> !ItemStack.isEmpty(stack)) == 0;
      }

      if (this.itemId != null && !this.itemId.isBlank()) {
         int requiredQuantity = Math.max(0, this.quantity);

         int count = switch (this.location != null ? this.location : ItemCondition.Location.IN_HAND) {
            case IN_HAND -> this.countStack(InventoryComponent.getItemInHand(store, ref));
            case HOTBAR -> {
               InventoryComponent.Hotbar hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
               yield hotbar != null ? this.countStacks(hotbar.getInventory()) : 0;
            }
            case INVENTORY -> this.countStacks(InventoryComponent.getCombined(store, ref, InventoryComponent.EVERYTHING));
         };
         return this.matches(count, requiredQuantity);
      } else {
         return true;
      }
   }

   private boolean matches(int count, int requiredQuantity) {
      return switch (this.comparison) {
         case AT_LEAST -> count >= requiredQuantity;
         case AT_MOST -> count <= requiredQuantity;
         case EXACTLY -> count == requiredQuantity;
         case MORE_THAN -> count > requiredQuantity;
         case LESS_THAN -> count < requiredQuantity;
      };
   }

   @Override
   public void applyOnAccept(@Nonnull TriggerContext context) {
      if (this.consume && !this.emptyInventory && this.itemId != null && !this.itemId.isBlank()) {
         int requiredQuantity = Math.max(0, this.quantity);
         if (requiredQuantity != 0) {
            this.consumeMatchedItems(context, requiredQuantity);
         }
      }
   }

   private void consumeMatchedItems(@Nonnull TriggerContext context, int requiredQuantity) {
      switch (this.location != null ? this.location : ItemCondition.Location.IN_HAND) {
         case IN_HAND:
            this.consumeFromActiveInventory(context, requiredQuantity);
            break;
         case HOTBAR:
            InventoryComponent.Hotbar hotbar = context.getStore().getComponent(context.getEntityRef(), InventoryComponent.Hotbar.getComponentType());
            if (hotbar != null) {
               hotbar.getInventory().removeItemStack(new ItemStack(this.itemId, requiredQuantity));
            }
            break;
         case INVENTORY:
            InventoryComponent.getCombined(context.getStore(), context.getEntityRef(), InventoryComponent.EVERYTHING)
               .removeItemStack(new ItemStack(this.itemId, requiredQuantity));
      }
   }

   private void consumeFromActiveInventory(@Nonnull TriggerContext context, int requiredQuantity) {
      InventoryComponent.Tool toolComponent = context.getStore().getComponent(context.getEntityRef(), InventoryComponent.Tool.getComponentType());
      if (toolComponent == null || !toolComponent.isUsingToolsItem() || !this.consumeFromActiveSlot(toolComponent, requiredQuantity)) {
         InventoryComponent.Hotbar hotbarComponent = context.getStore().getComponent(context.getEntityRef(), InventoryComponent.Hotbar.getComponentType());
         if (hotbarComponent != null) {
            this.consumeFromActiveSlot(hotbarComponent, requiredQuantity);
         }
      }
   }

   private boolean consumeFromActiveSlot(@Nonnull ActiveSlotInventoryComponent inventoryComponent, int requiredQuantity) {
      byte activeSlot = inventoryComponent.getActiveSlot();
      if (activeSlot == -1) {
         return false;
      }

      ItemStack activeItem = inventoryComponent.getInventory().getItemStack(activeSlot);
      if (this.countStack(activeItem) < requiredQuantity) {
         return false;
      }

      inventoryComponent.getInventory().removeItemStackFromSlot(activeSlot, activeItem, requiredQuantity);
      return true;
   }

   private int countStacks(@Nonnull ItemContainer container) {
      return container.countItemStacks(stack -> this.itemId.equals(stack.getItemId()) && this.matchesMetadata(stack));
   }

   private int countStack(@Nullable ItemStack stack) {
      return !ItemStack.isEmpty(stack) && this.itemId.equals(stack.getItemId()) && this.matchesMetadata(stack) ? stack.getQuantity() : 0;
   }

   private boolean matchesMetadata(@Nonnull ItemStack stack) {
      if (this.metadataKey.isBlank()) {
         return true;
      }

      BsonDocument metadata = stack.getMetadata();
      if (metadata == null) {
         return false;
      }

      BsonValue value = metadata.get(this.metadataKey);
      if (value == null) {
         return false;
      }

      if (this.metadataValue.isBlank()) {
         return true;
      }

      String actual = value instanceof BsonString bsonString ? bsonString.getValue() : value.toString();
      return this.metadataValue.equals(actual);
   }

   public enum Location {
      IN_HAND,
      HOTBAR,
      INVENTORY;
   }
}
