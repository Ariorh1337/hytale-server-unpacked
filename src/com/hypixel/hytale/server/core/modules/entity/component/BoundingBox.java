package com.hypixel.hytale.server.core.modules.entity.component;

import com.google.common.flogger.StackSize;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.server.core.asset.type.model.config.DetailBox;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BoundingBox implements Component<EntityStore> {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private final Box boundingBox = new Box();
   @Nullable
   private Box baseModelBox;
   protected Map<String, DetailBox[]> detailBoxes;

   public static ComponentType<EntityStore, BoundingBox> getComponentType() {
      return EntityModule.get().getBoundingBoxComponentType();
   }

   public BoundingBox() {
   }

   public BoundingBox(@Nonnull Box boundingBox) {
      this.setBoundingBox(boundingBox);
   }

   @Nonnull
   public Box getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(@Nonnull Box boundingBox) {
      this.boundingBox.assign(boundingBox);
   }

   public void setBaseModelBox(@Nullable Box baseModelBox) {
      this.baseModelBox = baseModelBox != null ? baseModelBox.clone() : null;
   }

   public void applyRotation(float pitch, float yaw, float roll) {
      if (this.baseModelBox != null) {
         if (Float.isFinite(pitch) && Float.isFinite(yaw) && Float.isFinite(roll)) {
            this.boundingBox.assign(this.baseModelBox.enclosingRotatedAABB(pitch, yaw, roll));
         } else {
            LOGGER.at(Level.SEVERE)
               .atMostEvery(1, TimeUnit.MINUTES)
               .withStackTrace(StackSize.MEDIUM)
               .log("Non-finite entity rotation reached applyRotation (pitch=%s, yaw=%s, roll=%s); leaving bounding box unrotated", pitch, yaw, roll);
            this.boundingBox.assign(this.baseModelBox);
         }
      }
   }

   public Map<String, DetailBox[]> getDetailBoxes() {
      return this.detailBoxes;
   }

   public void setDetailBoxes(Map<String, DetailBox[]> detailBoxes) {
      this.detailBoxes = detailBoxes;
   }

   @Nonnull
   @Override
   public Component<EntityStore> clone() {
      BoundingBox copy = new BoundingBox(this.boundingBox);
      copy.baseModelBox = this.baseModelBox != null ? this.baseModelBox.clone() : null;
      if (this.detailBoxes != null) {
         HashMap<String, DetailBox[]> detailBoxesCopy = new HashMap<>(this.detailBoxes.size());

         for (Entry<String, DetailBox[]> entry : this.detailBoxes.entrySet()) {
            DetailBox[] source = entry.getValue();
            DetailBox[] childDetailBoxes = null;
            if (source != null) {
               childDetailBoxes = new DetailBox[source.length];

               for (int i = 0; i < source.length; i++) {
                  childDetailBoxes[i] = source[i] != null ? new DetailBox(source[i]) : null;
               }
            }

            detailBoxesCopy.put(entry.getKey(), childDetailBoxes);
         }

         copy.detailBoxes = detailBoxesCopy;
      }

      return copy;
   }
}
