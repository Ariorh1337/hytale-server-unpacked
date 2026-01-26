/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.valuestore.ValueStore;
import com.hypixel.hytale.server.npc.valuestore.ValueStoreValidator;
import java.util.function.ToIntFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderValueToParameterMapping
extends BuilderBase<ValueToParameterMapping> {
    protected ValueStore.Type type;
    protected String fromValue;
    protected ToIntFunction<BuilderSupport> fromSlot;
    protected String toParameter;

    @Override
    @Nonnull
    public String getShortDescription() {
        return "An entry containing a list of actions to execute when moving from one state to another";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public ValueToParameterMapping build(BuilderSupport builderSupport) {
        return new ValueToParameterMapping(this, builderSupport);
    }

    @Override
    @Nonnull
    public Class<ValueToParameterMapping> category() {
        return ValueToParameterMapping.class;
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    public boolean isEnabled(ExecutionContext context) {
        return true;
    }

    @Override
    @Nonnull
    public Builder<ValueToParameterMapping> readConfig(@Nonnull JsonElement data) {
        this.requireEnum(data, "ValueType", (E e) -> {
            this.type = e;
        }, ValueStore.Type.class, BuilderDescriptorState.Stable, "The type of the value being mapped", null);
        this.requireString(data, "FromValue", (String s) -> {
            this.fromValue = s;
        }, (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The value to read from the value store", null);
        if (this.builderDescriptor == null) {
            this.fromSlot = switch (this.type) {
                default -> throw new MatchException(null, null);
                case ValueStore.Type.String -> this.requireStringValueStoreParameter(this.fromValue, ValueStoreValidator.UseType.READ);
                case ValueStore.Type.Int -> this.requireIntValueStoreParameter(this.fromValue, ValueStoreValidator.UseType.READ);
                case ValueStore.Type.Double -> this.requireDoubleValueStoreParameter(this.fromValue, ValueStoreValidator.UseType.READ);
            };
        }
        this.requireString(data, "ToParameter", (String s) -> {
            this.toParameter = s;
        }, (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The parameter name to override", null);
        return this;
    }

    public ValueStore.Type getType() {
        return this.type;
    }

    public int getFromSlot(BuilderSupport support) {
        return this.fromSlot.applyAsInt(support);
    }

    public String getToParameter() {
        return this.toParameter;
    }

    public static class ValueToParameterMapping {
        private final ValueStore.Type type;
        private int fromValueSlot;
        private int toParameterSlot;
        private String toParameterSlotName;

        private ValueToParameterMapping(@Nonnull BuilderValueToParameterMapping builder, @Nullable BuilderSupport support) {
            this.type = builder.getType();
            if (support != null) {
                this.fromValueSlot = builder.getFromSlot(support);
                this.toParameterSlot = support.getParameterSlot(builder.getToParameter());
            } else {
                this.toParameterSlotName = builder.getToParameter();
            }
        }

        public ValueStore.Type getType() {
            return this.type;
        }

        public int getFromValueSlot() {
            return this.fromValueSlot;
        }

        public int getToParameterSlot() {
            return this.toParameterSlot;
        }

        public String getToParameterSlotName() {
            return this.toParameterSlotName;
        }
    }
}

