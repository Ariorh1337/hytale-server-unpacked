/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.entities.player.movement;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class MovementConfig
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, MovementConfig>>,
NetworkSerializable<MovementSettings> {
    public static final AssetBuilderCodec<String, MovementConfig> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(MovementConfig.class, MovementConfig::new, Codec.STRING, (movementConfig, s) -> {
        movementConfig.id = s;
    }, movementConfig -> movementConfig.id, (movementConfig, data) -> {
        movementConfig.extraData = data;
    }, movementConfig -> movementConfig.extraData).appendInherited(new KeyedCodec<Float>("VelocityResistance", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.velocityResistance = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.velocityResistance), (movementConfig, parent) -> {
        movementConfig.velocityResistance = parent.velocityResistance;
    }).add()).appendInherited(new KeyedCodec<Float>("JumpForce", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.jumpForce = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.jumpForce), (movementConfig, parent) -> {
        movementConfig.jumpForce = parent.jumpForce;
    }).add()).appendInherited(new KeyedCodec<Float>("SwimJumpForce", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.swimJumpForce = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.swimJumpForce), (movementConfig, parent) -> {
        movementConfig.swimJumpForce = parent.swimJumpForce;
    }).add()).appendInherited(new KeyedCodec<Float>("JumpBufferDuration", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.jumpBufferDuration = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.jumpBufferDuration), (movementConfig, parent) -> {
        movementConfig.jumpBufferDuration = parent.jumpBufferDuration;
    }).add()).appendInherited(new KeyedCodec<Float>("JumpBufferMaxYVelocity", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.jumpBufferMaxYVelocity = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.jumpBufferMaxYVelocity), (movementConfig, parent) -> {
        movementConfig.jumpBufferMaxYVelocity = parent.jumpBufferMaxYVelocity;
    }).add()).appendInherited(new KeyedCodec<Float>("Acceleration", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.acceleration = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.acceleration), (movementConfig, parent) -> {
        movementConfig.acceleration = parent.acceleration;
    }).add()).appendInherited(new KeyedCodec<Float>("AirDragMin", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airDragMin = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airDragMin), (movementConfig, parent) -> {
        movementConfig.airDragMin = parent.airDragMin;
    }).add()).appendInherited(new KeyedCodec<Float>("AirDragMax", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airDragMax = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airDragMax), (movementConfig, parent) -> {
        movementConfig.airDragMax = parent.airDragMax;
    }).add()).appendInherited(new KeyedCodec<Float>("AirDragMinSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airDragMinSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airDragMinSpeed), (movementConfig, parent) -> {
        movementConfig.airDragMinSpeed = parent.airDragMinSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("AirDragMaxSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airDragMaxSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airDragMaxSpeed), (movementConfig, parent) -> {
        movementConfig.airDragMaxSpeed = parent.airDragMaxSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("AirFrictionMin", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airFrictionMin = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airFrictionMin), (movementConfig, parent) -> {
        movementConfig.airFrictionMin = parent.airFrictionMin;
    }).add()).appendInherited(new KeyedCodec<Float>("AirFrictionMax", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airFrictionMax = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airFrictionMax), (movementConfig, parent) -> {
        movementConfig.airFrictionMax = parent.airFrictionMax;
    }).add()).appendInherited(new KeyedCodec<Float>("AirFrictionMinSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airFrictionMinSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airFrictionMinSpeed), (movementConfig, parent) -> {
        movementConfig.airFrictionMinSpeed = parent.airFrictionMinSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("AirFrictionMaxSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airFrictionMaxSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airFrictionMaxSpeed), (movementConfig, parent) -> {
        movementConfig.airFrictionMaxSpeed = parent.airFrictionMaxSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("AirSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.airSpeedMultiplier = parent.airSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("AirControlMinSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airControlMinSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airControlMinSpeed), (movementConfig, parent) -> {
        movementConfig.airControlMinSpeed = parent.airControlMinSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("AirControlMaxSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airControlMaxSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airControlMaxSpeed), (movementConfig, parent) -> {
        movementConfig.airControlMaxSpeed = parent.airControlMaxSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("AirControlMinMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airControlMinMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airControlMinMultiplier), (movementConfig, parent) -> {
        movementConfig.airControlMinMultiplier = parent.airControlMinMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("AirControlMaxMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.airControlMaxMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.airControlMaxMultiplier), (movementConfig, parent) -> {
        movementConfig.airControlMaxMultiplier = parent.airControlMaxMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("ComboAirSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.comboAirSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.comboAirSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.comboAirSpeedMultiplier = parent.comboAirSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("BaseSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.baseSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.baseSpeed), (movementConfig, parent) -> {
        movementConfig.baseSpeed = parent.baseSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("ClimbSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.climbSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.climbSpeed), (movementConfig, parent) -> {
        movementConfig.climbSpeed = parent.climbSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("ClimbSpeedLateral", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.climbSpeedLateral = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.climbSpeedLateral), (movementConfig, parent) -> {
        movementConfig.climbSpeedLateral = parent.climbSpeedLateral;
    }).add()).appendInherited(new KeyedCodec<Float>("ClimbUpSprintSpeed", Codec.FLOAT), (movementConfig, aFloat) -> {
        movementConfig.climbUpSprintSpeed = aFloat.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.climbUpSprintSpeed), (movementConfig, parent) -> {
        movementConfig.climbUpSprintSpeed = parent.climbUpSprintSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("ClimbDownSprintSpeed", Codec.FLOAT), (movementConfig, aFloat) -> {
        movementConfig.climbDownSprintSpeed = aFloat.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.climbDownSprintSpeed), (movementConfig, parent) -> {
        movementConfig.climbDownSprintSpeed = parent.climbDownSprintSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("HorizontalFlySpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.horizontalFlySpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.horizontalFlySpeed), (movementConfig, parent) -> {
        movementConfig.horizontalFlySpeed = parent.horizontalFlySpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("VerticalFlySpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.verticalFlySpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.verticalFlySpeed), (movementConfig, parent) -> {
        movementConfig.verticalFlySpeed = parent.verticalFlySpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("MaxSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.maxSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.maxSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.maxSpeedMultiplier = parent.maxSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("MinSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.minSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.minSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.minSpeedMultiplier = parent.minSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("WishDirectionGravityX", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.wishDirectionGravityX = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.wishDirectionGravityX), (movementConfig, parent) -> {
        movementConfig.wishDirectionGravityX = parent.wishDirectionGravityX;
    }).add()).appendInherited(new KeyedCodec<Float>("WishDirectionGravityY", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.wishDirectionGravityY = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.wishDirectionGravityY), (movementConfig, parent) -> {
        movementConfig.wishDirectionGravityY = parent.wishDirectionGravityY;
    }).add()).appendInherited(new KeyedCodec<Float>("WishDirectionWeightX", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.wishDirectionWeightX = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.wishDirectionWeightX), (movementConfig, parent) -> {
        movementConfig.wishDirectionWeightX = parent.wishDirectionWeightX;
    }).add()).appendInherited(new KeyedCodec<Float>("WishDirectionWeightY", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.wishDirectionWeightY = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.wishDirectionWeightY), (movementConfig, parent) -> {
        movementConfig.wishDirectionWeightY = parent.wishDirectionWeightY;
    }).add()).appendInherited(new KeyedCodec<Float>("CollisionExpulsionForce", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.collisionExpulsionForce = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.collisionExpulsionForce), (movementConfig, parent) -> {
        movementConfig.collisionExpulsionForce = parent.collisionExpulsionForce;
    }).add()).appendInherited(new KeyedCodec<Float>("ForwardWalkSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.forwardWalkSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.forwardWalkSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.forwardWalkSpeedMultiplier = parent.forwardWalkSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("BackwardWalkSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.backwardWalkSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.backwardWalkSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.backwardWalkSpeedMultiplier = parent.backwardWalkSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("StrafeWalkSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.strafeWalkSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.strafeWalkSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.strafeWalkSpeedMultiplier = parent.strafeWalkSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("ForwardRunSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.forwardRunSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.forwardRunSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.forwardRunSpeedMultiplier = parent.forwardRunSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("BackwardRunSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.backwardRunSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.backwardRunSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.backwardRunSpeedMultiplier = parent.backwardRunSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("StrafeRunSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.strafeRunSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.strafeRunSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.strafeRunSpeedMultiplier = parent.strafeRunSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("ForwardCrouchSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.forwardCrouchSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.forwardCrouchSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.forwardCrouchSpeedMultiplier = parent.forwardCrouchSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("BackwardCrouchSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.backwardCrouchSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.backwardCrouchSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.backwardCrouchSpeedMultiplier = parent.backwardCrouchSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("StrafeCrouchSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.strafeCrouchSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.strafeCrouchSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.strafeCrouchSpeedMultiplier = parent.strafeCrouchSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("ForwardSprintSpeedMultiplier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.forwardSprintSpeedMultiplier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.forwardSprintSpeedMultiplier), (movementConfig, parent) -> {
        movementConfig.forwardSprintSpeedMultiplier = parent.forwardSprintSpeedMultiplier;
    }).add()).appendInherited(new KeyedCodec<Float>("VariableJumpFallForce", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.variableJumpFallForce = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.variableJumpFallForce), (movementConfig, parent) -> {
        movementConfig.variableJumpFallForce = parent.variableJumpFallForce;
    }).add()).appendInherited(new KeyedCodec<Float>("FallEffectDuration", Codec.FLOAT), (movementConfig, aFloat) -> {
        movementConfig.fallEffectDuration = aFloat.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.fallEffectDuration), (movementConfig, parent) -> {
        movementConfig.fallEffectDuration = parent.fallEffectDuration;
    }).add()).appendInherited(new KeyedCodec<Float>("FallJumpForce", Codec.FLOAT), (movementConfig, aFloat) -> {
        movementConfig.fallJumpForce = aFloat.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.fallJumpForce), (movementConfig, parent) -> {
        movementConfig.fallJumpForce = parent.fallJumpForce;
    }).add()).appendInherited(new KeyedCodec<Float>("FallMomentumLoss", Codec.FLOAT), (movementConfig, aFloat) -> {
        movementConfig.fallMomentumLoss = aFloat.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.fallMomentumLoss), (movementConfig, parent) -> {
        movementConfig.fallMomentumLoss = parent.fallMomentumLoss;
    }).add()).appendInherited(new KeyedCodec<Float>("AutoJumpObstacleSpeedLoss", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.autoJumpObstacleSpeedLoss = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.autoJumpObstacleSpeedLoss), (movementConfig, parent) -> {
        movementConfig.autoJumpObstacleSpeedLoss = parent.autoJumpObstacleSpeedLoss;
    }).add()).appendInherited(new KeyedCodec<Float>("AutoJumpObstacleSprintSpeedLoss", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.autoJumpObstacleSprintSpeedLoss = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.autoJumpObstacleSprintSpeedLoss), (movementConfig, parent) -> {
        movementConfig.autoJumpObstacleSprintSpeedLoss = parent.autoJumpObstacleSprintSpeedLoss;
    }).add()).appendInherited(new KeyedCodec<Float>("AutoJumpObstacleEffectDuration", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.autoJumpObstacleEffectDuration = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.autoJumpObstacleEffectDuration), (movementConfig, parent) -> {
        movementConfig.autoJumpObstacleEffectDuration = parent.autoJumpObstacleEffectDuration;
    }).add()).appendInherited(new KeyedCodec<Float>("AutoJumpObstacleSprintEffectDuration", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.autoJumpObstacleSprintEffectDuration = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.autoJumpObstacleSprintEffectDuration), (movementConfig, parent) -> {
        movementConfig.autoJumpObstacleSprintEffectDuration = parent.autoJumpObstacleSprintEffectDuration;
    }).add()).appendInherited(new KeyedCodec<Float>("AutoJumpObstacleMaxAngle", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.autoJumpObstacleMaxAngle = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.autoJumpObstacleMaxAngle), (movementConfig, parent) -> {
        movementConfig.autoJumpObstacleMaxAngle = parent.autoJumpObstacleMaxAngle;
    }).add()).appendInherited(new KeyedCodec<Boolean>("AutoJumpDisableJumping", Codec.BOOLEAN), (movementConfig, tasks) -> {
        movementConfig.autoJumpDisableJumping = tasks;
    }, movementConfig -> movementConfig.autoJumpDisableJumping, (movementConfig, parent) -> {
        movementConfig.autoJumpDisableJumping = parent.autoJumpDisableJumping;
    }).add()).appendInherited(new KeyedCodec<Float>("MinSlideEntrySpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.minSlideEntrySpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.minSlideEntrySpeed), (movementConfig, parent) -> {
        movementConfig.minSlideEntrySpeed = parent.minSlideEntrySpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("SlideExitSpeed", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.slideExitSpeed = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.slideExitSpeed), (movementConfig, parent) -> {
        movementConfig.slideExitSpeed = parent.slideExitSpeed;
    }).add()).appendInherited(new KeyedCodec<Float>("MinFallSpeedToEngageRoll", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.minFallSpeedToEngageRoll = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.minFallSpeedToEngageRoll), (movementConfig, parent) -> {
        movementConfig.minFallSpeedToEngageRoll = parent.minFallSpeedToEngageRoll;
    }).add()).appendInherited(new KeyedCodec<Float>("MaxFallSpeedToEngageRoll", Codec.FLOAT), (movementConfig, value) -> {
        movementConfig.maxFallSpeedToEngageRoll = value.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.maxFallSpeedToEngageRoll), (movementConfig, parent) -> {
        movementConfig.maxFallSpeedToEngageRoll = parent.maxFallSpeedToEngageRoll;
    }).add()).appendInherited(new KeyedCodec<Float>("FallDamagePartialMitigationPercent", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.fallDamagePartialMitigationPercent = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.fallDamagePartialMitigationPercent), (movementConfig, parent) -> {
        movementConfig.fallDamagePartialMitigationPercent = parent.fallDamagePartialMitigationPercent;
    }).add()).appendInherited(new KeyedCodec<Float>("MaxFallSpeedRollFullMitigation", Codec.FLOAT), (movementConfig, value) -> {
        movementConfig.maxFallSpeedRollFullMitigation = value.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.maxFallSpeedRollFullMitigation), (movementConfig, parent) -> {
        movementConfig.maxFallSpeedRollFullMitigation = parent.maxFallSpeedRollFullMitigation;
    }).add()).appendInherited(new KeyedCodec<Float>("RollStartSpeedModifier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.rollStartSpeedModifier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.rollStartSpeedModifier), (movementConfig, parent) -> {
        movementConfig.rollStartSpeedModifier = parent.rollStartSpeedModifier;
    }).add()).appendInherited(new KeyedCodec<Float>("RollExitSpeedModifier", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.rollExitSpeedModifier = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.rollExitSpeedModifier), (movementConfig, parent) -> {
        movementConfig.rollExitSpeedModifier = parent.rollExitSpeedModifier;
    }).add()).appendInherited(new KeyedCodec<Float>("RollTimeToComplete", Codec.FLOAT), (movementConfig, tasks) -> {
        movementConfig.rollTimeToComplete = tasks.floatValue();
    }, movementConfig -> Float.valueOf(movementConfig.rollTimeToComplete), (movementConfig, parent) -> {
        movementConfig.rollTimeToComplete = parent.rollTimeToComplete;
    }).add()).build();
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(MovementConfig::getAssetStore));
    private static AssetStore<String, MovementConfig, IndexedLookupTableAssetMap<String, MovementConfig>> ASSET_STORE;
    public static final int DEFAULT_INDEX = 0;
    public static final String DEFAULT_ID = "BuiltinDefault";
    public static final MovementConfig DEFAULT_MOVEMENT;
    protected AssetExtraInfo.Data extraData;
    protected String id;
    protected float velocityResistance;
    protected float jumpForce;
    protected float swimJumpForce;
    protected float jumpBufferDuration;
    protected float jumpBufferMaxYVelocity;
    protected float acceleration;
    protected float airDragMin;
    protected float airDragMax;
    protected float airDragMinSpeed;
    protected float airDragMaxSpeed;
    protected float airFrictionMin;
    protected float airFrictionMax;
    protected float airFrictionMinSpeed;
    protected float airFrictionMaxSpeed;
    protected float airSpeedMultiplier;
    protected float airControlMinSpeed;
    protected float airControlMaxSpeed;
    protected float airControlMinMultiplier;
    protected float airControlMaxMultiplier;
    protected float comboAirSpeedMultiplier;
    protected float baseSpeed;
    protected float climbSpeed;
    protected float climbSpeedLateral;
    protected float climbUpSprintSpeed;
    protected float climbDownSprintSpeed;
    protected float horizontalFlySpeed;
    protected float verticalFlySpeed;
    protected float maxSpeedMultiplier;
    protected float minSpeedMultiplier;
    protected float wishDirectionGravityX;
    protected float wishDirectionGravityY;
    protected float wishDirectionWeightX;
    protected float wishDirectionWeightY;
    protected float collisionExpulsionForce;
    protected float forwardWalkSpeedMultiplier;
    protected float backwardWalkSpeedMultiplier;
    protected float strafeWalkSpeedMultiplier;
    protected float forwardRunSpeedMultiplier;
    protected float backwardRunSpeedMultiplier;
    protected float strafeRunSpeedMultiplier;
    protected float forwardCrouchSpeedMultiplier;
    protected float backwardCrouchSpeedMultiplier;
    protected float strafeCrouchSpeedMultiplier;
    protected float forwardSprintSpeedMultiplier;
    protected float variableJumpFallForce;
    protected float fallEffectDuration;
    protected float fallJumpForce;
    protected float fallMomentumLoss;
    protected float autoJumpObstacleSpeedLoss;
    protected float autoJumpObstacleSprintSpeedLoss;
    protected float autoJumpObstacleEffectDuration;
    protected float autoJumpObstacleSprintEffectDuration;
    protected float autoJumpObstacleMaxAngle;
    protected boolean autoJumpDisableJumping;
    protected float minSlideEntrySpeed;
    protected float slideExitSpeed;
    protected float minFallSpeedToEngageRoll;
    protected float maxFallSpeedToEngageRoll;
    protected float fallDamagePartialMitigationPercent;
    protected float maxFallSpeedRollFullMitigation;
    protected float rollStartSpeedModifier;
    protected float rollExitSpeedModifier;
    protected float rollTimeToComplete;

    public static AssetStore<String, MovementConfig, IndexedLookupTableAssetMap<String, MovementConfig>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(MovementConfig.class);
        }
        return ASSET_STORE;
    }

    public static IndexedLookupTableAssetMap<String, MovementConfig> getAssetMap() {
        return MovementConfig.getAssetStore().getAssetMap();
    }

    public MovementConfig(@Nonnull MovementConfig movementConfig) {
        this.id = movementConfig.id;
        this.velocityResistance = movementConfig.velocityResistance;
        this.jumpForce = movementConfig.jumpForce;
        this.swimJumpForce = movementConfig.swimJumpForce;
        this.jumpBufferDuration = movementConfig.jumpBufferDuration;
        this.jumpBufferMaxYVelocity = movementConfig.jumpBufferMaxYVelocity;
        this.acceleration = movementConfig.acceleration;
        this.airDragMin = movementConfig.airDragMin;
        this.airDragMax = movementConfig.airDragMax;
        this.airDragMinSpeed = movementConfig.airDragMinSpeed;
        this.airDragMaxSpeed = movementConfig.airDragMaxSpeed;
        this.airFrictionMin = movementConfig.airFrictionMin;
        this.airFrictionMax = movementConfig.airFrictionMax;
        this.airFrictionMinSpeed = movementConfig.airFrictionMinSpeed;
        this.airFrictionMaxSpeed = movementConfig.airFrictionMaxSpeed;
        this.airSpeedMultiplier = movementConfig.airSpeedMultiplier;
        this.airControlMinSpeed = movementConfig.airControlMinSpeed;
        this.airControlMaxSpeed = movementConfig.airControlMaxSpeed;
        this.airControlMinMultiplier = movementConfig.airControlMinMultiplier;
        this.airControlMaxMultiplier = movementConfig.airControlMaxMultiplier;
        this.comboAirSpeedMultiplier = movementConfig.airSpeedMultiplier;
        this.baseSpeed = movementConfig.baseSpeed;
        this.climbSpeed = movementConfig.climbSpeed;
        this.climbSpeedLateral = movementConfig.climbSpeedLateral;
        this.climbUpSprintSpeed = movementConfig.climbUpSprintSpeed;
        this.climbDownSprintSpeed = movementConfig.climbDownSprintSpeed;
        this.horizontalFlySpeed = movementConfig.horizontalFlySpeed;
        this.verticalFlySpeed = movementConfig.verticalFlySpeed;
        this.maxSpeedMultiplier = movementConfig.maxSpeedMultiplier;
        this.minSpeedMultiplier = movementConfig.minSpeedMultiplier;
        this.wishDirectionGravityX = movementConfig.wishDirectionGravityX;
        this.wishDirectionGravityY = movementConfig.wishDirectionGravityY;
        this.wishDirectionWeightX = movementConfig.wishDirectionWeightX;
        this.wishDirectionWeightY = movementConfig.wishDirectionWeightY;
        this.collisionExpulsionForce = movementConfig.collisionExpulsionForce;
        this.forwardWalkSpeedMultiplier = movementConfig.forwardWalkSpeedMultiplier;
        this.backwardWalkSpeedMultiplier = movementConfig.backwardWalkSpeedMultiplier;
        this.strafeWalkSpeedMultiplier = movementConfig.strafeWalkSpeedMultiplier;
        this.forwardRunSpeedMultiplier = movementConfig.forwardRunSpeedMultiplier;
        this.backwardRunSpeedMultiplier = movementConfig.backwardRunSpeedMultiplier;
        this.strafeRunSpeedMultiplier = movementConfig.strafeRunSpeedMultiplier;
        this.forwardCrouchSpeedMultiplier = movementConfig.forwardCrouchSpeedMultiplier;
        this.backwardCrouchSpeedMultiplier = movementConfig.backwardCrouchSpeedMultiplier;
        this.strafeCrouchSpeedMultiplier = movementConfig.strafeCrouchSpeedMultiplier;
        this.forwardSprintSpeedMultiplier = movementConfig.forwardSprintSpeedMultiplier;
        this.variableJumpFallForce = movementConfig.variableJumpFallForce;
        this.autoJumpObstacleSpeedLoss = movementConfig.autoJumpObstacleSpeedLoss;
        this.autoJumpObstacleSprintSpeedLoss = movementConfig.autoJumpObstacleSprintSpeedLoss;
        this.autoJumpObstacleEffectDuration = movementConfig.autoJumpObstacleEffectDuration;
        this.autoJumpObstacleSprintEffectDuration = movementConfig.autoJumpObstacleSprintEffectDuration;
        this.autoJumpObstacleMaxAngle = movementConfig.autoJumpObstacleMaxAngle;
        this.autoJumpDisableJumping = movementConfig.autoJumpDisableJumping;
        this.minSlideEntrySpeed = movementConfig.minSlideEntrySpeed;
        this.slideExitSpeed = movementConfig.slideExitSpeed;
        this.minFallSpeedToEngageRoll = movementConfig.minFallSpeedToEngageRoll;
        this.maxFallSpeedToEngageRoll = movementConfig.maxFallSpeedToEngageRoll;
        this.fallDamagePartialMitigationPercent = movementConfig.fallDamagePartialMitigationPercent;
        this.maxFallSpeedRollFullMitigation = movementConfig.maxFallSpeedRollFullMitigation;
        this.rollStartSpeedModifier = movementConfig.rollStartSpeedModifier;
        this.rollExitSpeedModifier = movementConfig.rollExitSpeedModifier;
        this.rollTimeToComplete = movementConfig.rollTimeToComplete;
    }

    public MovementConfig(String id) {
        this.id = id;
    }

    protected MovementConfig() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    public AssetExtraInfo.Data getExtraData() {
        return this.extraData;
    }

    public float getVelocityResistance() {
        return this.velocityResistance;
    }

    public float getJumpForce() {
        return this.jumpForce;
    }

    public float getSwimJumpForce() {
        return this.swimJumpForce;
    }

    public float getJumpBufferDuration() {
        return this.jumpBufferDuration;
    }

    public float getJumpBufferMaxYVelocity() {
        return this.jumpBufferMaxYVelocity;
    }

    public float getAcceleration() {
        return this.acceleration;
    }

    public float getAirDragMin() {
        return this.airDragMin;
    }

    public float getAirDragMax() {
        return this.airDragMax;
    }

    public float getAirDragMinSpeed() {
        return this.airDragMinSpeed;
    }

    public float getAirDragMaxSpeed() {
        return this.airDragMaxSpeed;
    }

    public float getAirFrictionMin() {
        return this.airFrictionMin;
    }

    public float getAirFrictionMax() {
        return this.airFrictionMax;
    }

    public float getAirFrictionMinSpeed() {
        return this.airFrictionMinSpeed;
    }

    public float getAirFrictionMaxSpeed() {
        return this.airFrictionMaxSpeed;
    }

    public float getAirSpeedMultiplier() {
        return this.airSpeedMultiplier;
    }

    public float getAirControlMinSpeed() {
        return this.airControlMinSpeed;
    }

    public float getAirControlMaxSpeed() {
        return this.airControlMaxSpeed;
    }

    public float getAirControlMinMultiplier() {
        return this.airControlMinMultiplier;
    }

    public float getAirControlMaxMultiplier() {
        return this.airControlMaxMultiplier;
    }

    public float getComboAirSpeedMultiplier() {
        return this.comboAirSpeedMultiplier;
    }

    public float getBaseSpeed() {
        return this.baseSpeed;
    }

    public float getClimbSpeed() {
        return this.climbSpeed;
    }

    public float getClimbSpeedLateral() {
        return this.climbSpeedLateral;
    }

    public float getClimbUpSprintSpeed() {
        return this.climbUpSprintSpeed;
    }

    public float getClimbDownSprintSpeed() {
        return this.climbDownSprintSpeed;
    }

    public float getHorizontalFlySpeed() {
        return this.horizontalFlySpeed;
    }

    public float getVerticalFlySpeed() {
        return this.verticalFlySpeed;
    }

    public float getMaxSpeedMultiplier() {
        return this.maxSpeedMultiplier;
    }

    public float getMinSpeedMultiplier() {
        return this.minSpeedMultiplier;
    }

    public float getWishDirectionGravityX() {
        return this.wishDirectionGravityX;
    }

    public float getWishDirectionGravityY() {
        return this.wishDirectionGravityY;
    }

    public float getWishDirectionWeightX() {
        return this.wishDirectionWeightX;
    }

    public float getWishDirectionWeightY() {
        return this.wishDirectionWeightY;
    }

    public float getCollisionExpulsionForce() {
        return this.collisionExpulsionForce;
    }

    public float getForwardWalkSpeedMultiplier() {
        return this.forwardWalkSpeedMultiplier;
    }

    public float getBackwardWalkSpeedMultiplier() {
        return this.backwardWalkSpeedMultiplier;
    }

    public float getStrafeWalkSpeedMultiplier() {
        return this.strafeWalkSpeedMultiplier;
    }

    public float getForwardRunSpeedMultiplier() {
        return this.forwardRunSpeedMultiplier;
    }

    public float getBackwardRunSpeedMultiplier() {
        return this.backwardRunSpeedMultiplier;
    }

    public float getStrafeRunSpeedMultiplier() {
        return this.strafeRunSpeedMultiplier;
    }

    public float getForwardCrouchSpeedMultiplier() {
        return this.forwardCrouchSpeedMultiplier;
    }

    public float getBackwardCrouchSpeedMultiplier() {
        return this.backwardCrouchSpeedMultiplier;
    }

    public float getStrafeCrouchSpeedMultiplier() {
        return this.strafeCrouchSpeedMultiplier;
    }

    public float getForwardSprintSpeedMultiplier() {
        return this.forwardSprintSpeedMultiplier;
    }

    public float getVariableJumpFallForce() {
        return this.variableJumpFallForce;
    }

    public float getFallEffectDuration() {
        return this.fallEffectDuration;
    }

    public float getFallJumpForce() {
        return this.fallJumpForce;
    }

    public float getFallMomentumLoss() {
        return this.fallMomentumLoss;
    }

    public float getAutoJumpObstacleSpeedLoss() {
        return this.autoJumpObstacleSpeedLoss;
    }

    public float getAutoJumpObstacleSprintSpeedLoss() {
        return this.autoJumpObstacleSprintSpeedLoss;
    }

    public float getAutoJumpObstacleEffectDuration() {
        return this.autoJumpObstacleEffectDuration;
    }

    public float getAutoJumpObstacleSprintEffectDuration() {
        return this.autoJumpObstacleSprintEffectDuration;
    }

    public float getAutoJumpObstacleMaxAngle() {
        return this.autoJumpObstacleMaxAngle;
    }

    public boolean isAutoJumpDisableJumping() {
        return this.autoJumpDisableJumping;
    }

    public float getMinFallSpeedToEngageRoll() {
        return this.minFallSpeedToEngageRoll;
    }

    public float getMaxFallSpeedToEngageRoll() {
        return this.maxFallSpeedToEngageRoll;
    }

    public float getFallDamagePartialMitigationPercent() {
        return this.fallDamagePartialMitigationPercent;
    }

    public float getMaxFallSpeedRollFullMitigation() {
        return this.maxFallSpeedRollFullMitigation;
    }

    public float getRollStartSpeedModifier() {
        return this.rollStartSpeedModifier;
    }

    public float getRollExitSpeedModifier() {
        return this.rollExitSpeedModifier;
    }

    public float getRollTimeToComplete() {
        return this.rollTimeToComplete;
    }

    @Override
    @Nonnull
    public MovementSettings toPacket() {
        MovementSettings packet = new MovementSettings();
        packet.velocityResistance = this.velocityResistance;
        packet.jumpForce = this.jumpForce;
        packet.swimJumpForce = this.swimJumpForce;
        packet.jumpBufferDuration = this.jumpBufferDuration;
        packet.jumpBufferMaxYVelocity = this.jumpBufferMaxYVelocity;
        packet.acceleration = this.acceleration;
        packet.airDragMin = this.airDragMin;
        packet.airDragMax = this.airDragMax;
        packet.airDragMinSpeed = this.airDragMinSpeed;
        packet.airDragMaxSpeed = this.airDragMaxSpeed;
        packet.airFrictionMin = this.airFrictionMin;
        packet.airFrictionMax = this.airFrictionMax;
        packet.airFrictionMinSpeed = this.airFrictionMinSpeed;
        packet.airFrictionMaxSpeed = this.airFrictionMaxSpeed;
        packet.airSpeedMultiplier = this.airSpeedMultiplier;
        packet.airControlMinSpeed = this.airControlMinSpeed;
        packet.airControlMaxSpeed = this.airControlMaxSpeed;
        packet.airControlMinMultiplier = this.airControlMinMultiplier;
        packet.airControlMaxMultiplier = this.airControlMaxMultiplier;
        packet.comboAirSpeedMultiplier = this.airSpeedMultiplier;
        packet.baseSpeed = this.baseSpeed;
        packet.climbSpeed = this.climbSpeed;
        packet.climbSpeedLateral = this.climbSpeedLateral;
        packet.climbUpSprintSpeed = this.climbUpSprintSpeed;
        packet.climbDownSprintSpeed = this.climbDownSprintSpeed;
        packet.horizontalFlySpeed = this.horizontalFlySpeed;
        packet.verticalFlySpeed = this.verticalFlySpeed;
        packet.maxSpeedMultiplier = this.maxSpeedMultiplier;
        packet.minSpeedMultiplier = this.minSpeedMultiplier;
        packet.wishDirectionGravityX = this.wishDirectionGravityX;
        packet.wishDirectionGravityY = this.wishDirectionGravityY;
        packet.wishDirectionWeightX = this.wishDirectionWeightX;
        packet.wishDirectionWeightY = this.wishDirectionWeightY;
        packet.collisionExpulsionForce = this.collisionExpulsionForce;
        packet.forwardWalkSpeedMultiplier = this.forwardWalkSpeedMultiplier;
        packet.backwardWalkSpeedMultiplier = this.backwardWalkSpeedMultiplier;
        packet.strafeWalkSpeedMultiplier = this.strafeWalkSpeedMultiplier;
        packet.forwardRunSpeedMultiplier = this.forwardRunSpeedMultiplier;
        packet.backwardRunSpeedMultiplier = this.backwardRunSpeedMultiplier;
        packet.strafeRunSpeedMultiplier = this.strafeRunSpeedMultiplier;
        packet.forwardCrouchSpeedMultiplier = this.forwardCrouchSpeedMultiplier;
        packet.backwardCrouchSpeedMultiplier = this.backwardCrouchSpeedMultiplier;
        packet.strafeCrouchSpeedMultiplier = this.strafeCrouchSpeedMultiplier;
        packet.forwardSprintSpeedMultiplier = this.forwardSprintSpeedMultiplier;
        packet.variableJumpFallForce = this.variableJumpFallForce;
        packet.fallEffectDuration = this.fallEffectDuration;
        packet.fallJumpForce = this.fallJumpForce;
        packet.fallMomentumLoss = this.fallMomentumLoss;
        packet.autoJumpObstacleSpeedLoss = this.autoJumpObstacleSpeedLoss;
        packet.autoJumpObstacleSprintSpeedLoss = this.autoJumpObstacleSprintSpeedLoss;
        packet.autoJumpObstacleEffectDuration = this.autoJumpObstacleEffectDuration;
        packet.autoJumpObstacleSprintEffectDuration = this.autoJumpObstacleSprintEffectDuration;
        packet.autoJumpObstacleMaxAngle = this.autoJumpObstacleMaxAngle;
        packet.autoJumpDisableJumping = this.autoJumpDisableJumping;
        packet.minSlideEntrySpeed = this.minSlideEntrySpeed;
        packet.slideExitSpeed = this.slideExitSpeed;
        packet.minFallSpeedToEngageRoll = this.minFallSpeedToEngageRoll;
        packet.maxFallSpeedToEngageRoll = this.maxFallSpeedToEngageRoll;
        packet.rollStartSpeedModifier = this.rollStartSpeedModifier;
        packet.rollExitSpeedModifier = this.rollExitSpeedModifier;
        packet.rollTimeToComplete = this.rollTimeToComplete;
        return packet;
    }

    @Nonnull
    public String toString() {
        return "MovementConfig{id='" + this.id + "', velocityResistance=" + this.velocityResistance + ", jumpForce=" + this.jumpForce + ", swimJumpForce=" + this.swimJumpForce + ", jumpBufferDuration=" + this.jumpBufferDuration + ", jumpBufferMaxYVelocity=" + this.jumpBufferMaxYVelocity + ", acceleration=" + this.acceleration + ", airDragMin=" + this.airDragMin + ", airDragMax=" + this.airDragMax + ", airDragMinSpeed=" + this.airDragMinSpeed + ", airDragMaxSpeed=" + this.airDragMaxSpeed + ", airFrictionMin=" + this.airFrictionMin + ", airFrictionMax=" + this.airFrictionMax + ", airFrictionMinSpeed=" + this.airFrictionMinSpeed + ", airFrictionMaxSpeed=" + this.airFrictionMaxSpeed + ", airSpeedMultiplier=" + this.airSpeedMultiplier + ", airControlMinSpeed=" + this.airControlMinSpeed + ", airControlMaxSpeed=" + this.airControlMaxSpeed + ", airControlMinMultiplier=" + this.airControlMinMultiplier + ", airControlMaxMultiplier=" + this.airControlMaxMultiplier + ", comboAirSpeedMultiplier=" + this.comboAirSpeedMultiplier + ", baseSpeed=" + this.baseSpeed + ", climbSpeed=" + this.climbSpeed + ", climbSpeedLateral=" + this.climbSpeedLateral + ", climbUpSprintSpeed=" + this.climbUpSprintSpeed + ", climbDownSprintSpeed=" + this.climbDownSprintSpeed + ", horizontalFlySpeed=" + this.horizontalFlySpeed + ", verticalFlySpeed=" + this.verticalFlySpeed + ", maxSpeedMultiplier=" + this.maxSpeedMultiplier + ", minSpeedMultiplier=" + this.minSpeedMultiplier + ", wishDirectionGravityX=" + this.wishDirectionGravityX + ", wishDirectionGravityY=" + this.wishDirectionGravityY + ", wishDirectionWeightX=" + this.wishDirectionWeightX + ", wishDirectionWeightY=" + this.wishDirectionWeightY + ", collisionExpulsionForce=" + this.collisionExpulsionForce + ", forwardWalkSpeedMultiplier=" + this.forwardWalkSpeedMultiplier + ", backwardWalkSpeedMultiplier=" + this.backwardWalkSpeedMultiplier + ", strafeWalkSpeedMultiplier=" + this.strafeWalkSpeedMultiplier + ", forwardRunSpeedMultiplier=" + this.forwardRunSpeedMultiplier + ", backwardRunSpeedMultiplier=" + this.backwardRunSpeedMultiplier + ", strafeRunSpeedMultiplier=" + this.strafeRunSpeedMultiplier + ", forwardCrouchSpeedMultiplier=" + this.forwardCrouchSpeedMultiplier + ", backwardCrouchSpeedMultiplier=" + this.backwardCrouchSpeedMultiplier + ", strafeCrouchSpeedMultiplier=" + this.strafeCrouchSpeedMultiplier + ", forwardSprintSpeedMultiplier=" + this.forwardSprintSpeedMultiplier + ", variableJumpFallForce=" + this.variableJumpFallForce + ", fallEffectDuration=" + this.fallEffectDuration + ", fallJumpForce=" + this.fallJumpForce + ", fallMomentumLoss=" + this.fallMomentumLoss + ", autoJumpObstacleSpeedLoss=" + this.autoJumpObstacleSpeedLoss + ", autoJumpObstacleSprintSpeedLoss=" + this.autoJumpObstacleSprintSpeedLoss + ", autoJumpObstacleEffectDuration=" + this.autoJumpObstacleEffectDuration + ", autoJumpObstacleSprintEffectDuration=" + this.autoJumpObstacleSprintEffectDuration + ", autoJumpObstacleMaxAngle=" + this.autoJumpObstacleMaxAngle + ", autoJumpDisableJumping=" + this.autoJumpDisableJumping + ", minSlideEntrySpeed=" + this.minSlideEntrySpeed + ", slideExitSpeed=" + this.slideExitSpeed + ", minFallSpeedToEngageRoll=" + this.minFallSpeedToEngageRoll + ", maxFallSpeedToEngageRoll=" + this.maxFallSpeedToEngageRoll + ", fallDamagePartialMitigationPercent=" + this.fallDamagePartialMitigationPercent + ", maxFallSpeedRollFullMitigation=" + this.maxFallSpeedRollFullMitigation + ", rollStartSpeedModifier=" + this.rollStartSpeedModifier + ", rollExitSpeedModifier=" + this.rollExitSpeedModifier + ", rollTimeToComplete=" + this.rollTimeToComplete + "}";
    }

    static {
        DEFAULT_MOVEMENT = new MovementConfig(DEFAULT_ID){
            {
                this.velocityResistance = 0.242f;
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
                this.climbSpeed = 0.035f;
                this.climbSpeedLateral = 0.035f;
                this.climbUpSprintSpeed = 0.5f;
                this.climbDownSprintSpeed = 0.6f;
                this.horizontalFlySpeed = 10.32f;
                this.verticalFlySpeed = 10.32f;
                this.maxSpeedMultiplier = 1000.0f;
                this.minSpeedMultiplier = 0.01f;
                this.wishDirectionGravityX = 0.5f;
                this.wishDirectionGravityY = 0.5f;
                this.wishDirectionWeightX = 0.5f;
                this.wishDirectionWeightY = 0.5f;
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
                this.autoJumpObstacleSpeedLoss = 0.95f;
                this.autoJumpObstacleSprintSpeedLoss = 0.75f;
                this.autoJumpObstacleEffectDuration = 0.2f;
                this.autoJumpObstacleSprintEffectDuration = 0.1f;
                this.autoJumpObstacleMaxAngle = 45.0f;
                this.autoJumpDisableJumping = true;
                this.minSlideEntrySpeed = 8.5f;
                this.slideExitSpeed = 2.5f;
                this.minFallSpeedToEngageRoll = 21.0f;
                this.maxFallSpeedToEngageRoll = 31.0f;
                this.fallDamagePartialMitigationPercent = 33.0f;
                this.maxFallSpeedRollFullMitigation = 25.0f;
                this.rollStartSpeedModifier = 2.5f;
                this.rollExitSpeedModifier = 1.5f;
                this.rollTimeToComplete = 0.9f;
            }
        };
    }
}

