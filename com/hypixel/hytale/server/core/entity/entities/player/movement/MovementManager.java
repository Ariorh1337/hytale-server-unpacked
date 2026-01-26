/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.entities.player.movement;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.protocol.packets.player.UpdateMovementSettings;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementConfig;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

public class MovementManager
implements Component<EntityStore> {
    public static final BiFunction<PhysicsValues, GameMode, MovementSettings> MASTER_DEFAULT = (physicsValues, gameMode) -> new MovementSettings((PhysicsValues)physicsValues, (GameMode)((Object)gameMode)){
        final /* synthetic */ PhysicsValues val$physicsValues;
        final /* synthetic */ GameMode val$gameMode;
        {
            this.val$physicsValues = physicsValues;
            this.val$gameMode = gameMode;
            this.velocityResistance = 0.242f;
            this.mass = (float)this.val$physicsValues.getMass();
            this.dragCoefficient = (float)this.val$physicsValues.getDragCoefficient();
            this.invertedGravity = this.val$physicsValues.isInvertedGravity();
            this.jumpForce = 11.8f;
            this.swimJumpForce = 10.0f;
            this.jumpBufferDuration = 0.3f;
            this.jumpBufferMaxYVelocity = 3.0f;
            this.acceleration = 0.1f;
            this.airDragMin = 0.96f;
            this.airDragMax = 0.995f;
            this.airDragMinSpeed = 6.0f;
            this.airDragMaxSpeed = 10.0f;
            this.airFrictionMin = 0.02f;
            this.airFrictionMax = 0.045f;
            this.airFrictionMinSpeed = 6.0f;
            this.airFrictionMaxSpeed = 10.0f;
            this.airSpeedMultiplier = 1.0f;
            this.airControlMinSpeed = 0.0f;
            this.airControlMaxSpeed = 3.0f;
            this.airControlMinMultiplier = 0.0f;
            this.airControlMaxMultiplier = 3.13f;
            this.comboAirSpeedMultiplier = 1.05f;
            this.baseSpeed = 5.5f;
            this.horizontalFlySpeed = 10.32f;
            this.verticalFlySpeed = 10.32f;
            this.climbSpeed = 0.035f;
            this.climbSpeedLateral = 0.035f;
            this.climbUpSprintSpeed = 0.045f;
            this.climbDownSprintSpeed = 0.055f;
            this.wishDirectionGravityX = 0.5f;
            this.wishDirectionGravityY = 0.5f;
            this.wishDirectionWeightX = 0.5f;
            this.wishDirectionWeightY = 0.5f;
            this.maxSpeedMultiplier = 1000.0f;
            this.minSpeedMultiplier = 0.1f;
            this.canFly = this.val$gameMode == GameMode.Creative;
            this.collisionExpulsionForce = 0.04f;
            this.forwardWalkSpeedMultiplier = 0.3f;
            this.backwardWalkSpeedMultiplier = 0.3f;
            this.strafeWalkSpeedMultiplier = 0.3f;
            this.forwardRunSpeedMultiplier = 1.0f;
            this.backwardRunSpeedMultiplier = 0.65f;
            this.strafeRunSpeedMultiplier = 0.8f;
            this.forwardCrouchSpeedMultiplier = 0.55f;
            this.backwardCrouchSpeedMultiplier = 0.4f;
            this.strafeCrouchSpeedMultiplier = 0.45f;
            this.forwardSprintSpeedMultiplier = 1.65f;
            this.variableJumpFallForce = 35.0f;
            this.fallEffectDuration = 0.6f;
            this.fallJumpForce = 7.0f;
            this.fallMomentumLoss = 0.1f;
            this.autoJumpObstacleEffectDuration = 0.2f;
            this.autoJumpObstacleSpeedLoss = 0.95f;
            this.autoJumpObstacleSprintSpeedLoss = 0.75f;
            this.autoJumpObstacleSprintEffectDuration = 0.1f;
            this.autoJumpObstacleMaxAngle = 45.0f;
            this.autoJumpDisableJumping = true;
            this.minSlideEntrySpeed = 8.5f;
            this.slideExitSpeed = 2.5f;
            this.minFallSpeedToEngageRoll = 21.0f;
            this.maxFallSpeedToEngageRoll = 31.0f;
            this.rollStartSpeedModifier = 2.5f;
            this.rollExitSpeedModifier = 1.5f;
            this.rollTimeToComplete = 0.9f;
        }
    };
    protected MovementSettings defaultSettings;
    protected MovementSettings settings;

    public static ComponentType<EntityStore, MovementManager> getComponentType() {
        return EntityModule.get().getMovementManagerComponentType();
    }

    public MovementManager() {
    }

    public MovementManager(@Nonnull MovementManager other) {
        this();
        this.defaultSettings = new MovementSettings(other.defaultSettings);
        this.settings = new MovementSettings(other.settings);
    }

    public void resetDefaultsAndUpdate(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        this.refreshDefaultSettings(ref, componentAccessor);
        this.applyDefaultSettings();
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        this.update(playerRefComponent.getPacketHandler());
    }

    public void refreshDefaultSettings(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        World world = componentAccessor.getExternalData().getWorld();
        int movementConfigIndex = world.getGameplayConfig().getPlayerConfig().getMovementConfigIndex();
        MovementConfig movementConfig = MovementConfig.getAssetStore().getAssetMap().getAsset(movementConfigIndex);
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        this.setDefaultSettings(movementConfig.toPacket(), EntityUtils.getPhysicsValues(ref, componentAccessor), playerComponent.getGameMode());
    }

    public void applyDefaultSettings() {
        this.settings = new MovementSettings(this.defaultSettings);
    }

    public void update(@Nonnull PacketHandler playerPacketHandler) {
        playerPacketHandler.writeNoCache(new UpdateMovementSettings(this.getSettings()));
    }

    public MovementSettings getSettings() {
        return this.settings;
    }

    public void setDefaultSettings(MovementSettings settings, @Nonnull PhysicsValues physicsValues, GameMode gameMode) {
        this.defaultSettings = settings;
        this.defaultSettings.mass = (float)physicsValues.getMass();
        this.defaultSettings.dragCoefficient = (float)physicsValues.getDragCoefficient();
        this.defaultSettings.invertedGravity = physicsValues.isInvertedGravity();
        this.defaultSettings.canFly = gameMode == GameMode.Creative;
    }

    public MovementSettings getDefaultSettings() {
        return this.defaultSettings;
    }

    @Nonnull
    public String toString() {
        return "MovementManager{defaultSettings=" + String.valueOf(this.defaultSettings) + ", settings=" + String.valueOf(this.settings) + "}";
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        return new MovementManager(this);
    }
}

