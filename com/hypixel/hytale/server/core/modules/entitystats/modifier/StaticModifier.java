/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats.modifier;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StaticModifier
extends Modifier {
    public static final BuilderCodec<StaticModifier> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StaticModifier.class, StaticModifier::new, BASE_CODEC).append(new KeyedCodec<CalculationType>("CalculationType", new EnumCodec<CalculationType>(CalculationType.class)), (modifier, value) -> {
        modifier.calculationType = value;
    }, modifier -> modifier.calculationType).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Float>("Amount", Codec.FLOAT), (modifier, value) -> {
        modifier.amount = value.floatValue();
    }, modifier -> Float.valueOf(modifier.amount)).add()).build();
    public static final BuilderCodec<StaticModifier> ENTITY_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StaticModifier.class, StaticModifier::new).append(new KeyedCodec<CalculationType>("CalculationType", new EnumCodec<CalculationType>(CalculationType.class, EnumCodec.EnumStyle.LEGACY)), (modifier, value) -> {
        modifier.calculationType = value;
    }, modifier -> modifier.calculationType).setVersionRange(0, 3).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<CalculationType>("CalculationType", new EnumCodec<CalculationType>(CalculationType.class)), (modifier, value) -> {
        modifier.calculationType = value;
    }, modifier -> modifier.calculationType).setVersionRange(4, 5).addValidator(Validators.nonNull()).add()).addField(new KeyedCodec<Float>("Amount", Codec.FLOAT), (modifier, value) -> {
        modifier.amount = value.floatValue();
    }, modifier -> Float.valueOf(modifier.amount))).build();
    protected CalculationType calculationType;
    protected float amount;

    protected StaticModifier() {
    }

    public StaticModifier(Modifier.ModifierTarget target, CalculationType calculationType, float amount) {
        super(target);
        this.calculationType = calculationType;
        this.amount = amount;
    }

    public CalculationType getCalculationType() {
        return this.calculationType;
    }

    public float getAmount() {
        return this.amount;
    }

    @Override
    public float apply(float statValue) {
        return this.calculationType.compute(statValue, this.amount);
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.Modifier toPacket() {
        com.hypixel.hytale.protocol.Modifier packet = super.toPacket();
        packet.calculationType = switch (this.calculationType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> com.hypixel.hytale.protocol.CalculationType.Additive;
            case 1 -> com.hypixel.hytale.protocol.CalculationType.Multiplicative;
        };
        packet.amount = this.amount;
        return packet;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        StaticModifier that = (StaticModifier)o;
        if (Float.compare(that.amount, this.amount) != 0) {
            return false;
        }
        return this.calculationType == that.calculationType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.calculationType != null ? this.calculationType.hashCode() : 0);
        result = 31 * result + (this.amount != 0.0f ? Float.floatToIntBits(this.amount) : 0);
        return result;
    }

    @Override
    @Nonnull
    public String toString() {
        return "StaticModifier{calculationType=" + String.valueOf((Object)this.calculationType) + ", amount=" + this.amount + "} " + super.toString();
    }

    public static enum CalculationType {
        ADDITIVE{

            @Override
            public float compute(float value, float amount) {
                return value + amount;
            }
        }
        ,
        MULTIPLICATIVE{

            @Override
            public float compute(float value, float amount) {
                return value * amount;
            }
        };


        public abstract float compute(float var1, float var2);

        @Nonnull
        public String createKey(String armor) {
            return armor + "_" + String.valueOf((Object)this);
        }
    }
}

