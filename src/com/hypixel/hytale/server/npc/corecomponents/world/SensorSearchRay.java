package com.hypixel.hytale.server.npc.corecomponents.world;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.world.builders.BuilderSensorSearchRay;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.PositionProvider;
import com.hypixel.hytale.server.npc.util.RayBlockHitTest;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SensorSearchRay extends SensorBase {
   protected final int id;
   protected final float angle;
   protected final double range;
   protected final int blockSet;
   protected final float minRetestAngle;
   protected final double minRetestMoveSquared;
   protected final double throttleTime;
   protected final PositionProvider positionProvider = new PositionProvider();
   protected final Vector3d lastCheckedPosition = new Vector3d();
   protected float lastCheckedYaw = Float.MAX_VALUE;
   protected short lastBlockRevision;
   protected double throttleTimeRemaining;

   public SensorSearchRay(@Nonnull BuilderSensorSearchRay builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.id = builder.getId(support);
      this.angle = -builder.getAngle(support);
      this.range = builder.getRange(support);
      this.blockSet = builder.getBlockSet(support);
      this.minRetestAngle = builder.getMinRetestAngle(support);
      double minRetestMove = builder.getMinRetestMove(support);
      this.minRetestMoveSquared = minRetestMove * minRetestMove;
      this.throttleTime = builder.getThrottleTime(support);
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      if (!super.matches(ref, executionSupport, dt, store)) {
         this.positionProvider.clear();
         return false;
      }

      TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
      assert transformComponent != null;
      HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());
      assert headRotationComponent != null;
      Vector3d position = transformComponent.getPosition();
      Rotation3f headRotation = headRotationComponent.getRotation();
      Vector3d cachedPosition = executionSupport.getWorldSupport().getCachedSearchRayPosition(this.id);
      if (!cachedPosition.equals(Vector3dUtil.MIN)) {
         ChunkStore chunkStore = store.getExternalData().getWorld().getChunkStore();
         long chunkIndex = ChunkUtil.indexChunkFromBlock(cachedPosition.x, cachedPosition.z);
         Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
         BlockChunk blockChunkComponent = chunkRef != null && chunkRef.isValid()
            ? chunkStore.getStore().getComponent(chunkRef, BlockChunk.getComponentType())
            : null;
         if (blockChunkComponent != null) {
            BlockSection section = blockChunkComponent.getSectionAtBlockY(MathUtil.floor(cachedPosition.y));
            if (section.getLocalChangeCounter() == this.lastBlockRevision) {
               this.positionProvider.setTarget(cachedPosition);
               return true;
            }

            cachedPosition.set(Vector3dUtil.MIN);
            this.positionProvider.clear();
         }
      } else if ((this.throttleTimeRemaining -= dt) > 0.0
         && Math.abs(headRotation.yaw() - this.lastCheckedYaw) <= this.minRetestAngle
         && position.distanceSquared(this.lastCheckedPosition) <= this.minRetestMoveSquared) {
         this.positionProvider.clear();
         return false;
      }

      RayBlockHitTest blockRaySearch = RayBlockHitTest.THREAD_LOCAL.get();
      if (!blockRaySearch.init(ref, this.blockSet, this.angle, store)) {
         cachedPosition.set(Vector3dUtil.MIN);
         this.positionProvider.clear();
         blockRaySearch.clear();
         return false;
      }

      this.lastCheckedPosition.set(position);
      this.lastCheckedYaw = headRotation.yaw();
      this.throttleTimeRemaining = this.throttleTime;
      boolean result = blockRaySearch.run(this.range);
      if (result) {
         this.lastBlockRevision = blockRaySearch.getLastBlockRevision();
         Vector3d targetPosition = blockRaySearch.getHitPosition();
         cachedPosition.set(targetPosition.x + 0.5, targetPosition.y + 0.5, targetPosition.z + 0.5);
         this.positionProvider.setTarget(cachedPosition);
      } else {
         cachedPosition.set(Vector3dUtil.MIN);
         this.positionProvider.clear();
      }

      blockRaySearch.clear();
      return result;
   }

   @Override
   public InfoProvider getSensorInfo() {
      return this.positionProvider;
   }
}
