package com.hypixel.hytale.server.npc.corecomponents;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.blackboard.Blackboard;
import com.hypixel.hytale.server.npc.blackboard.view.resource.ResourceView;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class BlockTarget {
   private final Vector3d position = new Vector3d(Vector3dUtil.MIN);
   private int chunkChangeRevision = -1;
   private int foundBlockType = Integer.MIN_VALUE;
   @Nullable
   private ResourceView reservationHolder;

   @Nonnull
   public Vector3d getPosition() {
      return this.position;
   }

   public int getChunkChangeRevision() {
      return this.chunkChangeRevision;
   }

   public int getFoundBlockType() {
      return this.foundBlockType;
   }

   public void setChunkChangeRevision(int chunkChangeRevision) {
      this.chunkChangeRevision = chunkChangeRevision;
   }

   public void setFoundBlockType(int foundBlockType) {
      this.foundBlockType = foundBlockType;
   }

   public void setReservationHolder(ResourceView resourceView) {
      this.reservationHolder = resourceView;
   }

   public void reset(@Nonnull Ref<EntityStore> selfRef) {
      if (this.reservationHolder != null) {
         this.reservationHolder.clearReservation(selfRef);
         Blackboard.LOGGER.at(Level.FINE).log("Entity %d cleared reservation at %s", selfRef.getIndex(), this.position);
      }

      this.reservationHolder = null;
      this.position.set(Vector3dUtil.MIN);
      this.chunkChangeRevision = -1;
      this.foundBlockType = Integer.MIN_VALUE;
   }

   public boolean isActive() {
      return this.foundBlockType >= 0;
   }
}
