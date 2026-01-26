/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.codec.schema.NamedSchema;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.SchemaConvertable;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptor;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.asset.builder.FeatureEvaluatorHelper;
import com.hypixel.hytale.server.npc.asset.builder.InstructionContextHelper;
import com.hypixel.hytale.server.npc.asset.builder.StateMappingHelper;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Builder<T>
extends BuilderContext,
SchemaConvertable<Void>,
NamedSchema {
    @Nullable
    public T build(BuilderSupport var1);

    public boolean validate(String var1, NPCLoadTimeValidationHelper var2, ExecutionContext var3, Scope var4, List<String> var5);

    public void readConfig(BuilderContext var1, JsonElement var2, BuilderManager var3, BuilderParameters var4, BuilderValidationHelper var5);

    public void ignoreAttribute(String var1);

    public Class<T> category();

    public void setTypeName(String var1);

    public String getTypeName();

    public void setLabel(String var1);

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext var1);

    public BuilderDescriptor getDescriptor(String var1, String var2, BuilderManager var3);

    default public boolean isDeprecated() {
        return this.getBuilderDescriptorState() == BuilderDescriptorState.Deprecated;
    }

    @Nullable
    public BuilderDescriptorState getBuilderDescriptorState();

    public IntSet getDependencies();

    default public boolean hasDynamicDependencies() {
        return false;
    }

    default public void addDynamicDependency(int builderIndex) {
        throw new IllegalStateException("Builder: Adding dynamic dependencies is not supported");
    }

    @Nullable
    default public IntSet getDynamicDependencies() {
        return null;
    }

    default public void clearDynamicDependencies() {
    }

    public BuilderParameters getBuilderParameters();

    public FeatureEvaluatorHelper getEvaluatorHelper();

    public StateMappingHelper getStateMappingHelper();

    public InstructionContextHelper getInstructionContextHelper();

    public boolean canRequireFeature();

    public void validateReferencedProvidedFeatures(BuilderManager var1, ExecutionContext var2);

    public boolean excludeFromRegularBuilding();

    public boolean isEnabled(ExecutionContext var1);

    default public boolean isSpawnable() {
        return false;
    }
}

