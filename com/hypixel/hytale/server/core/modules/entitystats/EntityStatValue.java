/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.modules.entitystats.RegeneratingValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityStatValue {
    public static final EntityStatValue[] EMPTY_ARRAY = new EntityStatValue[0];
    public static final BuilderCodec<EntityStatValue> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(EntityStatValue.class, EntityStatValue::new).addField(new KeyedCodec<String>("Id", Codec.STRING), (regenerating, value) -> {
        regenerating.id = value;
    }, regenerating -> regenerating.id)).addField(new KeyedCodec<Float>("Value", Codec.FLOAT), (regenerating, value) -> {
        regenerating.value = value.floatValue();
    }, regenerating -> Float.valueOf(regenerating.value))).addField(new KeyedCodec("Modifiers", new MapCodec<Modifier, HashMap>(Modifier.CODEC, HashMap::new, false)), (regenerating, value) -> {
        regenerating.modifiers = value;
    }, regenerating -> regenerating.modifiers == null || regenerating.modifiers.isEmpty() ? null : regenerating.modifiers)).build();
    private String id;
    private int index = Integer.MIN_VALUE;
    private float value;
    private float min;
    private float max;
    private boolean ignoreInvulnerability;
    @Nullable
    private RegeneratingValue[] regeneratingValues;
    @Nullable
    private Map<String, Modifier> modifiers;

    protected EntityStatValue() {
    }

    public EntityStatValue(int index, @Nonnull EntityStatType asset) {
        this.id = asset.getId();
        this.index = index;
        this.value = asset.getInitialValue();
        this.synchronizeAsset(index, asset);
    }

    public String getId() {
        return this.id;
    }

    public int getIndex() {
        return this.index;
    }

    public float get() {
        return this.value;
    }

    public float asPercentage() {
        if (this.min == this.max) {
            return 0.0f;
        }
        return (this.value - this.min) / (this.max - this.min);
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    protected float set(float newValue) {
        this.value = MathUtil.clamp(newValue, this.min, this.max);
        return this.value;
    }

    @Nullable
    public RegeneratingValue[] getRegeneratingValues() {
        return this.regeneratingValues;
    }

    @Nullable
    public Modifier getModifier(String key) {
        if (this.modifiers == null) {
            return null;
        }
        return this.modifiers.get(key);
    }

    public boolean getIgnoreInvulnerability() {
        return this.ignoreInvulnerability;
    }

    @Nullable
    public Map<String, Modifier> getModifiers() {
        return this.modifiers;
    }

    @Nullable
    protected Modifier putModifier(String key, Modifier modifier) {
        if (this.modifiers == null) {
            this.modifiers = new Object2ObjectOpenHashMap<String, Modifier>();
        }
        Modifier oldModifier = this.modifiers.put(key, modifier);
        this.computeModifiers(EntityStatType.getAssetMap().getAsset(this.index));
        return oldModifier;
    }

    @Nullable
    protected Modifier removeModifier(String key) {
        if (this.modifiers == null) {
            return null;
        }
        Modifier modifier = this.modifiers.remove(key);
        if (modifier != null) {
            this.computeModifiers(EntityStatType.getAssetMap().getAsset(this.index));
        }
        return modifier;
    }

    public boolean synchronizeAsset(int index, @Nonnull EntityStatType asset) {
        this.id = asset.getId();
        this.index = index;
        this.initializeRegenerating(asset);
        boolean minMaxChanged = this.min != asset.getMin() || this.max != asset.getMax();
        this.ignoreInvulnerability = asset.getIgnoreInvulnerability();
        float oldValue = this.value;
        this.computeModifiers(asset);
        return minMaxChanged || this.value != oldValue;
    }

    private void initializeRegenerating(@Nonnull EntityStatType entityStatType) {
        EntityStatType.Regenerating[] regeneratingTypes = entityStatType.getRegenerating();
        if (regeneratingTypes == null) {
            return;
        }
        this.regeneratingValues = new RegeneratingValue[regeneratingTypes.length];
        for (int i = 0; i < regeneratingTypes.length; ++i) {
            this.regeneratingValues[i] = new RegeneratingValue(regeneratingTypes[i]);
        }
    }

    protected void computeModifiers(@Nonnull EntityStatType asset) {
        this.min = asset.getMin();
        this.max = asset.getMax();
        if (this.modifiers != null) {
            block8: for (Modifier.ModifierTarget target : Modifier.ModifierTarget.VALUES) {
                boolean hasAdditive = false;
                float additive = 0.0f;
                boolean hasMultiplicative = false;
                float multiplicative = 0.0f;
                for (Modifier modifier : this.modifiers.values()) {
                    StaticModifier staticModifier;
                    if (!(modifier instanceof StaticModifier) || (staticModifier = (StaticModifier)modifier).getTarget() != target) continue;
                    switch (staticModifier.getCalculationType()) {
                        case ADDITIVE: {
                            hasAdditive = true;
                            additive += staticModifier.getAmount();
                            break;
                        }
                        case MULTIPLICATIVE: {
                            hasMultiplicative = true;
                            multiplicative += staticModifier.getAmount();
                        }
                    }
                }
                switch (target) {
                    case MIN: {
                        if (hasAdditive) {
                            this.min = StaticModifier.CalculationType.ADDITIVE.compute(this.min, additive);
                        }
                        if (!hasMultiplicative) continue block8;
                        this.min = StaticModifier.CalculationType.MULTIPLICATIVE.compute(this.min, multiplicative);
                        continue block8;
                    }
                    case MAX: {
                        if (hasAdditive) {
                            this.max = StaticModifier.CalculationType.ADDITIVE.compute(this.max, additive);
                        }
                        if (!hasMultiplicative) continue block8;
                        this.max = StaticModifier.CalculationType.MULTIPLICATIVE.compute(this.max, multiplicative);
                    }
                }
            }
            for (Modifier modifier : this.modifiers.values()) {
                if (modifier instanceof StaticModifier) continue;
                this.applyModifier(modifier);
            }
        }
        this.value = MathUtil.clamp(this.value, this.min, this.max);
    }

    private void applyModifier(@Nonnull Modifier modifier) {
        switch (modifier.getTarget()) {
            case MIN: {
                this.min = modifier.apply(this.min);
                break;
            }
            case MAX: {
                this.max = modifier.apply(this.max);
            }
        }
    }

    @Nonnull
    public String toString() {
        return "EntityStatValue{id='" + this.id + "', index=" + this.index + ", value=" + this.value + ", min=" + this.min + ", max=" + this.max + ", regeneratingValues=" + Arrays.toString(this.regeneratingValues) + ", modifiers=" + String.valueOf(this.modifiers) + "}";
    }
}

