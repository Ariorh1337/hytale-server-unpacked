/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.asset.builder.BuilderFactory;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderModifier;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.asset.builder.FeatureEvaluatorHelper;
import com.hypixel.hytale.server.npc.asset.builder.InstructionContextHelper;
import com.hypixel.hytale.server.npc.asset.builder.InternalReferenceResolver;
import com.hypixel.hytale.server.npc.asset.builder.StateMappingHelper;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ReferenceProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringArrayNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderObjectReferenceHelper<T>
extends BuilderObjectHelper<T> {
    public static final String KEY_REFERENCE = "Reference";
    public static final String KEY_LOCAL = "Local";
    public static final String KEY_INTERFACE_LIST = "Interfaces";
    public static final String KEY_NULLABLE = "Nullable";
    public static final String NULL_COMPONENT = "$Null";
    public static final String KEY_LABEL = "$Label";
    @Nullable
    protected Builder<T> builder = null;
    protected final StringHolder fileReference = new StringHolder();
    protected String[] componentInterfaces;
    protected int referenceIndex = Integer.MIN_VALUE;
    protected boolean isReference;
    protected boolean isNullable;
    @Nullable
    protected BuilderModifier modifier = null;
    protected FeatureEvaluatorHelper evaluatorHelper;
    protected InternalReferenceResolver internalReferenceResolver;
    protected boolean isInternalReference;
    protected String label;

    public BuilderObjectReferenceHelper(Class<?> classType, BuilderContext owner) {
        super(classType, owner);
    }

    public boolean excludeFromRegularBuild() {
        if (this.builder == null) {
            return false;
        }
        return this.builder.excludeFromRegularBuilding();
    }

    @Override
    @Nullable
    public T build(@Nonnull BuilderSupport builderSupport) {
        T instance;
        boolean hasLocalComponentStates;
        if (!this.isPresent()) {
            return null;
        }
        Builder<T> builder = this.getBuilder(builderSupport.getBuilderManager(), builderSupport, this.isNullable);
        if (builder == null) {
            return null;
        }
        StateMappingHelper mappingHelper = builder.getStateMappingHelper();
        boolean bl = hasLocalComponentStates = this.builder == null && mappingHelper != null && mappingHelper.hasComponentStates();
        if (hasLocalComponentStates) {
            mappingHelper.initialiseComponentState(builderSupport);
        }
        if (this.modifier == null) {
            T instance2;
            this.validateRequiredFeatures(builder, builderSupport.getBuilderManager(), builderSupport.getExecutionContext());
            T t = instance2 = builder.isEnabled(builderSupport.getExecutionContext()) ? (T)builder.build(builderSupport) : null;
            if (hasLocalComponentStates) {
                mappingHelper.popComponentState(builderSupport);
            }
            return instance2;
        }
        Scope globalScope = null;
        if (this.isInternalReference) {
            globalScope = builderSupport.getGlobalScope();
            Objects.requireNonNull(globalScope, "Global scope should not be null when applying to an internal component");
        }
        if (this.modifier.exportedStateCount() != builder.getStateMappingHelper().importedStateCount()) {
            throw new SkipSentryException(new IllegalStateException(String.format("Number of exported states does not match imported states in component %s", this.fileReference.get(builderSupport.getExecutionContext()))));
        }
        ExecutionContext context = builderSupport.getExecutionContext();
        Scope newScope = this.modifier.createScope(builderSupport, builder.getBuilderParameters(), globalScope);
        Scope oldScope = context.setScope(newScope);
        if (this.modifier.exportedStateCount() > 0) {
            this.modifier.applyComponentStateMap(builderSupport);
        }
        this.validateRequiredFeatures(builder, builderSupport.getBuilderManager(), context);
        this.validateInstructionContext(builder, builderSupport);
        T t = instance = builder.isEnabled(builderSupport.getExecutionContext()) ? (T)builder.build(builderSupport) : null;
        if (this.modifier.exportedStateCount() > 0) {
            this.modifier.popComponentStateMap(builderSupport);
        }
        if (hasLocalComponentStates) {
            mappingHelper.popComponentState(builderSupport);
        }
        builderSupport.getExecutionContext().setScope(oldScope);
        return instance;
    }

    @Override
    public boolean validate(String configName, NPCLoadTimeValidationHelper loadTimeValidationHelper, @Nonnull BuilderManager manager, @Nonnull ExecutionContext context, Scope globalScope, @Nonnull List<String> errors) {
        Scope newScope;
        Builder<T> builder;
        if (!this.isPresent()) {
            return true;
        }
        try {
            builder = this.getBuilder(manager, context, null);
        }
        catch (Exception e) {
            errors.add(String.format("%s: %s", configName, e.getMessage()));
            return false;
        }
        if (builder == null) {
            if (this.isNullable) {
                return true;
            }
            errors.add(String.format("%s: %s is not a nullable component reference but a null component was passed", configName, this.fileReference.getExpressionString()));
            return false;
        }
        if (this.modifier == null) {
            if (!builder.isEnabled(context)) {
                return true;
            }
            boolean result = true;
            try {
                this.validateRequiredFeatures(builder, manager, context);
            }
            catch (Exception e) {
                errors.add(String.format("%s: %s", configName, e.getMessage()));
                result = false;
            }
            return result &= builder.validate(configName, loadTimeValidationHelper, context, globalScope, errors);
        }
        boolean result = true;
        if (this.modifier.exportedStateCount() != builder.getStateMappingHelper().importedStateCount()) {
            errors.add(String.format("%s: Number of exported states does not match imported states in component %s", configName, this.fileReference.get(context)));
            result = false;
        }
        Scope additionalScope = this.isInternalReference ? globalScope : null;
        try {
            newScope = this.modifier.createScope(context, builder.getBuilderParameters(), additionalScope);
        }
        catch (Exception e) {
            errors.add(String.format("%s: %s", configName, e.getMessage()));
            return false;
        }
        Scope oldScope = context.setScope(newScope);
        if (builder.isEnabled(context)) {
            try {
                this.validateRequiredFeatures(builder, manager, context);
            }
            catch (Exception e) {
                errors.add(String.format("%s: %s", configName, e.getMessage()));
                result = false;
            }
            result &= builder.validate(configName, loadTimeValidationHelper, context, globalScope, errors);
        }
        context.setScope(oldScope);
        return result;
    }

    @Nullable
    public Builder<T> getBuilder(@Nonnull BuilderManager builderManager, @Nonnull BuilderSupport support, boolean nullable) {
        Builder<T> builder = this.getBuilder(builderManager, support.getExecutionContext(), support.getParentSpawnable());
        if (!nullable && builder == null) {
            throw new NullPointerException(String.format("ReferenceHelper failed to get builder: %s", this.getClassType().getSimpleName()));
        }
        return builder;
    }

    @Nullable
    public Builder<T> getBuilder(@Nonnull BuilderManager builderManager, ExecutionContext context, @Nullable Builder<?> parentSpawnable) {
        if (this.builder != null) {
            return this.builder;
        }
        if (this.isInternalReference) {
            return this.internalReferenceResolver.getBuilder(this.referenceIndex, this.classType);
        }
        if (this.referenceIndex >= 0) {
            Builder builder = builderManager.tryGetCachedValidBuilder(this.referenceIndex, this.classType);
            if (builder == null) {
                throw new SkipSentryException(new IllegalStateException(String.format("Builder %s exists but is not valid!", builderManager.lookupName(this.referenceIndex))));
            }
            return builder;
        }
        String reference = this.fileReference.get(context);
        if (reference.equals(NULL_COMPONENT)) {
            return null;
        }
        int idx = builderManager.getIndex(reference);
        if (idx >= 0) {
            if (parentSpawnable != null) {
                parentSpawnable.addDynamicDependency(idx);
            }
            Builder builder = builderManager.getCachedBuilder(idx, this.classType);
            String builderInterfaceCode = builder.getBuilderParameters().getInterfaceCode();
            this.validateComponentInterfaceMatch(builderInterfaceCode);
            return builder;
        }
        if (!reference.isEmpty()) {
            throw new SkipSentryException(new IllegalStateException("Failed to find builder for: " + reference));
        }
        return null;
    }

    @Override
    public void readConfig(@Nonnull JsonElement data, @Nonnull BuilderManager builderManager, @Nonnull BuilderParameters builderParameters, @Nonnull BuilderValidationHelper builderValidationHelper) {
        this.readConfig(data, builderManager.getFactory(this.classType), builderManager, builderParameters, builderValidationHelper);
    }

    public void readConfig(@Nonnull JsonElement data, @Nonnull BuilderFactory<T> factory, @Nonnull BuilderManager builderManager, @Nonnull BuilderParameters builderParameters, @Nonnull BuilderValidationHelper builderValidationHelper) {
        JsonElement referenceValue;
        super.readConfig(data, builderManager, builderParameters, builderValidationHelper);
        if (data.isJsonNull()) {
            this.builder = null;
            return;
        }
        if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isString()) {
            builderValidationHelper.getReadErrors().add(builderValidationHelper.getName() + ": String reference '" + data.getAsString() + "' to a component is deprecated. Use the 'Reference' parameter instead.");
            return;
        }
        JsonObject jsonObject = data.isJsonObject() ? data.getAsJsonObject() : null;
        JsonElement jsonElement = referenceValue = jsonObject != null ? jsonObject.get(KEY_REFERENCE) : null;
        if (referenceValue != null) {
            try {
                if (BuilderBase.readBoolean(jsonObject, KEY_LOCAL, false)) {
                    BuilderModifier.readModifierObject(data.getAsJsonObject(), builderParameters, this.fileReference, holder -> this.setInternalReference((StringHolder)holder, builderValidationHelper.getInternalReferenceResolver()), modifier -> {
                        this.modifier = modifier;
                    }, builderValidationHelper.getStateMappingHelper(), builderValidationHelper.getExtraInfo());
                } else {
                    JsonObject dataObj = data.getAsJsonObject();
                    BuilderModifier.readModifierObject(dataObj, builderParameters, this.fileReference, holder -> this.setFileReference((StringHolder)holder, dataObj, builderManager), modifier -> {
                        this.modifier = modifier;
                    }, builderValidationHelper.getStateMappingHelper(), builderValidationHelper.getExtraInfo());
                }
                FeatureEvaluatorHelper evaluatorHelper = builderValidationHelper.getFeatureEvaluatorHelper();
                if (evaluatorHelper != null) {
                    if (evaluatorHelper.canAddProvider()) {
                        evaluatorHelper.add(new ReferenceProviderEvaluator(this.referenceIndex, this.classType));
                        evaluatorHelper.setContainsReference();
                        return;
                    }
                    this.evaluatorHelper = evaluatorHelper;
                }
            }
            catch (IllegalArgumentException | IllegalStateException e) {
                builderValidationHelper.getReadErrors().add(builderValidationHelper.getName() + ": " + e.getMessage() + " at " + this.getBreadCrumbs());
            }
        } else {
            this.builder = factory.createBuilder(data);
            if (this.builder.isDeprecated()) {
                builderManager.checkIfDeprecated(this.builder, factory, data, builderParameters.getFileName(), this.getBreadCrumbs());
            }
            if (data.isJsonObject() && data.getAsJsonObject().has(KEY_LABEL) && data.getAsJsonObject().get(KEY_LABEL).isJsonPrimitive()) {
                this.builder.setLabel(data.getAsJsonObject().get(KEY_LABEL).getAsString());
            } else {
                this.builder.setLabel(factory.getKeyName(data));
            }
            this.builder.readConfig(this, data, builderManager, builderParameters, builderValidationHelper);
        }
    }

    protected void setInternalReference(@Nonnull StringHolder holder, InternalReferenceResolver referenceResolver) {
        this.isInternalReference = true;
        this.isReference = true;
        if (holder.isStatic()) {
            this.internalReferenceResolver = referenceResolver;
            this.referenceIndex = this.internalReferenceResolver.getOrCreateIndex(holder.get(null));
        }
    }

    protected void setFileReference(@Nonnull StringHolder holder, @Nonnull JsonObject jsonObject, @Nonnull BuilderManager builderManager) {
        this.isInternalReference = false;
        this.isReference = true;
        this.componentInterfaces = BuilderBase.readStringArray(jsonObject, KEY_INTERFACE_LIST, StringNotEmptyValidator.get(), null);
        this.isNullable = BuilderBase.readBoolean(jsonObject, KEY_NULLABLE, false);
        if (holder.isStatic()) {
            this.referenceIndex = builderManager.getOrCreateIndex(holder.get(null));
            this.builderParameters.addDependency(this.referenceIndex);
        } else if (!StringArrayNotEmptyValidator.get().test(this.componentInterfaces)) {
            throw new SkipSentryException(new IllegalStateException("Computable references must define a list of 'Interfaces' to control which components can be attached."));
        }
    }

    private void validateRequiredFeatures(@Nonnull Builder<T> builder, BuilderManager manager, ExecutionContext context) {
        builder.validateReferencedProvidedFeatures(manager, context);
        if (this.evaluatorHelper != null) {
            this.evaluatorHelper.validateProviderReferences(manager, context);
            builder.getEvaluatorHelper().validateComponentRequirements(this.evaluatorHelper, context);
        }
    }

    private void validateInstructionContext(@Nonnull Builder<T> builder, @Nonnull BuilderSupport support) {
        InstructionContextHelper instructionContextHelper = builder.getInstructionContextHelper();
        if (instructionContextHelper == null || this.isInternalReference) {
            return;
        }
        instructionContextHelper.validateComponentContext(support.getCurrentInstructionContext(), support.getCurrentComponentContext());
    }

    private void validateComponentInterfaceMatch(String builderInterfaceCode) {
        for (String componentInterface : this.componentInterfaces) {
            if (!componentInterface.equals(builderInterfaceCode)) continue;
            return;
        }
        throw new SkipSentryException(new IllegalStateException(String.format("Component code %s does not match any of slot codes: %s.", builderInterfaceCode, Arrays.toString(this.componentInterfaces))));
    }

    @Override
    public boolean isPresent() {
        return this.isFinal() || this.isReference;
    }

    public boolean isFinal() {
        return this.builder != null;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

