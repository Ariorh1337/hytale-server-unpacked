package com.hypixel.hytale.builtin.buildertools.utils;

import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

public final class PasteToolUtil {
   private static final String PASTE_TOOL_ID = "EditorTool_Paste";

   private PasteToolUtil() {
   }

   public static void switchToPasteTool(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
      Inventory inventory = player.getInventory();
      ItemContainer hotbar = inventory.getHotbar();
      ItemContainer storage = inventory.getStorage();
      ItemContainer tools = inventory.getTools();
      int hotbarSize = hotbar.getCapacity();

      for (short slot = 0; slot < hotbarSize; slot++) {
         ItemStack itemStack = hotbar.getItemStack(slot);
         if (itemStack != null && !itemStack.isEmpty() && "EditorTool_Paste".equals(itemStack.getItemId())) {
            inventory.setActiveHotbarSlot((byte)slot);
            playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)slot));
            return;
         }
      }

      short emptySlot = -1;

      for (short slot = 0; slot < hotbarSize; slot++) {
         ItemStack itemStack = hotbar.getItemStack(slot);
         if (itemStack == null || itemStack.isEmpty()) {
            emptySlot = slot;
            break;
         }
      }

      if (emptySlot != -1) {
         for (short slot = 0; slot < storage.getCapacity(); slot++) {
            ItemStack itemStack = storage.getItemStack(slot);
            if (itemStack != null && !itemStack.isEmpty() && "EditorTool_Paste".equals(itemStack.getItemId())) {
               storage.moveItemStackFromSlotToSlot(slot, 1, hotbar, emptySlot);
               inventory.setActiveHotbarSlot((byte)emptySlot);
               playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)emptySlot));
               return;
            }
         }

         ItemStack pasteToolStack = null;

         for (short slot = 0; slot < tools.getCapacity(); slot++) {
            ItemStack itemStack = tools.getItemStack(slot);
            if (itemStack != null && !itemStack.isEmpty() && "EditorTool_Paste".equals(itemStack.getItemId())) {
               pasteToolStack = itemStack;
               break;
            }
         }

         if (pasteToolStack != null) {
            hotbar.setItemStackForSlot(emptySlot, new ItemStack(pasteToolStack.getItemId()));
            inventory.setActiveHotbarSlot((byte)emptySlot);
            playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)emptySlot));
         }
      }
   }
}
