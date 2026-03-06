package com.hypixel.hytale.builtin.buildertools.tooloperations;

import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import javax.annotation.Nonnull;

public class TintOperation extends ToolOperation {
   private final int tintColor;
   private final double opacity;
   private boolean blendMode = false;
   private int bufferOriginX;
   private int bufferOriginZ;
   private int[][] colorBuffer;
   private final LongOpenHashSet packedPlacedTinsPositions;
   private static final int SAMPLE_DISTANCE = 4;

   public TintOperation(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull Player player,
      @Nonnull BuilderToolOnUseInteraction packet,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      super(ref, packet, componentAccessor);
      String colorText = (String)this.args.tool().get("bTintColor");
      if (this.args.tool().get("aMode").equals("blend")) {
         this.blendMode = true;
      }

      try {
         this.tintColor = ColorParseUtil.hexStringToRGBInt(colorText);
      } catch (NumberFormatException e) {
         player.sendMessage(Message.translation("server.builderTools.tintOperation.colorParseError").param("value", colorText));
         throw e;
      }

      this.opacity = ((Integer)this.args.tool().getOrDefault("cOpacity", 0)).intValue() / 100.0;
      UUIDComponent uuidComponent = ref.getStore().getComponent(ref, UUIDComponent.getComponentType());
      PrototypePlayerBuilderToolSettings prototypeSettings = PROTOTYPE_TOOL_SETTINGS.get(uuidComponent.getUuid());
      if (!packet.isHoldDownInteraction) {
         prototypeSettings.getIgnoredPaintOperations().clear();
      }

      this.packedPlacedTinsPositions = prototypeSettings.addIgnoredPaintOperation();

      for (LongOpenHashSet previousSet : prototypeSettings.getIgnoredPaintOperations()) {
         if (previousSet != this.packedPlacedTinsPositions) {
            this.packedPlacedTinsPositions.addAll(previousSet);
         }
      }

      if (this.blendMode) {
         int bufferSize = (this.shapeRange + 4) * 2 + 1;
         this.bufferOriginX = this.x - this.shapeRange - 4;
         this.bufferOriginZ = this.z - this.shapeRange - 4;
         this.colorBuffer = new int[bufferSize][bufferSize];

         for (int bufferX = 0; bufferX < bufferSize; bufferX++) {
            for (int bufferZ = 0; bufferZ < bufferSize; bufferZ++) {
               this.colorBuffer[bufferX][bufferZ] = this.edit.getTint(this.bufferOriginX + bufferX, this.bufferOriginZ + bufferZ);
            }
         }
      }
   }

   @Override
   boolean execute0(int x, int y, int z) {
      long packed = BlockUtil.pack(x, 0, z);
      if (this.packedPlacedTinsPositions.contains(packed)) {
         return true;
      }

      this.packedPlacedTinsPositions.add(packed);
      if (this.blendMode) {
         int targetColor = this.sampleKernelBlend(x, z);
         this.edit.setTint(x, z, targetColor, 0.0);
      } else {
         this.edit.setTint(x, z, this.tintColor, this.opacity);
      }

      return true;
   }

   private int sampleKernelBlend(int x, int z) {
      double totalWeight = 0.0;
      double r = 0.0;
      double g = 0.0;
      double b = 0.0;

      for (int deltaX = -4; deltaX <= 4; deltaX++) {
         for (int deltaZ = -4; deltaZ <= 4; deltaZ++) {
            double dist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            if (!(dist > 4.0)) {
               int bufferX = x + deltaX - this.bufferOriginX;
               int bufferZ = z + deltaZ - this.bufferOriginZ;
               if (bufferX >= 0 && bufferZ >= 0 && bufferX < this.colorBuffer.length && bufferZ < this.colorBuffer[0].length) {
                  double sigma = 2.0;
                  double weight = Math.exp(-(dist * dist) / (2.0 * sigma * sigma));
                  int color = this.colorBuffer[bufferX][bufferZ];
                  r += (color >> 16 & 0xFF) * weight;
                  g += (color >> 8 & 0xFF) * weight;
                  b += (color & 0xFF) * weight;
                  totalWeight += weight;
               }
            }
         }
      }

      return totalWeight == 0.0
         ? this.tintColor
         : (int)Math.round(r / totalWeight) << 16 | (int)Math.round(g / totalWeight) << 8 | (int)Math.round(b / totalWeight);
   }
}
