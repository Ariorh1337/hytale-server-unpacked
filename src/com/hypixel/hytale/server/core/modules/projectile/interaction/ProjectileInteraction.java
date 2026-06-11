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
import com.hypixel.hytale.server.core.modules.interaction.interaction.util.InteractionValidation;
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
      .<Boolean>appendInherited(
         new KeyedCodec<>("IgnorePitch", Codec.BOOLEAN), (o, i) -> o.ignorePitch = i, o -> o.ignorePitch, (o, p) -> o.ignorePitch = p.ignorePitch
      )
      .documentation(
         "If true, the shooter's pitch is set to 0 before the launch direction is computed, so the projectile's pitch is fixed by the projectile config's SpawnRotationOffset."
      )
      .add()
      .<Boolean>appendInherited(new KeyedCodec<>("IgnoreYaw", Codec.BOOLEAN), (o, i) -> o.ignoreYaw = i, o -> o.ignoreYaw, (o, p) -> o.ignoreYaw = p.ignoreYaw)
      .documentation(
         "If true, the shooter's yaw is set to 0 before the launch direction is computed, so the projectile's yaw is fixed by the projectile config's SpawnRotationOffset."
      )
      .add()
      .<Boolean>appendInherited(
         new KeyedCodec<>("IgnoreRoll", Codec.BOOLEAN), (o, i) -> o.ignoreRoll = i, o -> o.ignoreRoll, (o, p) -> o.ignoreRoll = p.ignoreRoll
      )
      .documentation(
         "If true, the shooter's roll is set to 0 before the launch direction is computed, so the projectile's roll is fixed by the projectile config's SpawnRotationOffset."
      )
      .add()
      .build();
   protected String config;
   protected boolean ignorePitch;
   protected boolean ignoreYaw;
   protected boolean ignoreRoll;

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
         Rotation3f rotation = spawnTransform.getRotation();
         if (this.ignorePitch) {
            rotation.setPitch(0.0F);
         }

         if (this.ignoreYaw) {
            rotation.setYaw(0.0F);
         }

         if (this.ignoreRoll) {
            rotation.setRoll(0.0F);
         }

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
      p.ignorePitch = this.ignorePitch;
      p.ignoreYaw = this.ignoreYaw;
      p.ignoreRoll = this.ignoreRoll;
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
               .log(
                  "%s was too far from requested projectile spawn position (%f > %f)",
                  InteractionValidation.getEntityName(context.getEntity(), context.getCommandBuffer()),
                  distSq,
                  desyncAllowanceSq
               );
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
