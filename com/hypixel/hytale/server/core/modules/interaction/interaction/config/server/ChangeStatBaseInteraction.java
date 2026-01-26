/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.map.Object2FloatMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.ChangeStatBehaviour;
import com.hypixel.hytale.protocol.ValueType;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.util.InteractionTarget;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ChangeStatBaseInteraction
extends SimpleInstantInteraction {
    public static final BuilderCodec<ChangeStatBaseInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(ChangeStatBaseInteraction.class, SimpleInstantInteraction.CODEC).append(new KeyedCodec<String>("StatModifiers", new Object2FloatMapCodec<String>(Codec.STRING, Object2FloatOpenHashMap::new), true), (changeStatInteraction, stringObject2DoubleMap) -> {
        changeStatInteraction.entityStatAssets = stringObject2DoubleMap;
    }, changeStatInteraction -> changeStatInteraction.entityStatAssets).addValidator(Validators.nonNull()).addValidator(Validators.nonEmptyMap()).addValidator(EntityStatType.VALIDATOR_CACHE.getMapKeyValidator()).documentation("Modifiers to apply to EntityStats.").add()).append(new KeyedCodec<ValueType>("ValueType", new EnumCodec<ValueType>(ValueType.class)), (changeStatInteraction, valueType) -> {
        changeStatInteraction.valueType = valueType;
    }, changeStatInteraction -> changeStatInteraction.valueType).documentation("Enum to specify if the StatModifiers must be considered as absolute values or percent. Default value is Absolute. When using ValueType.Absolute, '100' matches the max value.").add()).append(new KeyedCodec<ChangeStatBehaviour>("Behaviour", ProtocolCodecs.CHANGE_STAT_BEHAVIOUR_CODEC), (changeStatInteraction, changeStatBehaviour) -> {
        changeStatInteraction.changeStatBehaviour = changeStatBehaviour;
    }, changeStatInteraction -> changeStatInteraction.changeStatBehaviour).documentation("Specifies how StatModifiers should be applied to the stats.").add()).appendInherited(new KeyedCodec<InteractionTarget>("Entity", InteractionTarget.CODEC), (o, i) -> {
        o.entityTarget = i;
    }, o -> o.entityTarget, (o, p) -> {
        o.entityTarget = p.entityTarget;
    }).documentation("The entity to target for this interaction.").addValidator(Validators.nonNull()).add()).afterDecode(changeStatInteraction -> {
        changeStatInteraction.entityStats = EntityStatsModule.resolveEntityStats(changeStatInteraction.entityStatAssets);
    })).build();
    protected Object2FloatMap<String> entityStatAssets;
    @Nullable
    protected Int2FloatMap entityStats;
    protected ValueType valueType = ValueType.Absolute;
    protected ChangeStatBehaviour changeStatBehaviour = ChangeStatBehaviour.Add;
    protected InteractionTarget entityTarget = InteractionTarget.USER;

    @Override
    @Nonnull
    public String toString() {
        return "ChangeStatBaseInteraction{unknownEntityStats=" + String.valueOf(this.entityStatAssets) + ", entityStats=" + String.valueOf(this.entityStats) + ", valueType=" + String.valueOf((Object)this.valueType) + ", changeStatBehaviour=" + String.valueOf((Object)this.changeStatBehaviour) + ", entityTarget=" + String.valueOf((Object)this.entityTarget) + "}";
    }
}

