/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemTool;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.Knockback;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExplosionConfig {
    @Nonnull
    public static final BuilderCodec<ExplosionConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ExplosionConfig.class, ExplosionConfig::new).appendInherited(new KeyedCodec<Boolean>("DamageEntities", Codec.BOOLEAN), (explosionConfig, b) -> {
        explosionConfig.damageEntities = b;
    }, explosionConfig -> explosionConfig.damageEntities, (explosionConfig, parent) -> {
        explosionConfig.damageEntities = parent.damageEntities;
    }).documentation("Determines whether the explosion should damage entities.").add()).appendInherited(new KeyedCodec<Boolean>("DamageBlocks", Codec.BOOLEAN), (explosionConfig, b) -> {
        explosionConfig.damageBlocks = b;
    }, explosionConfig -> explosionConfig.damageBlocks, (explosionConfig, parent) -> {
        explosionConfig.damageBlocks = parent.damageBlocks;
    }).documentation("Determines whether the explosion should damage blocks.").add()).appendInherited(new KeyedCodec<Integer>("BlockDamageRadius", Codec.INTEGER), (explosionConfig, i) -> {
        explosionConfig.blockDamageRadius = i;
    }, explosionConfig -> explosionConfig.blockDamageRadius, (explosionConfig, parent) -> {
        explosionConfig.blockDamageRadius = parent.blockDamageRadius;
    }).documentation("The radius in which blocks should be damaged by the explosion.").add()).appendInherited(new KeyedCodec<Float>("BlockDamageFalloff", Codec.FLOAT), (explosionConfig, f) -> {
        explosionConfig.blockDamageFalloff = f.floatValue();
    }, explosionConfig -> Float.valueOf(explosionConfig.entityDamageFalloff), (explosionConfig, parent) -> {
        explosionConfig.entityDamageFalloff = parent.entityDamageFalloff;
    }).documentation("The falloff applied to the block damage.").add()).appendInherited(new KeyedCodec<Float>("BlockDropChance", Codec.FLOAT), (explosionConfig, f) -> {
        explosionConfig.blockDropChance = f.floatValue();
    }, explosionConfig -> Float.valueOf(explosionConfig.blockDropChance), (explosionConfig, parent) -> {
        explosionConfig.blockDropChance = parent.blockDropChance;
    }).documentation("The chance in which a block drops its loot after breaking.").add()).appendInherited(new KeyedCodec<Float>("EntityDamageRadius", Codec.FLOAT), (explosionConfig, f) -> {
        explosionConfig.entityDamageRadius = f.floatValue();
    }, explosionConfig -> Float.valueOf(explosionConfig.entityDamageRadius), (explosionConfig, parent) -> {
        explosionConfig.entityDamageRadius = parent.entityDamageRadius;
    }).documentation("The radius in which entities should be damaged by the explosion.").add()).appendInherited(new KeyedCodec<Float>("EntityDamage", Codec.FLOAT), (explosionConfig, f) -> {
        explosionConfig.entityDamage = f.floatValue();
    }, explosionConfig -> Float.valueOf(explosionConfig.entityDamage), (explosionConfig, parent) -> {
        explosionConfig.entityDamage = parent.entityDamage;
    }).documentation("The amount of damage to be applied to entities within range.").add()).appendInherited(new KeyedCodec<Float>("EntityDamageFalloff", Codec.FLOAT), (explosionConfig, f) -> {
        explosionConfig.entityDamageFalloff = f.floatValue();
    }, explosionConfig -> Float.valueOf(explosionConfig.entityDamageFalloff), (explosionConfig, parent) -> {
        explosionConfig.entityDamageFalloff = parent.entityDamageFalloff;
    }).documentation("The falloff applied to the entity damage.").add()).appendInherited(new KeyedCodec<Knockback>("Knockback", Knockback.CODEC), (explosionConfig, s) -> {
        explosionConfig.knockback = s;
    }, explosionConfig -> explosionConfig.knockback, (explosionConfig, parent) -> {
        explosionConfig.knockback = parent.knockback;
    }).documentation("Determines the knockback effect applied to damaged entities.").add()).appendInherited(new KeyedCodec<ItemTool>("ItemTool", ItemTool.CODEC), (damageEffects, s) -> {
        damageEffects.itemTool = s;
    }, damageEffects -> damageEffects.itemTool, (damageEffects, parent) -> {
        damageEffects.itemTool = parent.itemTool;
    }).documentation("The item tool to reference when applying damage to blocks.").add()).build();
    protected boolean damageEntities = true;
    protected boolean damageBlocks = true;
    protected int blockDamageRadius = 3;
    protected float blockDamageFalloff = 1.0f;
    protected float entityDamageRadius = 5.0f;
    protected float entityDamage = 50.0f;
    protected float entityDamageFalloff = 1.0f;
    protected float blockDropChance = 1.0f;
    @Nullable
    protected Knockback knockback;
    @Nullable
    protected ItemTool itemTool;
}

