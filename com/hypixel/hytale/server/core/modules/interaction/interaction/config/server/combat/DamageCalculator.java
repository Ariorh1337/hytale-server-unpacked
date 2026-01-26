/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.map.Object2FloatMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageClass;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMaps;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DamageCalculator {
    public static final BuilderCodec<DamageCalculator> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DamageCalculator.class, DamageCalculator::new).appendInherited(new KeyedCodec<Type>("Type", Type.CODEC), (damageCalculator, type) -> {
        damageCalculator.type = type;
    }, damageCalculator -> damageCalculator.type, (damageCalculator, parent) -> {
        damageCalculator.type = parent.type;
    }).add()).appendInherited(new KeyedCodec<DamageClass>("Class", DamageClass.CODEC), (o, v) -> {
        o.damageClass = v;
    }, o -> o.damageClass, (o, p) -> {
        o.damageClass = p.damageClass;
    }).documentation("The class of the damage being created, used by the damage system to apply modifiers based on equipment of the source.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<String>("BaseDamage", new Object2FloatMapCodec<String>(Codec.STRING, Object2FloatOpenHashMap::new)), (damageCalculator, map) -> {
        damageCalculator.baseDamageRaw = map;
    }, damageCalculator -> damageCalculator.baseDamageRaw, (damageCalculator, parent) -> {
        damageCalculator.baseDamageRaw = parent.baseDamageRaw;
    }).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).appendInherited(new KeyedCodec<Float>("SequentialModifierStep", Codec.FLOAT), (damageCalculator, sequentialModifierStep) -> {
        damageCalculator.sequentialModifierStep = sequentialModifierStep.floatValue();
    }, damageCalculator -> Float.valueOf(damageCalculator.sequentialModifierStep), (damageCalculator, parent) -> {
        damageCalculator.sequentialModifierStep = parent.sequentialModifierStep;
    }).add()).appendInherited(new KeyedCodec<Float>("SequentialModifierMinimum", Codec.FLOAT), (damageCalculator, sequentialModifierMinimum) -> {
        damageCalculator.sequentialModifierMinimum = sequentialModifierMinimum.floatValue();
    }, damageCalculator -> Float.valueOf(damageCalculator.sequentialModifierMinimum), (damageCalculator, parent) -> {
        damageCalculator.sequentialModifierMinimum = parent.sequentialModifierMinimum;
    }).add()).appendInherited(new KeyedCodec<Float>("RandomPercentageModifier", Codec.FLOAT), (damageCalculator, randomPercentageModifier) -> {
        damageCalculator.randomPercentageModifier = randomPercentageModifier.floatValue();
    }, damageCalculator -> Float.valueOf(damageCalculator.randomPercentageModifier), (damageCalculator, parent) -> {
        damageCalculator.randomPercentageModifier = parent.randomPercentageModifier;
    }).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).afterDecode(asset -> {
        if (asset.baseDamageRaw != null) {
            asset.baseDamage = new Int2FloatOpenHashMap();
            for (Object2FloatMap.Entry entry : asset.baseDamageRaw.object2FloatEntrySet()) {
                int index = DamageCause.getAssetMap().getIndex((String)entry.getKey());
                asset.baseDamage.put(index, entry.getFloatValue());
            }
        }
    })).build();
    protected Type type = Type.ABSOLUTE;
    @Nonnull
    protected DamageClass damageClass = DamageClass.UNKNOWN;
    protected Object2FloatMap<String> baseDamageRaw;
    protected float sequentialModifierStep;
    protected float sequentialModifierMinimum;
    protected float randomPercentageModifier;
    @Nonnull
    protected transient Int2FloatMap baseDamage = Int2FloatMaps.EMPTY_MAP;

    protected DamageCalculator() {
    }

    @Nullable
    public Object2FloatMap<DamageCause> calculateDamage(double durationSeconds) {
        if (this.baseDamageRaw == null || this.baseDamageRaw.isEmpty()) {
            return null;
        }
        Object2FloatOpenHashMap<DamageCause> outDamage = new Object2FloatOpenHashMap<DamageCause>(this.baseDamage.size());
        float randomPercentageModifier = MathUtil.randomFloat(-this.randomPercentageModifier, this.randomPercentageModifier);
        for (Int2FloatMap.Entry entry : this.baseDamage.int2FloatEntrySet()) {
            DamageCause damageCause = DamageCause.getAssetMap().getAsset(entry.getIntKey());
            float value = entry.getFloatValue();
            float damage = this.scaleDamage(durationSeconds, value);
            damage += damage * randomPercentageModifier;
            outDamage.put(damageCause, damage);
        }
        return outDamage;
    }

    private float scaleDamage(double durationSeconds, float damage) {
        return switch (this.type.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> (float)durationSeconds * damage;
            case 1 -> damage;
        };
    }

    public Type getType() {
        return this.type;
    }

    @Nonnull
    public DamageClass getDamageClass() {
        return this.damageClass;
    }

    public float getSequentialModifierStep() {
        return this.sequentialModifierStep;
    }

    public float getSequentialModifierMinimum() {
        return this.sequentialModifierMinimum;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DamageCalculator)) {
            return false;
        }
        DamageCalculator that = (DamageCalculator)o;
        if (Double.compare(that.sequentialModifierStep, this.sequentialModifierStep) != 0) {
            return false;
        }
        if (Double.compare(that.sequentialModifierMinimum, this.sequentialModifierMinimum) != 0) {
            return false;
        }
        if (Double.compare(that.randomPercentageModifier, this.randomPercentageModifier) != 0) {
            return false;
        }
        if (this.type != that.type) {
            return false;
        }
        if (!Objects.equals(this.baseDamageRaw, that.baseDamageRaw)) {
            return false;
        }
        return false;
    }

    public int hashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + (this.baseDamageRaw != null ? this.baseDamageRaw.hashCode() : 0);
        long temp = Double.doubleToLongBits(this.sequentialModifierStep);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.sequentialModifierMinimum);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.randomPercentageModifier);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    @Nonnull
    public String toString() {
        return "DamageCalculator{type=" + String.valueOf((Object)this.type) + ", baseDamage=" + String.valueOf(this.baseDamageRaw) + ", sequentialModifierStep=" + this.sequentialModifierStep + ", sequentialModifierMinimum=" + this.sequentialModifierMinimum + ", randomPercentageModifier=" + this.randomPercentageModifier + "}";
    }

    public static enum Type {
        DPS,
        ABSOLUTE;

        public static final EnumCodec<Type> CODEC;

        static {
            CODEC = new EnumCodec<Type>(Type.class);
        }
    }
}

