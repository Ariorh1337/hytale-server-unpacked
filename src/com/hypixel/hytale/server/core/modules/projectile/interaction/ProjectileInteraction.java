package com.hypixel.hytale.server.core.modules.projectile.interaction;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.projectile.ProjectileModule;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticData;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticDataProvider;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProjectileInteraction extends SimpleInstantInteraction implements BallisticDataProvider {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static double SPAWN_TRANSFORM_DESYNC_ALLOWANCE_MULTIPLIER = 1.25;
   private static double SPAWN_TRANSFORM_DESYNC_ALLOWANCE_MINIMUM_SQUARED = 0.5;
   @Nonnull
   public static final BuilderCodec<ProjectileInteraction> CODEC = BuilderCodec.builder(
         ProjectileInteraction.class, ProjectileInteraction::new, SimpleInstantInteraction.CODEC
      )
      .documentation("Fires a projectile.")
      .<String>appendInherited(new KeyedCodec<>("Config", Codec.STRING), (o, i) -> o.config = i, o -> o.config, (o, p) -> o.config = p.config)
      .addValidator(ProjectileConfig.VALIDATOR_CACHE.getValidator().late())
      .documentation("The ID of the projectile config asset to use for the projectile.")
      .add()
      .build();
   protected String config;

   @Nullable
   public ProjectileConfig getConfig() {
      return ProjectileConfig.getAssetMap().getAsset(this.config);
   }

   @Nullable
   @Override
   public BallisticData getBallisticData() {
      return this.getConfig();
   }

   @Nonnull
   @Override
   public WaitForDataFrom getWaitForDataFrom() {
      return WaitForDataFrom.Client;
   }

   @Override
   public boolean needsRemoteSync() {
      return true;
   }

   @Override
   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
      ProjectileConfig config = this.getConfig();
      if (config != null) {
         Ref<EntityStore> ref = context.getEntity();
         CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
         assert commandBuffer != null;
         InteractionSyncData clientState = context.getClientState();
         UUID generatedUUID = null;
         if (clientState != null && clientState.generatedUUID != null) {
            generatedUUID = clientState.generatedUUID;
         }

         Transform spawnTransform = this.getProjectileSpawnSource(clientState, context);
         ProjectileModule.get().spawnProjectile(generatedUUID, ref, commandBuffer, config, spawnTransform.getPosition(), spawnTransform.getDirection());
      }
   }

   @Override
   protected void simulateFirstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
      CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
      assert commandBuffer != null;
      Ref<EntityStore> ref = context.getEntity();
      Transform lookVec = TargetUtil.getLook(ref, commandBuffer);
      InteractionSyncData state = context.getState();
      state.attackerPos = PositionUtil.toPositionPacket(lookVec.getPosition());
      Rotation3f rotation = lookVec.getRotation();
      state.attackerRot = new Direction(rotation.yaw(), rotation.pitch(), rotation.roll());
   }

   @Nonnull
   @Override
   protected Interaction generatePacket() {
      return new com.hypixel.hytale.protocol.ProjectileInteraction();
   }

   @Override
   protected void configurePacket(Interaction packet) {
      super.configurePacket(packet);
      com.hypixel.hytale.protocol.ProjectileInteraction p = (com.hypixel.hytale.protocol.ProjectileInteraction)packet;
      ProjectileConfig config = this.getConfig();
      if (config == null) {
         throw new IllegalStateException("ProjectileInteraction '" + this.getId() + "' has no valid ProjectileConfig: " + this.config);
      }

      p.configId = this.config;
   }

   @Nonnull
   private Transform getProjectileSpawnSource(@Nullable InteractionSyncData clientData, @Nonnull InteractionContext context) {
      Transform serverTransform = TargetUtil.getLook(context.getEntity(), context.getCommandBuffer());
      if (clientData != null && clientData.attackerPos != null && clientData.attackerRot != null) {
         double distSq = serverTransform.getPosition().distanceSquared(clientData.attackerPos.x, clientData.attackerPos.y, clientData.attackerPos.z);
         Velocity velocityComponent = context.getCommandBuffer().getComponent(context.getEntity(), Velocity.getComponentType());
         assert velocityComponent != null;
         double desyncAllowanceSq = Math.max(velocityComponent.getClientVelocity().lengthSquared(), SPAWN_TRANSFORM_DESYNC_ALLOWANCE_MINIMUM_SQUARED);
         if (distSq > desyncAllowanceSq * SPAWN_TRANSFORM_DESYNC_ALLOWANCE_MULTIPLIER) {
            LOGGER.at(Level.WARNING)
               .log("Entity %d was too far from requested projectile spawn position (%f > %f)", context.getEntity().getIndex(), distSq, desyncAllowanceSq);
            return serverTransform;
         } else {
            return new Transform(
               clientData.attackerPos.x,
               clientData.attackerPos.y,
               clientData.attackerPos.z,
               clientData.attackerRot.pitch,
               clientData.attackerRot.yaw,
               clientData.attackerRot.roll
            );
         }
      } else {
         return serverTransform;
      }
   }
}
