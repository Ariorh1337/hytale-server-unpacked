package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.gameplay.BrokenPenalties;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.projectile.config.Projectile;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
import com.hypixel.hytale.server.core.inventory.InventoryUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticData;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticDataProvider;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

@Deprecated(forRemoval = true)
public class LaunchProjectileInteraction extends SimpleInstantInteraction implements BallisticDataProvider {
   @Nonnull
   public static final BuilderCodec<LaunchProjectileInteraction> CODEC = BuilderCodec.builder(
         LaunchProjectileInteraction.class, LaunchProjectileInteraction::new, SimpleInstantInteraction.CODEC
      )
      .documentation("Launches a projectile.")
      .<String>appendInherited(
         new KeyedCodec<>("ProjectileId", Codec.STRING), (i, o) -> i.projectileId = o, i -> i.projectileId, (i, p) -> i.projectileId = p.projectileId
      )
      .addValidator(Validators.nonNull())
      .addValidator(Projectile.VALIDATOR_CACHE.getValidator().late())
      .add()
      .<Boolean>appendInherited(
         new KeyedCodec<>("IgnorePitch", Codec.BOOLEAN), (i, o) -> i.ignorePitch = o, i -> i.ignorePitch, (i, p) -> i.ignorePitch = p.ignorePitch
      )
      .documentation("If true, the shooter's pitch is set to 0 before the launch direction is computed, so the projectile's pitch is fixed by RotationOffset.")
      .add()
      .<Boolean>appendInherited(new KeyedCodec<>("IgnoreYaw", Codec.BOOLEAN), (i, o) -> i.ignoreYaw = o, i -> i.ignoreYaw, (i, p) -> i.ignoreYaw = p.ignoreYaw)
      .documentation("If true, the shooter's yaw is set to 0 before the launch direction is computed, so the projectile's yaw is fixed by RotationOffset.")
      .add()
      .<Boolean>appendInherited(
         new KeyedCodec<>("IgnoreRoll", Codec.BOOLEAN), (i, o) -> i.ignoreRoll = o, i -> i.ignoreRoll, (i, p) -> i.ignoreRoll = p.ignoreRoll
      )
      .documentation("If true, the shooter's roll is set to 0 before the launch direction is computed, so the projectile's roll is fixed by RotationOffset.")
      .add()
      .<Direction>appendInherited(new KeyedCodec<>("RotationOffset", ProtocolCodecs.DIRECTION), (i, o) -> {
         i.rotationOffset = o;
         i.rotationOffset.yaw *= (float) (Math.PI / 180.0);
         i.rotationOffset.pitch *= (float) (Math.PI / 180.0);
         i.rotationOffset.roll *= (float) (Math.PI / 180.0);
      }, i -> i.rotationOffset, (i, p) -> i.rotationOffset = p.rotationOffset)
      .addValidator(Validators.nonNull())
      .documentation("A fixed rotation offset (in degrees) added to the launch rotation after any ignored axes are zeroed.")
      .add()
      .build();
   protected String projectileId;
   protected boolean ignorePitch;
   protected boolean ignoreYaw;
   protected boolean ignoreRoll;
   @Nonnull
   protected Direction rotationOffset = new Direction(0.0F, 0.0F, 0.0F);

   public String getProjectileId() {
      return this.projectileId;
   }

   @Nullable
   @Override
   public BallisticData getBallisticData() {
      return Projectile.getAssetMap().getAsset(this.projectileId);
   }

   @Override
   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
      CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
      assert commandBuffer != null;
      World world = commandBuffer.getExternalData().getWorld();
      Ref<EntityStore> sourceRef = context.getEntity();
      Transform lookVec = TargetUtil.getLook(sourceRef, commandBuffer);
      Vector3d lookPosition = lookVec.getPosition();
      Rotation3f lookRotation = lookVec.getRotation();
      if (this.ignorePitch) {
         lookRotation.setPitch(0.0F);
      }

      if (this.ignoreYaw) {
         lookRotation.setYaw(0.0F);
      }

      if (this.ignoreRoll) {
         lookRotation.setRoll(0.0F);
      }

      lookRotation.add(this.rotationOffset.pitch, this.rotationOffset.yaw, this.rotationOffset.roll);
      UUIDComponent sourceUuidComponent = commandBuffer.getComponent(sourceRef, UUIDComponent.getComponentType());
      if (sourceUuidComponent != null) {
         UUID sourceUuid = sourceUuidComponent.getUuid();
         TimeResource timeResource = commandBuffer.getResource(TimeResource.getResourceType());
         Holder<EntityStore> holder = ProjectileComponent.assembleDefaultProjectile(timeResource, this.projectileId, lookPosition, lookRotation);
         ProjectileComponent projectileComponent = holder.getComponent(ProjectileComponent.getComponentType());
         assert projectileComponent != null;
         holder.ensureComponent(Intangible.getComponentType());
         if (projectileComponent.getProjectile() == null) {
            projectileComponent.initialize();
            if (projectileComponent.getProjectile() == null) {
               return;
            }
         }

         projectileComponent.shoot(holder, sourceUuid, lookPosition.x(), lookPosition.y(), lookPosition.z(), lookRotation.yaw(), lookRotation.pitch());
         commandBuffer.addEntity(holder, AddReason.SPAWN);
         ItemStack itemInHand = context.getHeldItem();
         if (itemInHand != null && !itemInHand.isEmpty()) {
            Item item = itemInHand.getItem();
            if (ItemUtils.canDecreaseItemStackDurability(sourceRef, commandBuffer) && !itemInHand.isUnbreakable() && item.getWeapon() != null) {
               ItemContainer section = InventoryUtils.getSectionById(sourceRef, context.getHeldItemSectionId(), commandBuffer);
               if (section != null) {
                  ItemUtils.updateItemStackDurability(sourceRef, itemInHand, section, context.getHeldItemSlot(), -item.getDurabilityLossOnHit(), commandBuffer);
               }
            }

            if (itemInHand.isBroken()) {
               BrokenPenalties brokenPenalties = world.getGameplayConfig().getItemDurabilityConfig().getBrokenPenalties();
               projectileComponent.applyBrokenPenalty((float)brokenPenalties.getWeapon(1.0));
            }
         }
      }
   }

   @Override
   protected void simulateFirstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
   }
}
