/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.gameplay;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.HomeOrSpawnPoint;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.RespawnController;
import javax.annotation.Nonnull;

public class DeathConfig {
    public static final EnumCodec<ItemsLossMode> LOSS_MODE_CODEC = new EnumCodec<ItemsLossMode>(ItemsLossMode.class);
    @Nonnull
    public static final BuilderCodec<DeathConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DeathConfig.class, DeathConfig::new).appendInherited(new KeyedCodec<RespawnController>("RespawnController", RespawnController.CODEC), (o, i) -> {
        o.respawnController = i;
    }, o -> o.respawnController, (o, p) -> {
        o.respawnController = p.respawnController;
    }).addValidator(Validators.nonNull()).documentation("The respawn controller that determines where the player respawns.").add()).appendInherited(new KeyedCodec<ItemsLossMode>("ItemsLossMode", LOSS_MODE_CODEC), (deathConfig, o) -> {
        deathConfig.itemsLossMode = o;
    }, deathConfig -> deathConfig.itemsLossMode, (o, p) -> {
        o.itemsLossMode = p.itemsLossMode;
    }).documentation("The mode used to compute what the entity will lose upon death.").add()).appendInherited(new KeyedCodec<Double>("ItemsAmountLossPercentage", Codec.DOUBLE), (deathConfig, aDouble) -> {
        deathConfig.itemsAmountLossPercentage = aDouble;
    }, deathConfig -> deathConfig.itemsAmountLossPercentage, (deathConfig, parent) -> {
        deathConfig.itemsAmountLossPercentage = parent.itemsAmountLossPercentage;
    }).addValidator(Validators.range(0.0, 100.0)).documentation("The amount (in %) of items lost for an ItemStack upon death (applied to the entire inventory). Used ONLY if `ItemsLossMode` is set to 'Configured` and applied to items that have `DropOnDeath` set to `true`.").add()).appendInherited(new KeyedCodec<Double>("ItemsDurabilityLossPercentage", Codec.DOUBLE), (deathConfig, aDouble) -> {
        deathConfig.itemsDurabilityLossPercentage = aDouble;
    }, deathConfig -> deathConfig.itemsDurabilityLossPercentage, (deathConfig, parent) -> {
        deathConfig.itemsDurabilityLossPercentage = parent.itemsDurabilityLossPercentage;
    }).addValidator(Validators.range(0.0, 100.0)).documentation("The amount of durability (in %) items lose upon death (applied to the entire inventory).").add()).build();
    @Nonnull
    protected RespawnController respawnController = HomeOrSpawnPoint.INSTANCE;
    protected ItemsLossMode itemsLossMode = ItemsLossMode.NONE;
    protected double itemsAmountLossPercentage = 10.0;
    protected double itemsDurabilityLossPercentage = 10.0;

    @Nonnull
    public RespawnController getRespawnController() {
        return this.respawnController;
    }

    public ItemsLossMode getItemsLossMode() {
        return this.itemsLossMode;
    }

    public double getItemsAmountLossPercentage() {
        return this.itemsAmountLossPercentage;
    }

    public double getItemsDurabilityLossPercentage() {
        return this.itemsDurabilityLossPercentage;
    }

    public static enum ItemsLossMode {
        NONE,
        ALL,
        CONFIGURED;

    }
}

