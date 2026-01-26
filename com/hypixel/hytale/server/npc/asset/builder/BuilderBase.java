/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.BooleanSchema;
import com.hypixel.hytale.codec.schema.config.IntegerSchema;
import com.hypixel.hytale.codec.schema.config.NumberSchema;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.common.util.StringUtil;
import com.hypixel.hytale.function.consumer.BooleanConsumer;
import com.hypixel.hytale.function.consumer.FloatConsumer;
import com.hypixel.hytale.function.consumer.TriConsumer;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderCodecObjectHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptor;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderFactory;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderModifier;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectArrayHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.asset.builder.ComponentContext;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.FeatureEvaluatorHelper;
import com.hypixel.hytale.server.npc.asset.builder.InstructionContextHelper;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.InternalReferenceResolver;
import com.hypixel.hytale.server.npc.asset.builder.StateMappingHelper;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamic;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumSetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.FloatHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.IntHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.TemporalArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.ValueHolder;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ParameterProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ParameterType;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.UnconditionalFeatureProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.UnconditionalParameterProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.util.StringListHelpers;
import com.hypixel.hytale.server.npc.asset.builder.validators.AnyBooleanValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.AnyPresentValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ArraysOneSetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.AtMostOneBooleanValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.AttributeRelationValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.BooleanImplicationValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ComponentOnlyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.EnumArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ExistsIfParameterSetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.InstructionContextValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.NoDuplicatesValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.OneOrNonePresentValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.OnePresentValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RelationalOperator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RequiredFeatureValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RequiresFeatureIfEnumValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RequiresFeatureIfValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RequiresOneOfFeaturesValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StateStringValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringArrayNoEmptyStringsValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringsAtMostOneValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringsNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringsOneSetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.TemporalArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ValidateAssetIfEnumIsValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ValidateIfEnumIsValidator;
import com.hypixel.hytale.server.npc.decisionmaker.core.Evaluator;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import com.hypixel.hytale.server.npc.valuestore.ValueStore;
import com.hypixel.hytale.server.npc.valuestore.ValueStoreValidator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BuilderBase<T>
implements Builder<T> {
    private static final Pattern PATTERN = Pattern.compile("\\s*,\\s*");
    protected String fileName;
    @Nullable
    protected Set<String> queriedKeys = new HashSet<String>();
    protected boolean useDefaultsOnly;
    protected String label;
    protected String typeName;
    protected FeatureEvaluatorHelper evaluatorHelper;
    protected InternalReferenceResolver internalReferenceResolver;
    protected StateMappingHelper stateHelper;
    protected InstructionContextHelper instructionContextHelper;
    protected ExtraInfo extraInfo;
    protected List<Evaluator<?>> evaluators;
    protected BuilderValidationHelper validationHelper;
    @Nullable
    protected BuilderDescriptor builderDescriptor;
    protected BuilderParameters builderParameters;
    protected BuilderManager builderManager;
    protected BuilderContext owner;
    @Nullable
    protected List<String> readErrors;
    private List<ValueHolder> dynamicHolders;
    private List<ValueStoreValidator.ValueUsage> valueStoreUsages;
    @Nullable
    protected ObjectSchema builderSchema;
    protected Schema builderSchemaRaw;
    @Nullable
    protected SchemaContext builderSchemaContext;

    @Override
    public void setTypeName(String name) {
        this.typeName = name;
    }

    @Override
    public String getTypeName() {
        return this.typeName;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public FeatureEvaluatorHelper getEvaluatorHelper() {
        return this.evaluatorHelper;
    }

    @Override
    public StateMappingHelper getStateMappingHelper() {
        return this.stateHelper;
    }

    @Override
    public InstructionContextHelper getInstructionContextHelper() {
        return this.instructionContextHelper;
    }

    @Override
    public void validateReferencedProvidedFeatures(BuilderManager manager, ExecutionContext context) {
        if (this.evaluatorHelper == null) {
            return;
        }
        this.evaluatorHelper.validateProviderReferences(manager, context);
    }

    @Override
    public boolean canRequireFeature() {
        return false;
    }

    @Override
    public boolean excludeFromRegularBuilding() {
        return false;
    }

    @Override
    public final void readConfig(BuilderContext owner, @Nonnull JsonElement data, BuilderManager builderManager, BuilderParameters builderParameters, BuilderValidationHelper builderValidationHelper) {
        this.preReadConfig(owner, builderManager, builderParameters, builderValidationHelper);
        this.readCommonConfig(data);
        this.readConfig(data);
        this.postReadConfig(data);
    }

    private void preReadConfig(BuilderContext owner, BuilderManager builderManager, BuilderParameters builderParameters, @Nullable BuilderValidationHelper builderValidationHelper) {
        this.owner = owner;
        this.useDefaultsOnly = false;
        this.builderParameters = builderParameters;
        this.builderManager = builderManager;
        this.queriedKeys.add("Comment");
        this.queriedKeys.add("$Title");
        this.queriedKeys.add("$Comment");
        this.queriedKeys.add("$Author");
        this.queriedKeys.add("$TODO");
        this.queriedKeys.add("$Position");
        this.queriedKeys.add("$FloatingFunctionNodes");
        this.queriedKeys.add("$Groups");
        this.queriedKeys.add("$WorkspaceID");
        this.queriedKeys.add("$NodeEditorMetadata");
        this.queriedKeys.add("$NodeId");
        if (builderValidationHelper != null) {
            this.validationHelper = builderValidationHelper;
            this.fileName = builderValidationHelper.getName();
            this.evaluatorHelper = builderValidationHelper.getFeatureEvaluatorHelper();
            this.internalReferenceResolver = builderValidationHelper.getInternalReferenceResolver();
            this.stateHelper = builderValidationHelper.getStateMappingHelper();
            this.instructionContextHelper = builderValidationHelper.getInstructionContextHelper();
            this.extraInfo = builderValidationHelper.getExtraInfo();
            this.evaluators = builderValidationHelper.getEvaluators();
            this.readErrors = builderValidationHelper.getReadErrors();
        }
    }

    private void addQueryKey(String name) {
        if (!this.queriedKeys.add(name)) {
            throw new IllegalArgumentException(String.valueOf(name));
        }
    }

    @Override
    public BuilderContext getOwner() {
        return this.owner;
    }

    @Override
    public void ignoreAttribute(String name) {
        this.queriedKeys.add(name);
    }

    private void postReadConfig(@Nonnull JsonElement data) {
        if (this.builderDescriptor == null && data.isJsonObject()) {
            this.queriedKeys.add("Type");
            for (Map.Entry<String, JsonElement> entry : data.getAsJsonObject().entrySet()) {
                String key = entry.getKey();
                if (this.queriedKeys.contains(key)) continue;
                String string = data.toString();
                NPCPlugin.get().getLogger().at(Level.WARNING).log("Unknown JSON attribute '%s' found in %s: %s (JSON: %s)", key, this.getBreadCrumbs(), this.builderParameters.getFileName(), string.length() > 60 ? string.substring(60) + "..." : string);
            }
        }
        this.queriedKeys = null;
        this.readErrors = null;
    }

    public Builder<T> readCommonConfig(JsonElement data) {
        return this;
    }

    public Builder<T> readConfig(JsonElement data) {
        return this;
    }

    public BuilderManager getBuilderManager() {
        return this.builderManager;
    }

    @Override
    public BuilderParameters getBuilderParameters() {
        return this.builderParameters;
    }

    protected JsonObject expectJsonObject(@Nonnull JsonElement data, String name) {
        if (data.isJsonObject()) {
            return data.getAsJsonObject();
        }
        this.checkForUnexpectedComputeObject(data, name);
        throw new IllegalStateException("Expected object when looking for parameter \"" + name + "\" but found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
    }

    protected JsonArray expectJsonArray(@Nonnull JsonElement data, String name) {
        if (data.isJsonArray()) {
            return data.getAsJsonArray();
        }
        this.checkForUnexpectedComputeObject(data, name);
        throw new IllegalStateException("Expected array when looking for parameter \"" + name + "\" but found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
    }

    @Nullable
    protected String expectString(@Nonnull JsonElement data, String name) {
        if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isString()) {
            return data.getAsJsonPrimitive().getAsString();
        }
        if (data.isJsonNull()) {
            return null;
        }
        this.checkForUnexpectedComputeObject(data, name);
        throw new IllegalStateException("Expected string when looking for parameter \"" + name + "\" but found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
    }

    protected double expectDouble(@Nonnull JsonElement data, String name) {
        if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isNumber()) {
            try {
                return data.getAsJsonPrimitive().getAsDouble();
            }
            catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid number when looking for parameter \"" + name + "\", found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
            }
        }
        this.checkForUnexpectedComputeObject(data, name);
        throw new IllegalStateException("Expected number when looking for parameter \"" + name + "\" but found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
    }

    protected int expectInteger(@Nonnull JsonElement data, String name) {
        if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isNumber()) {
            try {
                return data.getAsJsonPrimitive().getAsInt();
            }
            catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid integer number when looking for parameter \"" + name + "\", found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
            }
        }
        this.checkForUnexpectedComputeObject(data, name);
        throw new IllegalStateException("Expected integer number when looking for parameter \"" + name + "\" but found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
    }

    protected boolean expectBoolean(@Nonnull JsonElement data, String name) {
        if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isBoolean()) {
            return data.getAsJsonPrimitive().getAsBoolean();
        }
        this.checkForUnexpectedComputeObject(data, name);
        throw new IllegalStateException("Expected boolean value when looking for parameter \"" + name + "\" but found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
    }

    protected int[] expectIntArray(@Nonnull JsonElement data, String name, int minSize, int maxSize) {
        JsonArray jsonArray = this.expectJsonArray(data, name, minSize, maxSize);
        int count = jsonArray.size();
        int[] array = new int[count];
        for (int i = 0; i < count; ++i) {
            array[i] = this.expectInteger(jsonArray.get(i), name);
        }
        return array;
    }

    protected int[] expectIntArray(@Nonnull JsonElement data, String name, int size) {
        return this.expectIntArray(data, name, size, size);
    }

    protected double[] expectDoubleArray(@Nonnull JsonElement data, String name, int minSize, int maxSize) {
        JsonArray jsonArray = this.expectJsonArray(data, name, minSize, maxSize);
        int count = jsonArray.size();
        double[] array = new double[count];
        for (int i = 0; i < count; ++i) {
            array[i] = this.expectDouble(jsonArray.get(i), name);
        }
        return array;
    }

    protected double[] expectDoubleArray(@Nonnull JsonElement data, String name, int size) {
        return this.expectDoubleArray(data, name, size, size);
    }

    @Nonnull
    protected JsonArray expectJsonArray(@Nonnull JsonElement data, String name, int minSize, int maxSize) {
        JsonArray jsonArray = this.expectJsonArray(data, name);
        int count = jsonArray.size();
        if (count < minSize || count > maxSize) {
            if (maxSize == minSize) {
                throw new IllegalStateException("Expected array with " + maxSize + " elements when looking for parameter \"" + name + "\" but found " + count + " elements in context " + this.getBreadCrumbs());
            }
            throw new IllegalStateException("Expected array with " + minSize + " to " + maxSize + " elements when looking for parameter \"" + name + "\" but found " + count + " elements in context " + this.getBreadCrumbs());
        }
        return jsonArray;
    }

    protected void checkForUnexpectedComputeObject(@Nonnull JsonElement data, String name) {
        if (data.isJsonObject() && data.getAsJsonObject().has("Compute")) {
            throw new IllegalStateException("Parameter \"" + name + "\" of " + this.category().getSimpleName() + " " + this.getTypeName() + " is not computable (yet) in context " + this.getBreadCrumbs());
        }
    }

    @Nonnull
    protected JsonElement getRequiredJsonElement(@Nonnull JsonElement data, String name, boolean addKey) {
        JsonElement element;
        if (addKey) {
            this.addQueryKey(name);
        }
        if ((element = this.expectJsonObject(data, name).get(name)) == null) {
            throw new IllegalStateException("Parameter \"" + name + "\" is missing in context " + this.getBreadCrumbs());
        }
        return element;
    }

    @Nonnull
    protected JsonElement getRequiredJsonElement(@Nonnull JsonElement data, String name) {
        return this.getRequiredJsonElement(data, name, true);
    }

    @Nullable
    protected JsonElement getRequiredJsonElementIfNotOverridden(@Nonnull JsonElement data, String name, @Nonnull ParameterType type, boolean addKey) {
        JsonElement element;
        if (addKey) {
            this.addQueryKey(name);
        }
        if ((element = this.expectJsonObject(data, name).get(name)) != null) {
            return element;
        }
        if (this.evaluatorHelper.belongsToFeatureRequiringComponent()) {
            this.evaluatorHelper.addComponentRequirementValidator((helper, executionContext) -> this.validateOverriddenParameter(name, type, (FeatureEvaluatorHelper)helper));
            return null;
        }
        if (this.hasOverriddenParameter(name, type, this.evaluatorHelper)) {
            return null;
        }
        if (this.evaluatorHelper.requiresProviderReferenceEvaluation()) {
            this.evaluatorHelper.addProviderReferenceValidator((manager, context) -> {
                this.resolveFeatureProviderReverences((BuilderManager)manager);
                this.validateOverriddenParameter(name, type, this.evaluatorHelper);
            });
            return null;
        }
        throw new IllegalStateException(String.format("Parameter %s is missing and either not provided by a sensor, or provided with the wrong parameter type (expected %s) in context %s", name, type.get(), this.getBreadCrumbs()));
    }

    @Nullable
    protected JsonElement getRequiredJsonElementIfNotOverridden(@Nonnull JsonElement data, String name, @Nonnull ParameterType type) {
        return this.getRequiredJsonElementIfNotOverridden(data, name, type, true);
    }

    @Nullable
    protected JsonElement getOptionalJsonElement(@Nonnull JsonElement data, String name, boolean addKey) {
        JsonElement result = null;
        if (!this.useDefaultsOnly) {
            if (addKey) {
                this.addQueryKey(name);
            }
            result = this.expectJsonObject(data, name).get(name);
        }
        return result;
    }

    @Nullable
    protected JsonElement getOptionalJsonElement(@Nonnull JsonElement data, String name) {
        return this.getOptionalJsonElement(data, name, true);
    }

    public void requireString(@Nonnull JsonElement data, String name, @Nonnull Consumer<String> setter, StringValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new StringSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, String.class.getSimpleName(), state, shortDescription, longDescription).required().validator(validator);
            return;
        }
        try {
            this.validateAndSet(this.expectString(this.getRequiredJsonElement(data, name), name), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getString(@Nonnull JsonElement data, String name, @Nonnull Consumer<String> setter, String defaultValue, StringValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new StringSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, String.class.getSimpleName(), state, shortDescription, longDescription).optional(defaultValue).validator(validator);
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = this.expectString(element, name);
            }
            this.validateAndSet(defaultValue, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireString(@Nonnull JsonElement data, String name, @Nonnull StringHolder stringHolder, StringValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new StringSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            stringHolder.setName(name);
            this.builderDescriptor.addAttribute(name, String.class.getSimpleName(), state, shortDescription, longDescription).required().computable().validator(validator);
            return;
        }
        Objects.requireNonNull(stringHolder, "stringHolder is null");
        try {
            stringHolder.readJSON(this.getRequiredJsonElement(data, name), validator, name, this.builderParameters);
            this.trackDynamicHolder(stringHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean requireStringIfNotOverridden(@Nonnull JsonElement data, String name, @Nonnull StringHolder stringHolder, StringValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new StringSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            stringHolder.setName(name);
            this.builderDescriptor.addAttribute(name, String.class.getSimpleName(), state, shortDescription, longDescription).requiredIfNotOverridden().computable().validator(validator);
            return false;
        }
        Objects.requireNonNull(stringHolder, "stringHolder is null");
        try {
            JsonElement element = this.getRequiredJsonElementIfNotOverridden(data, name, ParameterType.STRING);
            boolean valueProvided = element != null;
            stringHolder.readJSON(element, null, !valueProvided ? null : validator, name, this.builderParameters);
            this.trackDynamicHolder(stringHolder);
            return valueProvided;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public boolean getString(@Nonnull JsonElement data, String name, @Nonnull StringHolder stringHolder, String defaultValue, StringValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new StringSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            stringHolder.setName(name);
            this.builderDescriptor.addAttribute(name, String.class.getSimpleName(), state, shortDescription, longDescription).optional(defaultValue).computable().validator(validator);
            return false;
        }
        Objects.requireNonNull(stringHolder, "stringHolder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            stringHolder.readJSON(optionalJsonElement, defaultValue, validator, name, this.builderParameters);
            this.trackDynamicHolder(stringHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    private void validateAndSet(String str, @Nullable StringValidator validator, @Nonnull Consumer<String> setter, String name) {
        if (validator != null && !validator.test(str)) {
            throw new IllegalStateException(validator.errorMessage(str, name) + " in " + this.getBreadCrumbs());
        }
        setter.accept(str);
    }

    @Nonnull
    protected String[] nonNull(@Nullable String[] array) {
        return array == null ? ArrayUtil.EMPTY_STRING_ARRAY : array;
    }

    @Nonnull
    public String[] expectStringArray(@Nonnull JsonElement data, @Nullable Function<String, String> mapper, String name, boolean warning) {
        if (mapper == null) {
            mapper = Function.identity();
        }
        if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isString()) {
            if (warning) {
                NPCPlugin.get().getLogger().at(Level.WARNING).log("Use of strings for lists is deprecated for JSON attribute '%s' (use []) in %s: %s", name, this.getBreadCrumbs(), this.builderParameters.getFileName());
            }
            return (String[])StringListHelpers.splitToStringList(data.getAsJsonPrimitive().getAsString(), mapper).toArray(String[]::new);
        }
        if (!data.isJsonArray()) {
            throw new IllegalStateException("Expected string or array when looking for parameter \"" + name + "\" but found '" + String.valueOf(data) + "' in context " + this.getBreadCrumbs());
        }
        JsonArray array = data.getAsJsonArray();
        String[] result = new String[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            String s = mapper.apply(this.expectString(array.get(i), name).trim());
            if (s == null || s.isEmpty()) continue;
            result[i] = s;
        }
        return result;
    }

    @Nonnull
    public String[] expectStringArray(@Nonnull JsonElement data, Function<String, String> mapper, String name) {
        return this.expectStringArray(data, mapper, name, true);
    }

    public boolean getStringArray(@Nonnull JsonElement data, String name, @Nonnull Consumer<String[]> setter, Function<String, String> mapper, String[] defaultValue, StringArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema();
            a.setItem(new StringSchema());
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "StringList", state, shortDescription, longDescription).optional(this.defaultArrayToString(defaultValue)).validator(validator);
            return false;
        }
        try {
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean haveValue = element != null;
            String[] array = haveValue ? this.expectStringArray(element, mapper, name) : defaultValue;
            this.validateAndSet(array, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireStringArray(@Nonnull JsonElement data, String name, @Nonnull Consumer<String[]> setter, Function<String, String> mapper, StringArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema();
            a.setItem(new StringSchema());
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "StringList", state, shortDescription, longDescription).required().validator(validator);
            return;
        }
        try {
            JsonElement element = this.getRequiredJsonElement(data, name);
            this.validateAndSet(this.expectStringArray(element, mapper, name), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public void requireStringArray(@Nonnull JsonElement data, String name, @Nonnull StringArrayHolder holder, int minLength, int maxLength, StringArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema();
            a.setItem(new StringSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("String").length(minLength, maxLength).validator(validator).computable().required();
            return;
        }
        Objects.requireNonNull(holder, "string array holder is null");
        try {
            holder.readJSON(this.getRequiredJsonElement(data, name), minLength, maxLength, validator, name, this.builderParameters);
            this.trackDynamicHolder(holder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public void requireTemporalArray(@Nonnull JsonElement data, String name, @Nonnull TemporalArrayHolder holder, int minLength, int maxLength, TemporalArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema();
            a.setItem(new StringSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("TemporalAmount").length(minLength, maxLength).validator(validator).computable().required();
            return;
        }
        Objects.requireNonNull(holder, "temporal array holder is null");
        try {
            holder.readJSON(this.getRequiredJsonElement(data, name), minLength, maxLength, validator, name, this.builderParameters);
            this.trackDynamicHolder(holder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public void requireTemporalRange(@Nonnull JsonElement data, String name, @Nonnull TemporalArrayHolder holder, TemporalArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.requireTemporalArray(data, name, holder, 2, 2, validator, state, shortDescription, longDescription);
    }

    public boolean getStringArray(@Nonnull JsonElement data, String name, @Nonnull StringArrayHolder holder, String[] defaultValue, int minLength, int maxLength, StringArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema();
            a.setItem(new StringSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("String").length(minLength, maxLength).validator(validator).computable().optional(defaultValue);
            return false;
        }
        Objects.requireNonNull(holder, "string array holder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            holder.readJSON(optionalJsonElement, minLength, maxLength, defaultValue, validator, name, this.builderParameters);
            this.trackDynamicHolder(holder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    private void validateAndSet(String[] value, @Nullable StringArrayValidator validator, @Nonnull Consumer<String[]> setter, String name) {
        if (validator != null && !validator.test(value)) {
            throw new IllegalStateException(validator.errorMessage(name, value) + " in " + this.getBreadCrumbs());
        }
        setter.accept(value);
    }

    private String defaultArrayToString(@Nullable String[] defaultValue) {
        return defaultValue == null ? null : Arrays.toString(defaultValue);
    }

    private boolean requireOrGetDictionary(@Nonnull JsonElement data, String name, String domain, @Nonnull BiConsumer<String, JsonElement> setter, boolean required, BuilderDescriptorState state, String shortDescription, String longDescription) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Dictionary", state, shortDescription, longDescription).required().domain(domain);
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = required ? this.getRequiredJsonElement(data, name) : this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                JsonObject object = this.expectJsonObject(element, name);
                object.entrySet().forEach(stringJsonElementEntry -> setter.accept((String)stringJsonElementEntry.getKey(), (JsonElement)stringJsonElementEntry.getValue()));
            }
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireDictionary(@Nonnull JsonElement data, String name, String domain, @Nonnull BiConsumer<String, JsonElement> setter, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.requireOrGetDictionary(data, name, domain, setter, true, state, shortDescription, longDescription);
    }

    public boolean getDictionary(@Nonnull JsonElement data, String name, String domain, @Nonnull BiConsumer<String, JsonElement> setter, BuilderDescriptorState state, String shortDescription, String longDescription) {
        return this.requireOrGetDictionary(data, name, domain, setter, false, state, shortDescription, longDescription);
    }

    public void requireDouble(@Nonnull JsonElement data, String name, @Nonnull DoubleConsumer setter, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).required().validator(validator);
            return;
        }
        try {
            this.validateAndSet(this.expectDouble(this.getRequiredJsonElement(data, name), name), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getDouble(@Nonnull JsonElement data, String name, @Nonnull DoubleConsumer setter, double defaultValue, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).optional(Double.toString(defaultValue)).validator(validator);
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = this.expectDouble(element, name);
            }
            this.validateAndSet(defaultValue, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireDouble(@Nonnull JsonElement data, String name, @Nonnull DoubleHolder doubleHolder, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            doubleHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).required().computable().validator(validator);
            return;
        }
        Objects.requireNonNull(doubleHolder, "doubleHolder is null");
        try {
            doubleHolder.readJSON(this.getRequiredJsonElement(data, name), validator, name, this.builderParameters);
            this.trackDynamicHolder(doubleHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean requireDoubleIfNotOverridden(@Nonnull JsonElement data, String name, @Nonnull DoubleHolder doubleHolder, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            doubleHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).requiredIfNotOverridden().computable().validator(validator);
            return false;
        }
        Objects.requireNonNull(doubleHolder, "doubleHolder is null");
        try {
            JsonElement element = this.getRequiredJsonElementIfNotOverridden(data, name, ParameterType.DOUBLE);
            boolean valueProvided = element != null;
            doubleHolder.readJSON(element, -1.7976931348623157E308, !valueProvided ? null : validator, name, this.builderParameters);
            this.trackDynamicHolder(doubleHolder);
            return valueProvided;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public boolean getDouble(@Nonnull JsonElement data, String name, @Nonnull DoubleHolder doubleHolder, double defaultValue, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            doubleHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).optional(Double.toString(defaultValue)).computable().validator(validator);
            return false;
        }
        Objects.requireNonNull(doubleHolder, "doubleHolder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            doubleHolder.readJSON(optionalJsonElement, defaultValue, validator, name, this.builderParameters);
            this.trackDynamicHolder(doubleHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    private void validateAndSet(double v, @Nullable DoubleValidator validator, @Nonnull DoubleConsumer setter, String name) {
        if (validator != null && !validator.test(v)) {
            throw new IllegalStateException(validator.errorMessage(v, name) + " in " + this.getBreadCrumbs());
        }
        setter.accept(v);
    }

    public void requireIntArray(@Nonnull JsonElement data, String name, @Nonnull Consumer<int[]> setter, int minLength, int maxLength, IntArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema(new IntegerSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("integer").length(minLength, maxLength).validator(validator).required();
            return;
        }
        try {
            this.validateAndSet(this.expectIntArray(this.getRequiredJsonElement(data, name), name, minLength, maxLength), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getIntArray(@Nonnull JsonElement data, String name, @Nonnull Consumer<int[]> setter, int[] defaultValue, int minLength, int maxLength, IntArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema(new IntegerSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("integer").length(minLength, maxLength).validator(validator).optional(defaultValue);
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = this.expectIntArray(element, name, minLength, maxLength);
            }
            this.validateAndSet(defaultValue, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireIntArray(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, int minLength, int maxLength, IntArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema(new IntegerSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("integer").length(minLength, maxLength).validator(validator).computable().required();
            return;
        }
        Objects.requireNonNull(holder, "int array holder is null");
        try {
            holder.readJSON(this.getRequiredJsonElement(data, name), minLength, maxLength, validator, name, this.builderParameters);
            this.trackDynamicHolder(holder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getIntArray(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, int[] defaultValue, int minLength, int maxLength, IntArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema(new IntegerSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("integer").length(minLength, maxLength).validator(validator).computable().optional(defaultValue);
            return false;
        }
        Objects.requireNonNull(holder, "int array holder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            holder.readJSON(optionalJsonElement, minLength, maxLength, defaultValue, validator, name, this.builderParameters);
            this.trackDynamicHolder(holder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireIntRange(@Nonnull JsonElement data, String name, @Nonnull Consumer<int[]> setter, IntArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.requireIntArray(data, name, setter, 2, 2, validator, state, shortDescription, longDescription);
    }

    public boolean getIntRange(@Nonnull JsonElement data, String name, @Nonnull Consumer<int[]> setter, int[] defaultValue, IntArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        return this.getIntArray(data, name, setter, defaultValue, 2, 2, validator, state, shortDescription, longDescription);
    }

    public void requireIntRange(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, IntArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.requireIntArray(data, name, holder, 2, 2, validator, state, shortDescription, longDescription);
    }

    public boolean getIntRange(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, int[] defaultValue, IntArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        return this.getIntArray(data, name, holder, defaultValue, 2, 2, validator, state, shortDescription, longDescription);
    }

    private void validateAndSet(int[] v, @Nullable IntArrayValidator validator, @Nonnull Consumer<int[]> setter, String name) {
        if (validator != null && !validator.test(v)) {
            throw new IllegalStateException(validator.errorMessage(v, name) + " in " + this.getBreadCrumbs());
        }
        setter.accept(v);
    }

    public void requireDoubleArray(@Nonnull JsonElement data, String name, @Nonnull Consumer<double[]> setter, int minLength, int maxLength, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema(new NumberSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("Double").length(minLength, maxLength).validator(validator).required();
            return;
        }
        try {
            this.validateAndSet(this.expectDoubleArray(this.getRequiredJsonElement(data, name), name, minLength, maxLength), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getDoubleArray(@Nonnull JsonElement data, String name, @Nonnull Consumer<double[]> setter, double[] defaultValue, int minLength, int maxLength, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema(new NumberSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("Double").length(minLength, maxLength).validator(validator).optional(defaultValue);
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = this.expectDoubleArray(element, name, minLength, maxLength);
            }
            this.validateAndSet(defaultValue, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireDoubleArray(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, int minLength, int maxLength, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema(new NumberSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("Double").length(minLength, maxLength).validator(validator).computable().required();
            return;
        }
        Objects.requireNonNull(holder, "double array holder is null");
        try {
            holder.readJSON(this.getRequiredJsonElement(data, name), minLength, maxLength, validator, name, this.builderParameters);
            this.trackDynamicHolder(holder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getDoubleArray(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, double[] defaultValue, int minLength, int maxLength, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema(new NumberSchema());
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).domain("Double").length(minLength, maxLength).validator(validator).computable().optional(defaultValue);
            return false;
        }
        Objects.requireNonNull(holder, "double array holder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            holder.readJSON(optionalJsonElement, minLength, maxLength, defaultValue, validator, name, this.builderParameters);
            this.trackDynamicHolder(holder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireDoubleRange(@Nonnull JsonElement data, String name, @Nonnull Consumer<double[]> setter, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.requireDoubleArray(data, name, setter, 2, 2, validator, state, shortDescription, longDescription);
    }

    public boolean getDoubleRange(@Nonnull JsonElement data, String name, @Nonnull Consumer<double[]> setter, double[] defaultValue, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        return this.getDoubleArray(data, name, setter, defaultValue, 2, 2, validator, state, shortDescription, longDescription);
    }

    public void requireVector3d(@Nonnull JsonElement data, String name, @Nonnull Consumer<double[]> setter, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.requireDoubleArray(data, name, setter, 3, 3, validator, state, shortDescription, longDescription);
    }

    public boolean getVector3d(@Nonnull JsonElement data, String name, @Nonnull Consumer<double[]> setter, double[] defaultValue, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        return this.getDoubleArray(data, name, setter, defaultValue, 3, 3, validator, state, shortDescription, longDescription);
    }

    public void requireDoubleRange(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.requireDoubleArray(data, name, holder, 2, 2, validator, state, shortDescription, longDescription);
    }

    public boolean getDoubleRange(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, double[] defaultValue, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        return this.getDoubleArray(data, name, holder, defaultValue, 2, 2, validator, state, shortDescription, longDescription);
    }

    public void requireVector3d(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.requireDoubleArray(data, name, holder, 3, 3, validator, state, shortDescription, longDescription);
    }

    public boolean getVector3d(@Nonnull JsonElement data, String name, @Nonnull NumberArrayHolder holder, double[] defaultValue, DoubleArrayValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        return this.getDoubleArray(data, name, holder, defaultValue, 3, 3, validator, state, shortDescription, longDescription);
    }

    private void validateAndSet(double[] v, @Nullable DoubleArrayValidator validator, @Nonnull Consumer<double[]> setter, String name) {
        if (validator != null && !validator.test(v)) {
            throw new IllegalStateException(validator.errorMessage(v, name) + " in " + this.getBreadCrumbs());
        }
        setter.accept(v);
    }

    @Nonnull
    public static Vector3d createVector3d(@Nonnull double[] coordinates) {
        return new Vector3d(coordinates[0], coordinates[1], coordinates[2]);
    }

    public static Vector3d createVector3d(@Nullable double[] coordinates, @Nonnull Supplier<Vector3d> defaultSupplier) {
        return coordinates != null ? BuilderBase.createVector3d(coordinates) : defaultSupplier.get();
    }

    public void requireFloat(@Nonnull JsonElement data, String name, @Nonnull FloatConsumer setter, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).required().validator(validator);
            return;
        }
        try {
            this.validateAndSet((float)this.expectDouble(this.getRequiredJsonElement(data, name), name), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getFloat(@Nonnull JsonElement data, String name, @Nonnull FloatConsumer setter, float defaultValue, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).optional(Double.toString(defaultValue)).validator(validator);
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = (float)this.expectDouble(element, name);
            }
            this.validateAndSet(defaultValue, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireFloat(@Nonnull JsonElement data, String name, @Nonnull FloatHolder floatHolder, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            floatHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).required().computable().validator(validator);
            return;
        }
        Objects.requireNonNull(floatHolder, "floatHolder is null");
        try {
            floatHolder.readJSON(this.getRequiredJsonElement(data, name), validator, name, this.builderParameters);
            this.trackDynamicHolder(floatHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getFloat(@Nonnull JsonElement data, String name, @Nonnull FloatHolder floatHolder, double defaultValue, DoubleValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new NumberSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            floatHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Double.class.getSimpleName(), state, shortDescription, longDescription).optional(Double.toString(defaultValue)).computable().validator(validator);
            return false;
        }
        Objects.requireNonNull(floatHolder, "floatHolder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            floatHolder.readJSON(optionalJsonElement, defaultValue, validator, name, this.builderParameters);
            this.trackDynamicHolder(floatHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    private void validateAndSet(float v, @Nullable DoubleValidator validator, @Nonnull FloatConsumer setter, String name) {
        if (validator != null && !validator.test(v)) {
            throw new IllegalStateException(validator.errorMessage(v, name) + " in " + this.getBreadCrumbs());
        }
        setter.accept(v);
    }

    public void requireInt(@Nonnull JsonElement data, String name, @Nonnull IntConsumer setter, IntValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new IntegerSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, Integer.class.getSimpleName(), state, shortDescription, longDescription).required().validator(validator);
            return;
        }
        try {
            this.validateAndSet(this.expectInteger(this.getRequiredJsonElement(data, name), name), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getInt(@Nonnull JsonElement data, String name, @Nonnull IntConsumer setter, int defaultValue, IntValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new IntegerSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, Integer.class.getSimpleName(), state, shortDescription, longDescription).optional(Integer.toString(defaultValue)).validator(validator);
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = this.expectInteger(element, name);
            }
            this.validateAndSet(defaultValue, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireInt(@Nonnull JsonElement data, String name, @Nonnull IntHolder intHolder, IntValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new IntegerSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            intHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Integer.class.getSimpleName(), state, shortDescription, longDescription).required().computable().validator(validator);
            return;
        }
        Objects.requireNonNull(intHolder, "intHolder is null");
        try {
            intHolder.readJSON(this.getRequiredJsonElement(data, name), validator, name, this.builderParameters);
            this.trackDynamicHolder(intHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean requireIntIfNotOverridden(@Nonnull JsonElement data, String name, @Nonnull IntHolder intHolder, IntValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new IntegerSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            intHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Integer.class.getSimpleName(), state, shortDescription, longDescription).requiredIfNotOverridden().computable().validator(validator);
            return false;
        }
        Objects.requireNonNull(intHolder, "intHolder is null");
        try {
            JsonElement element = this.getRequiredJsonElementIfNotOverridden(data, name, ParameterType.INTEGER);
            boolean valueProvided = element != null;
            intHolder.readJSON(element, Integer.MIN_VALUE, !valueProvided ? null : validator, name, this.builderParameters);
            this.trackDynamicHolder(intHolder);
            return valueProvided;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public boolean getInt(@Nonnull JsonElement data, String name, @Nonnull IntHolder intHolder, int defaultValue, IntValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new IntegerSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            intHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Integer.class.getSimpleName(), state, shortDescription, longDescription).optional(Double.toString(defaultValue)).computable().validator(validator);
            return false;
        }
        try {
            Objects.requireNonNull(intHolder, "intHolder is null");
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            intHolder.readJSON(optionalJsonElement, defaultValue, validator, name, this.builderParameters);
            this.trackDynamicHolder(intHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    private void validateAndSet(int v, @Nullable IntValidator validator, @Nonnull IntConsumer setter, String name) {
        if (validator != null && !validator.test(v)) {
            throw new IllegalStateException(validator.errorMessage(v, name) + " in " + this.getBreadCrumbs());
        }
        setter.accept(v);
    }

    public void requireBoolean(@Nonnull JsonElement data, String name, @Nonnull BooleanHolder booleanHolder, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new BooleanSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            booleanHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Boolean.class.getSimpleName(), state, shortDescription, longDescription).required().computable();
            return;
        }
        Objects.requireNonNull(booleanHolder, "booleanHolder is null");
        try {
            booleanHolder.readJSON(this.getRequiredJsonElement(data, name), name, this.builderParameters);
            this.trackDynamicHolder(booleanHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getBoolean(@Nonnull JsonElement data, String name, @Nonnull BooleanHolder booleanHolder, boolean defaultValue, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new BooleanSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            booleanHolder.setName(name);
            this.builderDescriptor.addAttribute(name, Boolean.class.getSimpleName(), state, shortDescription, longDescription).optional(Boolean.toString(defaultValue)).computable();
            return false;
        }
        Objects.requireNonNull(booleanHolder, "booleanHolder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            booleanHolder.readJSON(optionalJsonElement, defaultValue, name, this.builderParameters);
            this.trackDynamicHolder(booleanHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireBoolean(@Nonnull JsonElement data, String name, @Nonnull BooleanConsumer setter, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new BooleanSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, Boolean.class.getSimpleName(), state, shortDescription, longDescription).required();
            return;
        }
        try {
            setter.accept(this.expectBoolean(this.getRequiredJsonElement(data, name), name));
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getBoolean(@Nonnull JsonElement data, String name, @Nonnull BooleanConsumer setter, boolean defaultValue, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = BuilderExpressionDynamic.computableSchema(new BooleanSchema());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, Boolean.class.getSimpleName(), state, shortDescription, longDescription).optional(Boolean.toString(defaultValue));
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = this.expectBoolean(element, name);
            }
            setter.accept(defaultValue);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void getParameterBlock(@Nonnull JsonElement data, BuilderDescriptorState state, String shortDescription, String longDescription) {
        if (this.isCreatingSchema()) {
            this.builderSchema.getProperties().put("Parameters", new ObjectSchema());
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute("Parameters", "Parameters", state, shortDescription, longDescription).optional("");
            return;
        }
        if (!data.isJsonObject()) {
            throw new IllegalStateException(String.format("Looking for parameter block in a JsonElement that isn't an object at %s", this.getBreadCrumbs()));
        }
        BuilderParameters builderParameters = new BuilderParameters(this.builderParameters);
        builderParameters.readJSON(data.getAsJsonObject(), this.stateHelper);
        builderParameters.validateNoDuplicateParameters(this.builderParameters);
        builderParameters.addParametersToScope();
        this.builderParameters = builderParameters;
        this.addQueryKey("Parameters");
    }

    public void cleanupParameters() {
        if (this.isCreatingDescriptor()) {
            return;
        }
        this.builderParameters.disposeCompileContext();
    }

    @Nonnull
    protected <E extends Enum<E>> E resolveValue(String txt, E[] enumConstants, String paramName) {
        try {
            return (E)BuilderBase.stringToEnum((String)txt, enumConstants, (String)paramName);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage() + " in " + this.getBreadCrumbs(), e);
        }
    }

    @Nonnull
    public static <E extends Enum<E>> E stringToEnum(@Nullable String value, E[] enumConstants, String ident) {
        if (value != null && !value.isBlank()) {
            String trimmed = value.trim();
            for (E E : enumConstants) {
                if (!((Enum)E).name().equalsIgnoreCase(trimmed)) continue;
                return E;
            }
        }
        throw new IllegalArgumentException(String.format("Enum value '%s' is '%s', must be one of %s", ident, value, BuilderBase.getDomain(enumConstants)));
    }

    @Nonnull
    public static <E extends Enum<E>> String getDomain(E[] enumConstants) {
        return Arrays.toString(enumConstants);
    }

    @Nonnull
    private static String formatEnumCamelCase(@Nonnull String name) {
        boolean isLower = Character.isLowerCase(name.charAt(0));
        if (name.chars().anyMatch(v -> Character.isLowerCase(v) != isLower)) {
            return name;
        }
        StringBuilder nameParts = new StringBuilder();
        for (String part : name.split("_")) {
            nameParts.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase()).append('_');
        }
        nameParts.deleteCharAt(nameParts.length() - 1);
        return nameParts.toString();
    }

    @Nonnull
    private static <E extends Enum<E>> String[] getEnumValues(@Nonnull Class<E> enumClass) {
        return (String[])Arrays.stream((Enum[])enumClass.getEnumConstants()).map(Enum::name).map(BuilderBase::formatEnumCamelCase).toArray(String[]::new);
    }

    public <E extends Enum<E>> void requireEnum(@Nonnull JsonElement data, String name, @Nonnull Consumer<E> setter, @Nonnull Class<E> clazz, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(s));
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Flag", state, shortDescription, longDescription).required().setEnum(clazz);
            return;
        }
        try {
            setter.accept(this.resolveValue(this.expectString(this.getRequiredJsonElement(data, name), name), (Enum[])clazz.getEnumConstants(), name));
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public <E extends Enum<E>> boolean getEnum(@Nonnull JsonElement data, String name, @Nonnull Consumer<E> setter, @Nonnull Class<E> clazz, @Nullable E defaultValue, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(s));
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Flag", state, shortDescription, longDescription).optional(defaultValue != null ? defaultValue.toString() : "<context>").setEnum(clazz);
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = this.resolveValue(this.expectString(element, name), (Enum[])clazz.getEnumConstants(), name);
            }
            setter.accept(defaultValue);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public <E extends Enum<E>> void requireEnum(@Nonnull JsonElement data, String name, @Nonnull EnumHolder<E> enumHolder, @Nonnull Class<E> clazz, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(s));
            return;
        }
        if (this.isCreatingDescriptor()) {
            enumHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "Flag", state, shortDescription, longDescription).required().computable().setEnum(clazz);
            return;
        }
        Objects.requireNonNull(enumHolder, "enumHolder is null");
        try {
            enumHolder.readJSON(this.getRequiredJsonElement(data, name), clazz, name, this.builderParameters);
            this.trackDynamicHolder(enumHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public <E extends Enum<E>> boolean getEnum(@Nonnull JsonElement data, String name, @Nonnull EnumHolder<E> enumHolder, @Nonnull Class<E> clazz, @Nonnull E defaultValue, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(s));
            return false;
        }
        if (this.isCreatingDescriptor()) {
            enumHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "Flag", state, shortDescription, longDescription).optional(defaultValue.toString()).computable().setEnum(clazz);
            return false;
        }
        Objects.requireNonNull(enumHolder, "enumHolder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            enumHolder.readJSON(optionalJsonElement, clazz, defaultValue, name, this.builderParameters);
            this.trackDynamicHolder(enumHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    @Nonnull
    public static <E extends Enum<E>> String[] enumSetToStrings(@Nonnull EnumSet<E> enumSet) {
        int count = 0;
        Iterator it = enumSet.iterator();
        while (it.hasNext()) {
            ++count;
            it.next();
        }
        if (count == 0) {
            return ArrayUtil.EMPTY_STRING_ARRAY;
        }
        String[] result = new String[count];
        it = enumSet.iterator();
        for (int i = 0; i < count; ++i) {
            result[i] = ((Enum)it.next()).toString();
        }
        return result;
    }

    @Nonnull
    public static <E extends Enum<E>> EnumSet<E> stringsToEnumSet(@Nullable String[] array, @Nonnull Class<E> clazz, E[] enumConstants, String ident) {
        EnumSet<Enum> value = EnumSet.noneOf(clazz);
        if (array == null) {
            return value;
        }
        for (String s : array) {
            value.add(BuilderBase.stringToEnum((String)s, enumConstants, (String)ident));
        }
        return value;
    }

    @Nonnull
    public static <E extends Enum<E>> E[] stringsToEnumArray(@Nullable String[] array, @Nonnull Class<E> clazz, E[] enumConstants, String ident) {
        if (array == null || array.length == 0) {
            return (Enum[])Array.newInstance(clazz, 0);
        }
        Enum[] value = (Enum[])Array.newInstance(clazz, array.length);
        for (int i = 0; i < array.length; ++i) {
            value[i] = BuilderBase.stringToEnum((String)array[i], enumConstants, (String)ident);
        }
        return value;
    }

    protected <E extends Enum<E>> void toSet(String name, @Nonnull Class<E> clazz, @Nonnull EnumSet<E> t, @Nonnull String elementAsString) {
        Enum[] enumConstants = (Enum[])clazz.getEnumConstants();
        for (String s : elementAsString.split(",")) {
            t.add(this.resolveValue(s.trim(), enumConstants, name));
        }
    }

    @Nonnull
    protected EnumSet<RoleDebugFlags> toDebugFlagSet(String name, @Nonnull String elementAsString) {
        try {
            return RoleDebugFlags.getFlags(PATTERN.split(elementAsString.trim()));
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage() + " in parameter " + name + " at " + this.getBreadCrumbs(), e);
        }
    }

    protected <E extends Enum<E>> void toSet(String name, @Nonnull Class<E> clazz, @Nonnull EnumSet<E> t, @Nonnull JsonArray jsonArray) {
        Enum[] enumConstants = (Enum[])clazz.getEnumConstants();
        for (JsonElement jsonElement : jsonArray) {
            t.add(this.resolveValue(this.expectString(jsonElement, name), enumConstants, name));
        }
    }

    protected <E extends Enum<E>> void toSet(String name, @Nonnull Class<E> clazz, @Nonnull EnumSet<E> t, @Nonnull JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            this.toSet(name, clazz, t, this.expectJsonArray(jsonElement, name));
            NPCPlugin.get().getLogger().at(Level.WARNING).log("Use of strings for enum sets is deprecated for JSON attribute '%s' (use []) in %s: %s", name, this.getBreadCrumbs(), this.builderParameters.getFileName());
        } else {
            this.toSet(name, clazz, t, this.expectString(jsonElement, name));
        }
    }

    public <E extends Enum<E>> void requireEnumArray(@Nonnull JsonElement data, String name, @Nonnull EnumArrayHolder<E> enumArrayHolderHolder, @Nonnull Class<E> clazz, EnumArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            ArraySchema a = new ArraySchema();
            a.setItem(s);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(a));
            return;
        }
        if (this.isCreatingDescriptor()) {
            enumArrayHolderHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "FlagArray", state, shortDescription, longDescription).required().computable().validator(validator).setEnum(clazz);
            return;
        }
        Objects.requireNonNull(enumArrayHolderHolder, "enumArrayHolder is null");
        try {
            enumArrayHolderHolder.readJSON(this.getRequiredJsonElement(data, name), clazz, validator, name, this.builderParameters);
            this.trackDynamicHolder(enumArrayHolderHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public <E extends Enum<E>> void requireEnumSet(@Nonnull JsonElement data, String name, @Nonnull Consumer<? super EnumSet<E>> setter, @Nonnull Class<E> clazz, @Nonnull Supplier<? extends EnumSet<E>> factory, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            ArraySchema a = new ArraySchema();
            a.setItem(s);
            a.setUniqueItems(true);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(a));
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "FlagSet", state, shortDescription, longDescription).required().setEnum(clazz);
            return;
        }
        try {
            EnumSet<E> t = factory.get();
            this.toSet(name, clazz, t, this.getRequiredJsonElement(data, name));
            setter.accept(t);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public <E extends Enum<E>> boolean getEnumSet(@Nonnull JsonElement data, String name, @Nonnull Consumer<? super EnumSet<E>> setter, @Nonnull Class<E> clazz, @Nonnull Supplier<? extends EnumSet<E>> factory, @Nonnull EnumSet<E> defaultValue, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            ArraySchema a = new ArraySchema();
            a.setItem(s);
            a.setUniqueItems(true);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(a));
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "FlagSet", state, shortDescription, longDescription).optional(defaultValue.toString()).setEnum(clazz);
            return false;
        }
        try {
            JsonElement element = this.getOptionalJsonElement(data, name);
            if (element != null) {
                EnumSet<E> t = factory.get();
                this.toSet(name, clazz, t, element);
                setter.accept(t);
                return true;
            }
            setter.accept(defaultValue);
        }
        catch (Exception e) {
            this.addError(e);
        }
        return false;
    }

    public <E extends Enum<E>> void requireEnumSet(@Nonnull JsonElement data, String name, @Nonnull EnumSetHolder<E> enumSetHolder, @Nonnull Class<E> clazz, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            ArraySchema a = new ArraySchema();
            a.setItem(s);
            a.setUniqueItems(true);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(a));
            return;
        }
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            ArraySchema a = new ArraySchema();
            a.setItem(s);
            a.setUniqueItems(true);
            this.builderSchema.getProperties().put(name, a);
            return;
        }
        if (this.isCreatingDescriptor()) {
            enumSetHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "FlagSet", state, shortDescription, longDescription).required().computable().setEnum(clazz);
            return;
        }
        Objects.requireNonNull(enumSetHolder, "enumSetHolder is null");
        try {
            enumSetHolder.readJSON(this.getRequiredJsonElement(data, name), clazz, name, this.builderParameters);
            this.trackDynamicHolder(enumSetHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public <E extends Enum<E>> boolean getEnumSet(@Nonnull JsonElement data, String name, @Nonnull EnumSetHolder<E> enumSetHolder, @Nonnull Class<E> clazz, @Nonnull EnumSet<E> defaultValue, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema s = new StringSchema();
            s.setEnum(BuilderBase.getEnumValues(clazz));
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            ArraySchema a = new ArraySchema();
            a.setItem(s);
            a.setUniqueItems(true);
            this.builderSchema.getProperties().put(name, BuilderExpressionDynamic.computableSchema(a));
            return false;
        }
        if (this.isCreatingDescriptor()) {
            enumSetHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "FlagSet", state, shortDescription, longDescription).optional(defaultValue.toString()).computable().setEnum(clazz);
            return false;
        }
        Objects.requireNonNull(enumSetHolder, "enumSetHolder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            enumSetHolder.readJSON(optionalJsonElement, defaultValue, clazz, name, this.builderParameters);
            this.trackDynamicHolder(enumSetHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    @Nonnull
    private Schema getObjectSchema(@Nonnull Class<?> classType) {
        BuilderFactory factory = this.builderManager.getFactory(classType);
        Schema subSchema = this.builderSchemaContext.refDefinition(factory);
        ObjectSchema ref = new ObjectSchema();
        ref.setProperties(new LinkedHashMap<String, Schema>());
        Map<String, Schema> props = ref.getProperties();
        props.put("Reference", BuilderExpressionDynamic.computableSchema(new StringSchema()));
        props.put("Local", BuilderExpressionDynamic.computableSchema(new BooleanSchema()));
        props.put("$Label", BuilderExpressionDynamic.computableSchema(new StringSchema()));
        props.put("Nullable", BuilderExpressionDynamic.computableSchema(new BooleanSchema()));
        props.put("Interfaces", BuilderExpressionDynamic.computableSchema(new ArraySchema(new StringSchema())));
        props.put("Modify", BuilderModifier.toSchema(this.builderSchemaContext));
        Schema comment = new Schema();
        comment.setDoNotSuggest(true);
        props.put("Comment", comment);
        props.put("$Title", comment);
        props.put("$Comment", comment);
        props.put("$TODO", comment);
        props.put("$Author", comment);
        props.put("$Position", comment);
        props.put("$FloatingFunctionNodes", comment);
        props.put("$Groups", comment);
        props.put("$WorkspaceID", comment);
        props.put("$NodeEditorMetadata", comment);
        props.put("$NodeId", comment);
        ref.setTitle("Object reference");
        ref.setRequired("Reference");
        ref.setAdditionalProperties(false);
        Schema cond = new Schema();
        ObjectSchema check = new ObjectSchema();
        check.setProperties(Map.of("Reference", BuilderExpressionDynamic.computableSchema(new StringSchema())));
        check.setRequired("Reference");
        cond.setIf(check);
        cond.setThen(ref);
        cond.setElse(subSchema);
        return BuilderExpressionDynamic.computableSchema(cond);
    }

    public boolean getObject(@Nonnull JsonElement data, String name, @Nonnull BuilderObjectReferenceHelper<?> builderObjectReferenceHelper, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription, @Nonnull BuilderValidationHelper builderValidationHelper) {
        if (this.isCreatingSchema()) {
            Schema s = this.getObjectSchema(builderObjectReferenceHelper.getClassType());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "ObjectRef", state, shortDescription, longDescription).domain(this.builderManager.getCategoryName(builderObjectReferenceHelper.getClassType())).optional((String)null);
            return false;
        }
        this.addQueryKey(name);
        try {
            JsonElement element = this.expectJsonObject(data, name).get(name);
            if (element != null) {
                builderObjectReferenceHelper.setLabel(name);
                this.extraInfo.pushKey(name);
                builderObjectReferenceHelper.readConfig(element, this.builderManager, this.builderParameters, builderValidationHelper);
                this.extraInfo.popKey();
                return true;
            }
        }
        catch (Exception e) {
            this.addError(e);
        }
        return false;
    }

    public void requireObject(@Nonnull JsonElement data, String name, @Nonnull BuilderObjectReferenceHelper<?> builderObjectReferenceHelper, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription, @Nonnull BuilderValidationHelper builderValidationHelper) {
        if (this.isCreatingSchema()) {
            Schema s = this.getObjectSchema(builderObjectReferenceHelper.getClassType());
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "ObjectRef", state, shortDescription, longDescription).domain(this.builderManager.getCategoryName(builderObjectReferenceHelper.getClassType())).required();
            return;
        }
        try {
            builderObjectReferenceHelper.setLabel(name);
            this.extraInfo.pushKey(name);
            builderObjectReferenceHelper.readConfig(this.getRequiredJsonElement(data, name), this.builderManager, this.builderParameters, builderValidationHelper);
            this.extraInfo.popKey();
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getCodecObject(@Nonnull JsonElement data, String name, @Nonnull BuilderCodecObjectHelper<?> helper, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        block6: {
            if (this.isCreatingSchema()) {
                Schema s = this.builderSchemaContext.refDefinition(helper.codec);
                s.setDescription(longDescription == null ? shortDescription : longDescription);
                this.builderSchema.getProperties().put(name, s);
                return false;
            }
            if (this.isCreatingDescriptor()) {
                this.builderDescriptor.addAttribute(name, "CodecObject", state, shortDescription, longDescription).domain(helper.getClassType().getSimpleName()).optional((String)null);
                return false;
            }
            this.addQueryKey(name);
            try {
                JsonElement element = this.expectJsonObject(data, name).get(name);
                if (element == null) break block6;
                this.extraInfo.pushKey(name);
                try {
                    helper.readConfig(element, this.extraInfo);
                }
                catch (Exception e) {
                    this.addError(e);
                }
                this.extraInfo.popKey();
                return true;
            }
            catch (Exception e) {
                this.addError(e);
            }
        }
        return false;
    }

    public void requireCodecObject(@Nonnull JsonElement data, String name, @Nonnull BuilderCodecObjectHelper<?> helper, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            Schema s = this.builderSchemaContext.refDefinition(helper.codec);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "CodecObject", state, shortDescription, longDescription).domain(helper.getClassType().getSimpleName()).required();
            return;
        }
        this.extraInfo.pushKey(name);
        try {
            helper.readConfig(this.getRequiredJsonElement(data, name), this.extraInfo);
        }
        catch (Exception e) {
            this.addError(e);
        }
        this.extraInfo.popKey();
    }

    public void requireEmbeddableArray(@Nonnull JsonElement data, String embedTag, @Nonnull BuilderObjectArrayHelper<?, ?> builderObjectArrayHelper, @Nonnull ArrayValidator arrayValidator, BuilderDescriptorState state, String shortDescription, String longDescription, @Nonnull BuilderValidationHelper builderValidationHelper) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(embedTag, "EmbeddableArray", state, shortDescription, longDescription).domain(this.builderManager.getCategoryName(builderObjectArrayHelper.getClassType())).validator(arrayValidator).required();
            return;
        }
        if (this.useDefaultsOnly) {
            throw new IllegalArgumentException("An embeddable array can be only used once!");
        }
        try {
            if (data.isJsonArray()) {
                builderObjectArrayHelper.readConfig(data, this.builderManager, this.builderParameters, builderValidationHelper);
                if (!arrayValidator.test(builderObjectArrayHelper)) {
                    throw new IllegalStateException(arrayValidator.errorMessage(builderObjectArrayHelper) + " at " + this.getBreadCrumbs());
                }
                this.useDefaultsOnly = true;
            } else {
                this.requireArray0(data, embedTag, builderObjectArrayHelper, arrayValidator, builderValidationHelper);
            }
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getArray(@Nonnull JsonElement data, String name, @Nonnull BuilderObjectArrayHelper<?, ?> builderObjectArrayHelper, ArrayValidator arrayValidator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription, @Nonnull BuilderValidationHelper builderValidationHelper) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema();
            Schema s = this.getObjectSchema(builderObjectArrayHelper.getClassType());
            a.setDescription(longDescription == null ? shortDescription : longDescription);
            a.setItem(s);
            this.builderSchema.getProperties().put(name, a);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).optional((String)null).validator(arrayValidator).domain(this.builderManager.getCategoryName(builderObjectArrayHelper.getClassType()));
            return false;
        }
        try {
            JsonElement element = this.getOptionalJsonElement(data, name);
            if (element != null) {
                this.requireArray0(element, name, builderObjectArrayHelper, arrayValidator, builderValidationHelper);
                return true;
            }
        }
        catch (Exception e) {
            this.addError(e);
        }
        return false;
    }

    public void requireArray(@Nonnull JsonElement data, String name, @Nonnull BuilderObjectArrayHelper<?, ?> builderObjectArrayHelper, ArrayValidator arrayValidator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription, @Nonnull BuilderValidationHelper builderValidationHelper) {
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema();
            Schema s = this.getObjectSchema(builderObjectArrayHelper.getClassType());
            a.setDescription(longDescription == null ? shortDescription : longDescription);
            a.setItem(s);
            this.builderSchema.getProperties().put(name, a);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Array", state, shortDescription, longDescription).required().validator(arrayValidator).domain(this.builderManager.getCategoryName(builderObjectArrayHelper.getClassType()));
            return;
        }
        try {
            this.requireArray0(this.getRequiredJsonElement(data, name), name, builderObjectArrayHelper, arrayValidator, builderValidationHelper);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    private void requireArray0(@Nonnull JsonElement data, String name, @Nonnull BuilderObjectArrayHelper<?, ?> builderObjectArrayHelper, @Nullable ArrayValidator validator, @Nonnull BuilderValidationHelper builderValidationHelper) {
        builderObjectArrayHelper.setLabel(name);
        this.extraInfo.pushKey(name);
        builderObjectArrayHelper.readConfig(data, this.builderManager, this.builderParameters, builderValidationHelper);
        this.extraInfo.popKey();
        if (validator != null && !validator.test(builderObjectArrayHelper)) {
            throw new IllegalStateException(validator.errorMessage(builderObjectArrayHelper) + " at " + this.getBreadCrumbs());
        }
    }

    public void requireArray(@Nonnull JsonElement data, @Nonnull BuilderObjectArrayHelper<?, ?> builderObjectArrayHelper, ArrayValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription, @Nonnull BuilderValidationHelper builderValidationHelper) {
        if (this.isCreatingSchema()) {
            this.builderSchemaRaw = new ArraySchema();
            Schema s = this.getObjectSchema(builderObjectArrayHelper.getClassType());
            this.builderSchemaRaw.setDescription(longDescription == null ? shortDescription : longDescription);
            ((ArraySchema)this.builderSchemaRaw).setItem(s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(null, "Array", state, shortDescription, longDescription).domain(this.builderManager.getCategoryName(builderObjectArrayHelper.getClassType())).validator(validator).required();
            return;
        }
        try {
            builderObjectArrayHelper.readConfig(data, this.builderManager, this.builderParameters, builderValidationHelper);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public void requireAsset(@Nonnull JsonElement data, String name, @Nonnull Consumer<String> setter, @Nonnull AssetValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema assetS = new StringSchema();
            validator.updateSchema(assetS);
            Schema s = BuilderExpressionDynamic.computableSchema(assetS);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Asset", state, shortDescription, longDescription).required().domain(validator.getDomain());
            return;
        }
        try {
            this.validateAndSet(this.expectString(this.getRequiredJsonElement(data, name), name), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getAsset(@Nonnull JsonElement data, String name, @Nonnull Consumer<String> setter, String defaultValue, @Nonnull AssetValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema assetS = new StringSchema();
            validator.updateSchema(assetS);
            Schema s = BuilderExpressionDynamic.computableSchema(assetS);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "Asset", state, shortDescription, longDescription).optional(defaultValue).domain(validator.getDomain());
            return false;
        }
        try {
            boolean haveValue;
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean bl = haveValue = element != null;
            if (haveValue) {
                defaultValue = this.expectString(element, name);
            }
            this.validateAndSet(defaultValue, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireAsset(@Nonnull JsonElement data, String name, @Nonnull AssetHolder assetHolder, @Nonnull AssetValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema assetS = new StringSchema();
            validator.updateSchema(assetS);
            Schema s = BuilderExpressionDynamic.computableSchema(assetS);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            assetHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "Asset", state, shortDescription, longDescription).required().computable().domain(validator.getDomain());
            return;
        }
        Objects.requireNonNull(assetHolder, "assetHolder is null");
        try {
            assetHolder.readJSON(this.getRequiredJsonElement(data, name), validator, name, this.builderParameters);
            assetHolder.staticValidate();
            this.trackDynamicHolder(assetHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public boolean getAsset(@Nonnull JsonElement data, String name, @Nonnull AssetHolder assetHolder, String defaultValue, @Nonnull AssetValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema assetS = new StringSchema();
            validator.updateSchema(assetS);
            Schema s = BuilderExpressionDynamic.computableSchema(assetS);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            assetHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "Asset", state, shortDescription, longDescription).optional(defaultValue).computable().domain(validator.getDomain());
            return false;
        }
        Objects.requireNonNull(assetHolder, "assetHolder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            assetHolder.readJSON(optionalJsonElement, defaultValue, validator, name, this.builderParameters);
            assetHolder.staticValidate();
            this.trackDynamicHolder(assetHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    private void validateAndSet(String str, @Nullable AssetValidator validator, @Nonnull Consumer<String> setter, String name) {
        if (validator != null) {
            this.validateSingleAsset(str, validator, name);
        }
        setter.accept(str);
    }

    public static boolean validateAssetList(@Nullable String[] assetList, @Nonnull AssetValidator validator, String attributeName, boolean testExistance) {
        if (assetList == null) {
            if (!validator.canListBeEmpty()) {
                throw new IllegalStateException("Null is not an allowed list of " + validator.getDomain() + " value for attribute \"" + attributeName + "\"");
            }
            return true;
        }
        if (assetList.length == 0) {
            if (!validator.canListBeEmpty()) {
                throw new IllegalStateException("List of " + validator.getDomain() + " value for attribute \"" + attributeName + "\" must not be empty");
            }
            return true;
        }
        for (String asset : assetList) {
            BuilderBase.validateAsset(asset, validator, attributeName, testExistance);
        }
        return true;
    }

    public static boolean validateAsset(@Nullable String assetName, @Nonnull AssetValidator validator, String attributeName, boolean testExistance) {
        if (assetName == null) {
            if (!validator.isNullable()) {
                throw new SkipSentryException(new IllegalStateException("Null is not an allowed " + validator.getDomain() + " value for attribute \"" + attributeName + "\""));
            }
            return true;
        }
        if (assetName.isEmpty()) {
            if (!validator.canBeEmpty()) {
                throw new SkipSentryException(new IllegalStateException("Empty string is not an allowed " + validator.getDomain() + " value for attribute \"" + attributeName + "\""));
            }
            return true;
        }
        if (!testExistance) {
            return true;
        }
        if (validator.isMatcher() && StringUtil.isGlobPattern(assetName)) {
            return true;
        }
        if (!validator.test(assetName)) {
            throw new SkipSentryException(new IllegalStateException(validator.errorMessage(assetName, attributeName)));
        }
        return true;
    }

    private void validateSingleAsset(String assetName, @Nonnull AssetValidator validator, String attributeName) {
        try {
            if (!BuilderBase.validateAsset(assetName, validator, attributeName, true)) {
                NPCPlugin.get().getLogger().at(Level.WARNING).log("%s in %s: %s", validator.errorMessage(assetName, attributeName), this.getBreadCrumbs(), this.builderParameters.getFileName());
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage() + " in " + this.getBreadCrumbs());
        }
    }

    public boolean getAssetArray(@Nonnull JsonElement data, String name, @Nonnull Consumer<String[]> setter, Function<String, String> mapper, String[] defaultValue, @Nonnull AssetValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema assetS = new StringSchema();
            validator.updateSchema(assetS);
            ArraySchema a = new ArraySchema(assetS);
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "AssetArray", state, shortDescription, longDescription).optional(this.defaultArrayToString(defaultValue)).domain(validator.getDomain());
            return false;
        }
        try {
            JsonElement element = this.getOptionalJsonElement(data, name);
            boolean haveValue = element != null;
            String[] list = haveValue ? this.expectStringArray(element, mapper, name) : defaultValue;
            this.validateAndSet(list, validator, setter, name);
            return haveValue;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public boolean getAssetArray(@Nonnull JsonElement data, String name, @Nonnull AssetArrayHolder assetHolder, String[] defaultValue, int minLength, int maxLength, @Nonnull AssetValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema assetS = new StringSchema();
            validator.updateSchema(assetS);
            ArraySchema a = new ArraySchema(assetS);
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            assetHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "AssetArray", state, shortDescription, longDescription).optional(this.defaultArrayToString(defaultValue)).computable().domain(validator.getDomain());
            return false;
        }
        Objects.requireNonNull(assetHolder, "assetHolder is null");
        try {
            JsonElement optionalJsonElement = this.getOptionalJsonElement(data, name);
            assetHolder.readJSON(optionalJsonElement, minLength, maxLength, defaultValue, validator, name, this.builderParameters);
            assetHolder.staticValidate();
            this.trackDynamicHolder(assetHolder);
            return optionalJsonElement != null;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    public void requireAssetArray(@Nonnull JsonElement data, String name, @Nonnull Consumer<String[]> setter, Function<String, String> mapper, @Nonnull AssetValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema assetS = new StringSchema();
            validator.updateSchema(assetS);
            ArraySchema a = new ArraySchema(assetS);
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "AssetArray", state, shortDescription, longDescription).required().domain(validator.getDomain());
            return;
        }
        try {
            JsonElement element = this.getRequiredJsonElement(data, name);
            this.validateAndSet(this.expectStringArray(element, mapper, name), validator, setter, name);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    public void requireAssetArray(@Nonnull JsonElement data, String name, @Nonnull AssetArrayHolder assetHolder, int minLength, int maxLength, @Nonnull AssetValidator validator, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        if (this.isCreatingSchema()) {
            StringSchema assetS = new StringSchema();
            validator.updateSchema(assetS);
            ArraySchema a = new ArraySchema(assetS);
            if (minLength != 0) {
                a.setMinItems(minLength);
            }
            if (maxLength != Integer.MAX_VALUE) {
                a.setMaxItems(maxLength);
            }
            Schema s = BuilderExpressionDynamic.computableSchema(a);
            s.setTitle(name);
            s.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s);
            return;
        }
        if (this.isCreatingDescriptor()) {
            assetHolder.setName(name);
            this.builderDescriptor.addAttribute(name, "AssetArray", state, shortDescription, longDescription).required().computable().domain(validator.getDomain());
            return;
        }
        Objects.requireNonNull(assetHolder, "assetHolder is null");
        try {
            assetHolder.readJSON(this.getRequiredJsonElement(data, name), minLength, maxLength, validator, name, this.builderParameters);
            assetHolder.staticValidate();
            this.trackDynamicHolder(assetHolder);
        }
        catch (Exception e) {
            this.addError(e);
        }
    }

    private void validateAndSet(@Nullable String[] assetList, @Nullable AssetValidator validator, @Nonnull Consumer<String[]> setter, String attributeName) {
        if (validator != null) {
            if (assetList == null) {
                if (!validator.canListBeEmpty()) {
                    throw new IllegalStateException("Null is not an allowed list of " + validator.getDomain() + " value for attribute \"" + attributeName + "\" in " + this.getBreadCrumbs());
                }
            } else if (assetList.length == 0) {
                if (!validator.canListBeEmpty()) {
                    throw new IllegalStateException("List of " + validator.getDomain() + " value for attribute \"" + attributeName + "\" must not be empty in " + this.getBreadCrumbs());
                }
            } else {
                for (String asset : assetList) {
                    this.validateSingleAsset(asset, validator, attributeName);
                }
            }
        }
        setter.accept(assetList);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected BuilderDescriptor createDescriptor(@Nonnull Builder<?> builder, String builderName, String categoryName, BuilderManager builderManager, BuilderDescriptorState state, String shortDescription, String longDescription, Set<String> tags) {
        this.builderDescriptor = new BuilderDescriptor(builderName, categoryName, shortDescription, longDescription, tags, state);
        try {
            builder.readConfig(null, null, builderManager, this.builderParameters, null);
            BuilderDescriptor builderDescriptor = this.builderDescriptor;
            return builderDescriptor;
        }
        finally {
            this.builderDescriptor = null;
        }
    }

    protected boolean isCreatingDescriptor() {
        return this.builderDescriptor != null;
    }

    protected boolean isCreatingSchema() {
        return this.builderSchema != null;
    }

    @Override
    @Nonnull
    public String getSchemaName() {
        if (this.typeName == null) {
            return "NPC:Class:" + this.getClass().getSimpleName();
        }
        return "NPC:" + this.typeName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        try {
            this.builderSchemaContext = context;
            this.builderSchema = new ObjectSchema();
            this.builderSchemaRaw = this.builderSchema;
            this.builderSchema.setProperties(new LinkedHashMap<String, Schema>());
            this.builderSchema.setAdditionalProperties(false);
            this.builderDescriptor = new BuilderDescriptor(null, null, null, null, null, null);
            try {
                this.readConfig(null, null, BuilderManager.SCHEMA_BUILDER_MANAGER, this.builderParameters, null);
            }
            finally {
                this.builderDescriptor = null;
            }
            Schema comment = new Schema();
            comment.setDoNotSuggest(true);
            this.builderSchema.getProperties().put("Comment", comment);
            this.builderSchema.getProperties().put("$Title", comment);
            this.builderSchema.getProperties().put("$Comment", comment);
            this.builderSchema.getProperties().put("$TODO", comment);
            this.builderSchema.getProperties().put("$Author", comment);
            this.builderSchema.getProperties().put("$Position", comment);
            this.builderSchema.getProperties().put("$FloatingFunctionNodes", comment);
            this.builderSchema.getProperties().put("$Groups", comment);
            this.builderSchema.getProperties().put("$WorkspaceID", comment);
            this.builderSchema.getProperties().put("$NodeEditorMetadata", comment);
            this.builderSchema.getProperties().put("$NodeId", comment);
            this.builderSchemaRaw.setTitle(this.typeName);
            this.builderSchemaRaw.setDescription(this.getLongDescription());
            Schema schema = this.builderSchemaRaw;
            return schema;
        }
        finally {
            this.builderSchema = null;
            this.builderSchemaContext = null;
        }
    }

    @Override
    public final BuilderDescriptor getDescriptor(String builderName, String categoryName, BuilderManager builderManager) {
        HashSet<String> tags = new HashSet<String>();
        this.registerTags(tags);
        return this.createDescriptor(this, builderName, categoryName, builderManager, this.getBuilderDescriptorState(), this.getShortDescription(), this.getLongDescription(), tags);
    }

    @Nullable
    public abstract String getShortDescription();

    @Nullable
    public abstract String getLongDescription();

    public void registerTags(@Nonnull Set<String> tags) {
        String pkg = this.getClass().getPackageName().replaceFirst("\\.builders$", "");
        tags.add(pkg.substring(pkg.lastIndexOf(46) + 1));
    }

    @Override
    @Nullable
    public abstract BuilderDescriptorState getBuilderDescriptorState();

    protected void validateNotAllStringsEmpty(String attribute1, String string1, String attribute2, String string2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(StringsNotEmptyValidator.withAttributes(attribute1, attribute2));
            return;
        }
        if (!StringsNotEmptyValidator.test(string1, string2)) {
            this.addError(StringsNotEmptyValidator.errorMessage(string1, attribute1, string2, attribute2, this.getBreadCrumbs()));
        }
    }

    protected void validateAtMostOneString(String attribute1, String string1, String attribute2, String string2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(StringsAtMostOneValidator.withAttributes(attribute1, attribute2));
            return;
        }
        if (StringsAtMostOneValidator.test(string1, string2)) {
            this.addError(StringsAtMostOneValidator.errorMessage(string1, attribute1, string2, attribute2, this.getBreadCrumbs()));
        }
    }

    protected void validateOneSetString(String attribute1, String string1, String attribute2, String string2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(StringsOneSetValidator.withAttributes(attribute1, attribute2));
            return;
        }
        if (!StringsOneSetValidator.test(string1, string2)) {
            this.addError(StringsOneSetValidator.formatErrorMessage(string1, attribute1, string2, attribute2, this.getBreadCrumbs()));
        }
    }

    protected void validateOneSetAsset(@Nonnull AssetHolder value1, String attribute2, String string2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(StringsOneSetValidator.withAttributes(value1.getName(), string2));
            return;
        }
        if (value1.isStatic()) {
            this.validateOneSetString(value1.getName(), value1.get(null), attribute2, string2);
            return;
        }
        value1.addRelationValidator((executionContext, v1) -> {
            if (!StringsOneSetValidator.test(v1, string2)) {
                throw new IllegalStateException(StringsOneSetValidator.formatErrorMessage(v1, value1.getName(), string2, attribute2, this.getBreadCrumbs()));
            }
        });
    }

    protected void validateOneSetAsset(@Nonnull AssetHolder value1, @Nonnull AssetHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(StringsOneSetValidator.withAttributes(value1.getName(), value2.getName()));
            return;
        }
        if (value1.isStatic()) {
            this.validateOneSetAsset(value2, value1.getName(), value1.get(null));
            return;
        }
        if (value2.isStatic()) {
            this.validateOneSetAsset(value1, value2.getName(), value2.get(null));
            return;
        }
        value1.addRelationValidator((executionContext, v1) -> {
            String s2 = value2.rawGet((ExecutionContext)executionContext);
            if (!StringsOneSetValidator.test(v1, s2)) {
                throw new IllegalStateException(StringsOneSetValidator.formatErrorMessage(v1, value1.getName(), s2, value2.getName(), this.getBreadCrumbs()));
            }
        });
        value2.addRelationValidator((executionContext, v2) -> {
            String s1 = value1.rawGet((ExecutionContext)executionContext);
            if (!StringsOneSetValidator.test(s1, v2)) {
                throw new IllegalStateException(StringsOneSetValidator.formatErrorMessage(s1, value1.getName(), v2, value2.getName(), this.getBreadCrumbs()));
            }
        });
    }

    protected void validateOneSetAssetArray(@Nonnull AssetArrayHolder value1, String attribute2, String[] value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(ArraysOneSetValidator.withAttributes(value1.getName(), attribute2));
            return;
        }
        if (value1.isStatic()) {
            if (!ArraysOneSetValidator.validate(value1.get(null), value2)) {
                this.addError(ArraysOneSetValidator.formatErrorMessage(value1.getName(), attribute2, this.getBreadCrumbs()));
            }
            return;
        }
        value1.addRelationValidator((executionContext, v1) -> {
            if (!ArraysOneSetValidator.validate(v1, value2)) {
                throw new IllegalStateException(ArraysOneSetValidator.formatErrorMessage(value1.getName(), attribute2, this.getBreadCrumbs()));
            }
        });
    }

    protected void validateOneSetAssetArray(@Nonnull AssetArrayHolder value1, @Nonnull AssetArrayHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(ArraysOneSetValidator.withAttributes(value1.getName(), value2.getName()));
            return;
        }
        if (value1.isStatic()) {
            this.validateOneSetAssetArray(value2, value1.getName(), value1.get(null));
            return;
        }
        if (value2.isStatic()) {
            this.validateOneSetAssetArray(value1, value2.getName(), value2.get(null));
            return;
        }
        value1.addRelationValidator((executionContext, v1) -> {
            String[] s2 = value2.rawGet((ExecutionContext)executionContext);
            if (!ArraysOneSetValidator.validate(v1, s2)) {
                throw new IllegalStateException(ArraysOneSetValidator.formatErrorMessage(value1.getName(), value2.getName(), this.getBreadCrumbs()));
            }
        });
        value2.addRelationValidator((executionContext, v2) -> {
            String[] s1 = value1.rawGet((ExecutionContext)executionContext);
            if (!ArraysOneSetValidator.validate(s1, v2)) {
                throw new IllegalStateException(ArraysOneSetValidator.formatErrorMessage(value1.getName(), value2.getName(), this.getBreadCrumbs()));
            }
        });
    }

    protected <K> void validateNoDuplicates(Iterable<K> list, String variableName) {
        NoDuplicatesValidator<K> validator = NoDuplicatesValidator.withAttributes(list, variableName);
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(validator);
            return;
        }
        if (!validator.test()) {
            this.addError(validator.errorMessage());
        }
    }

    protected void validateDoubleRelation(String attribute1, double value1, @Nonnull RelationalOperator relation, String attribute2, double value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(attribute1, relation, attribute2));
            return;
        }
        if (!DoubleValidator.compare(value1, relation, value2)) {
            this.addError(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", attribute1, value1, relation.asText(), attribute2, value2, this.getBreadCrumbs()));
        }
    }

    protected void validateDoubleRelation(@Nonnull DoubleHolder value1, @Nonnull RelationalOperator relation, String attribute2, double value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(value1.getName(), relation, attribute2));
            return;
        }
        if (value1.isStatic()) {
            this.validateDoubleRelation(value1.getName(), value1.get(null), relation, attribute2, value2);
        } else {
            value1.addRelationValidator((executionContext, v1) -> {
                if (!DoubleValidator.compare(v1, relation, value2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), attribute2, value2, this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateDoubleRelation(String attribute1, double value1, @Nonnull RelationalOperator relation, @Nonnull DoubleHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(attribute1, relation, value2.getName()));
            return;
        }
        if (value2.isStatic()) {
            this.validateDoubleRelation(attribute1, value1, relation, value2.getName(), value2.get(null));
        } else {
            value2.addRelationValidator((executionContext, v2) -> {
                if (!DoubleValidator.compare(value1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", attribute1, value1, relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateDoubleRelation(@Nonnull DoubleHolder value1, @Nonnull RelationalOperator relation, @Nonnull DoubleHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(value1.getName(), relation, value2.getName()));
            return;
        }
        if (value1.isStatic()) {
            this.validateDoubleRelation(value1.getName(), value1.get(null), relation, value2);
        } else if (value2.isStatic()) {
            this.validateDoubleRelation(value1, relation, value2.getName(), value2.get(null));
        } else {
            value1.addRelationValidator((executionContext1, v1) -> {
                double v2 = value2.rawGet((ExecutionContext)executionContext1);
                if (!DoubleValidator.compare(v1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
            value2.addRelationValidator((executionContext, v2) -> {
                double v1 = value1.rawGet((ExecutionContext)executionContext);
                if (!DoubleValidator.compare(v1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateFloatRelation(String attribute1, float value1, @Nonnull RelationalOperator relation, String attribute2, float value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(attribute1, relation, attribute2));
            return;
        }
        if (!DoubleValidator.compare(value1, relation, value2)) {
            this.addError(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", attribute1, Float.valueOf(value1), relation.asText(), attribute2, Float.valueOf(value2), this.getBreadCrumbs()));
        }
    }

    protected void validateFloatRelation(@Nonnull FloatHolder value1, @Nonnull RelationalOperator relation, String attribute2, float value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(value1.getName(), relation, attribute2));
            return;
        }
        if (value1.isStatic()) {
            this.validateFloatRelation(value1.getName(), value1.get(null), relation, attribute2, value2);
        } else {
            value1.addRelationValidator((executionContext, v1) -> {
                if (!DoubleValidator.compare(v1, relation, value2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), attribute2, Float.valueOf(value2), this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateFloatRelation(String attribute1, float value1, @Nonnull RelationalOperator relation, @Nonnull FloatHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(attribute1, relation, value2.getName()));
            return;
        }
        if (value2.isStatic()) {
            this.validateFloatRelation(attribute1, value1, relation, value2.getName(), value2.get(null));
        } else {
            value2.addRelationValidator((executionContext, v2) -> {
                if (!DoubleValidator.compare(value1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", attribute1, Float.valueOf(value1), relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateFloatRelation(@Nonnull FloatHolder value1, @Nonnull RelationalOperator relation, @Nonnull FloatHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(value1.getName(), relation, value2.getName()));
            return;
        }
        if (value1.isStatic()) {
            this.validateFloatRelation(value1.getName(), value1.get(null), relation, value2);
        } else if (value2.isStatic()) {
            this.validateFloatRelation(value1, relation, value2.getName(), value2.get(null));
        } else {
            value1.addRelationValidator((executionContext1, v1) -> {
                double v2 = value2.rawGet((ExecutionContext)executionContext1);
                if (!DoubleValidator.compare(v1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
            value2.addRelationValidator((executionContext, v2) -> {
                double v1 = value1.rawGet((ExecutionContext)executionContext);
                if (!DoubleValidator.compare(v1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateIntRelation(String attribute1, int value1, @Nonnull RelationalOperator relation, String attribute2, int value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(attribute1, relation, attribute2));
            return;
        }
        if (!IntValidator.compare(value1, relation, value2)) {
            this.addError(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", attribute1, value1, relation.asText(), attribute2, value2, this.getBreadCrumbs()));
        }
    }

    protected void validateIntRelation(@Nonnull IntHolder value1, @Nonnull RelationalOperator relation, String attribute2, int value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(value1.getName(), relation, attribute2));
            return;
        }
        if (value1.isStatic()) {
            this.validateIntRelation(value1.getName(), value1.get(null), relation, attribute2, value2);
        } else {
            value1.addRelationValidator((executionContext, v1) -> {
                if (!IntValidator.compare(v1, relation, value2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), attribute2, value2, this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateIntRelation(String attribute1, int value1, @Nonnull RelationalOperator relation, @Nonnull IntHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(attribute1, relation, value2.getName()));
            return;
        }
        if (value2.isStatic()) {
            this.validateIntRelation(attribute1, value1, relation, value2.getName(), value2.get(null));
        } else {
            value2.addRelationValidator((executionContext, v2) -> {
                if (!IntValidator.compare(value1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", attribute1, value1, relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateIntRelation(@Nonnull IntHolder value1, @Nonnull RelationalOperator relation, @Nonnull IntHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(value1.getName(), relation, value2.getName()));
            return;
        }
        if (value1.isStatic()) {
            this.validateIntRelation(value1.getName(), value1.get(null), relation, value2);
        } else if (value2.isStatic()) {
            this.validateIntRelation(value1, relation, value2.getName(), value2.get(null));
        } else {
            value1.addRelationValidator((executionContext1, v1) -> {
                int v2 = value2.rawGet((ExecutionContext)executionContext1);
                if (!IntValidator.compare(v1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
            value2.addRelationValidator((executionContext, v2) -> {
                int v1 = value1.rawGet((ExecutionContext)executionContext);
                if (!IntValidator.compare(v1, relation, v2)) {
                    throw new IllegalStateException(String.format("'%s'(=%s) should be %s '%s'(=%s) in %s", value1.getName(), v1, relation.asText(), value2.getName(), v2, this.getBreadCrumbs()));
                }
            });
        }
    }

    protected void validateIntRelationIfBooleanIs(boolean targetValue, boolean actualValue, @Nonnull IntHolder value1, @Nonnull RelationalOperator relation, @Nonnull IntHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AttributeRelationValidator.withAttributes(value1.getName(), relation, value2.getName()));
            return;
        }
        if (actualValue != targetValue) {
            return;
        }
        this.validateIntRelation(value1, relation, value2);
    }

    protected void validateAnyPresent(String attribute1, @Nonnull BuilderObjectHelper<?> objectHelper1, String attribute2, @Nonnull BuilderObjectHelper<?> objectHelper2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AnyPresentValidator.withAttributes(attribute1, attribute2));
            return;
        }
        if (objectHelper1.isPresent() || objectHelper2.isPresent()) {
            return;
        }
        this.addError(AnyPresentValidator.errorMessage(new String[]{attribute1, attribute2}) + " in " + this.getBreadCrumbs());
    }

    protected void validateAnyPresent(String attribute1, @Nonnull BuilderObjectHelper<?> objectHelper1, String attribute2, @Nonnull BuilderObjectHelper<?> objectHelper2, String attribute3, @Nonnull BuilderObjectHelper<?> objectHelper3) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AnyPresentValidator.withAttributes(new String[]{attribute1, attribute2, attribute3}));
            return;
        }
        if (objectHelper1.isPresent() || objectHelper2.isPresent() || objectHelper3.isPresent()) {
            return;
        }
        this.addError(AnyPresentValidator.errorMessage(new String[]{attribute1, attribute2, attribute3}) + " in " + this.getBreadCrumbs());
    }

    protected void validateAnyPresent(@Nonnull String[] attributes, @Nonnull BuilderObjectHelper<?>[] objectHelpers) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AnyPresentValidator.withAttributes(attributes));
            return;
        }
        if (AnyPresentValidator.test(objectHelpers)) {
            return;
        }
        this.addError(AnyPresentValidator.errorMessage(attributes) + " in " + this.getBreadCrumbs());
    }

    protected void validateOnePresent(String attribute1, @Nonnull BuilderObjectHelper<?> objectHelper1, String attribute2, @Nonnull BuilderObjectHelper<?> objectHelper2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(OnePresentValidator.withAttributes(attribute1, attribute2));
            return;
        }
        if (OnePresentValidator.test(objectHelper1, objectHelper2)) {
            return;
        }
        this.addError(OnePresentValidator.errorMessage(new String[]{attribute1, attribute2}, new BuilderObjectHelper[]{objectHelper1, objectHelper2}) + " in " + this.getBreadCrumbs());
    }

    protected void validateOnePresent(String attribute1, @Nonnull BuilderObjectHelper<?> objectHelper1, String attribute2, @Nonnull BuilderObjectHelper<?> objectHelper2, String attribute3, @Nonnull BuilderObjectHelper<?> objectHelper3) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(OnePresentValidator.withAttributes(attribute1, attribute2, attribute3));
            return;
        }
        if (OnePresentValidator.test(objectHelper1, objectHelper2, objectHelper3)) {
            return;
        }
        this.addError(OnePresentValidator.errorMessage(new String[]{attribute1, attribute2, attribute3}, new BuilderObjectHelper[]{objectHelper1, objectHelper2, objectHelper3}) + " in " + this.getBreadCrumbs());
    }

    protected void validateOnePresent(@Nonnull String[] attributes, @Nonnull BuilderObjectHelper<?>[] objectHelpers) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(OnePresentValidator.withAttributes(attributes));
            return;
        }
        if (OnePresentValidator.test(objectHelpers)) {
            return;
        }
        this.addError(OnePresentValidator.errorMessage(attributes, objectHelpers) + " in " + this.getBreadCrumbs());
    }

    protected void validateOnePresent(@Nonnull String[] attributes, @Nonnull boolean[] readStatus) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(OnePresentValidator.withAttributes(attributes));
            return;
        }
        if (OnePresentValidator.test(readStatus)) {
            return;
        }
        this.addError(OnePresentValidator.errorMessage(attributes, readStatus) + " in " + this.getBreadCrumbs());
    }

    protected void validateOneOrNonePresent(String attribute1, @Nonnull BuilderObjectHelper<?> objectHelper1, String attribute2, @Nonnull BuilderObjectHelper<?> objectHelper2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(OneOrNonePresentValidator.withAttributes(attribute1, attribute2));
            return;
        }
        if (OneOrNonePresentValidator.test(objectHelper1, objectHelper2)) {
            return;
        }
        this.addError(OneOrNonePresentValidator.errorMessage(new String[]{attribute1, attribute2}, new BuilderObjectHelper[]{objectHelper1, objectHelper2}) + " in " + this.getBreadCrumbs());
    }

    protected void validateOneOrNonePresent(String attribute1, @Nonnull BuilderObjectHelper<?> objectHelper1, String attribute2, @Nonnull BuilderObjectHelper<?> objectHelper2, String attribute3, @Nonnull BuilderObjectHelper<?> objectHelper3) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(OneOrNonePresentValidator.withAttributes(attribute1, attribute2, attribute3));
            return;
        }
        if (OneOrNonePresentValidator.test(objectHelper1, objectHelper2, objectHelper3)) {
            return;
        }
        this.addError(OneOrNonePresentValidator.errorMessage(new String[]{attribute1, attribute2, attribute3}, new BuilderObjectHelper[]{objectHelper1, objectHelper2, objectHelper3}) + " in " + this.getBreadCrumbs());
    }

    protected void validateOneOrNonePresent(@Nonnull String[] attributes, @Nonnull BuilderObjectHelper<?>[] objectHelpers) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(OneOrNonePresentValidator.withAttributes(attributes));
            return;
        }
        if (OneOrNonePresentValidator.test(objectHelpers)) {
            return;
        }
        this.addError(OneOrNonePresentValidator.errorMessage(attributes, objectHelpers) + " in " + this.getBreadCrumbs());
    }

    protected void validateOneOrNonePresent(@Nonnull String[] attributes, @Nonnull boolean[] readStatus) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(OneOrNonePresentValidator.withAttributes(attributes));
            return;
        }
        if (OneOrNonePresentValidator.test(readStatus)) {
            return;
        }
        this.addError(OneOrNonePresentValidator.errorMessage(attributes, readStatus) + " in " + this.getBreadCrumbs());
    }

    protected void validateExistsIfParameterSet(String parameter, boolean value, String attribute, @Nonnull BuilderObjectHelper<?> objectHelper) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(ExistsIfParameterSetValidator.withAttributes(parameter, attribute));
            return;
        }
        if (!value) {
            return;
        }
        if (objectHelper.isPresent()) {
            return;
        }
        this.addError(ExistsIfParameterSetValidator.errorMessage(parameter, attribute) + " in " + this.getBreadCrumbs());
    }

    protected <E extends Enum<E>> void validateStringIfEnumIs(@Nonnull StringHolder parameter, @Nonnull StringValidator validator, @Nonnull EnumHolder<E> enumParameter, E targetValue) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(ValidateIfEnumIsValidator.withAttributes(parameter.getName(), validator, enumParameter.getName(), targetValue));
            return;
        }
        if (enumParameter.isStatic()) {
            this.validateStringIfEnumIs(parameter, validator, enumParameter.getName(), enumParameter.get(null), targetValue);
            return;
        }
        parameter.addRelationValidator((context, s) -> {
            Object enumValue = enumParameter.rawGet((ExecutionContext)context);
            if (enumValue != targetValue) {
                return;
            }
            if (!validator.test((String)s)) {
                throw new IllegalStateException(validator.errorMessage((String)s, parameter.getName()) + " if " + enumParameter.getName() + " is " + String.valueOf(targetValue) + " in " + this.getBreadCrumbs());
            }
        });
        enumParameter.addEnumRelationValidator((context, e) -> {
            if (e != targetValue) {
                return;
            }
            String stringValue = parameter.rawGet((ExecutionContext)context);
            if (!validator.test(stringValue)) {
                throw new IllegalStateException(validator.errorMessage(stringValue, parameter.getName()) + " if " + enumParameter.getName() + " is " + String.valueOf(targetValue) + " in " + this.getBreadCrumbs());
            }
        });
    }

    protected <E extends Enum<E>> void validateStringIfEnumIs(@Nonnull StringHolder parameter, @Nonnull StringValidator validator, String enumName, E targetValue, E enumValue) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(ValidateIfEnumIsValidator.withAttributes(parameter.getName(), validator, enumName, targetValue));
            return;
        }
        if (targetValue != enumValue) {
            return;
        }
        if (parameter.isStatic()) {
            String value = parameter.get(null);
            if (!validator.test(value)) {
                this.addError(validator.errorMessage(value, parameter.getName()) + " if " + enumName + " is " + String.valueOf(targetValue) + " in " + this.getBreadCrumbs());
            }
            return;
        }
        parameter.addRelationValidator((context, s) -> {
            if (!validator.test((String)s)) {
                throw new IllegalStateException(validator.errorMessage((String)s, parameter.getName()) + " if " + enumName + " is " + String.valueOf(targetValue) + " in " + this.getBreadCrumbs());
            }
        });
    }

    protected <E extends Enum<E>> void validateAssetIfEnumIs(@Nonnull AssetHolder parameter, @Nonnull AssetValidator validator, @Nonnull EnumHolder<E> enumParameter, E targetValue) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(ValidateAssetIfEnumIsValidator.withAttributes(parameter.getName(), validator, enumParameter.getName(), targetValue));
            return;
        }
        if (enumParameter.isStatic()) {
            this.validateAssetIfEnumIs(parameter, validator, enumParameter.getName(), enumParameter.get(null), targetValue);
            return;
        }
        parameter.addRelationValidator((context, s) -> {
            Object enumValue = enumParameter.rawGet((ExecutionContext)context);
            if (enumValue != targetValue) {
                return;
            }
            if (!validator.test((String)s)) {
                throw new IllegalStateException(validator.errorMessage((String)s, parameter.getName()) + " if " + enumParameter.getName() + " is " + String.valueOf(targetValue) + " in " + this.getBreadCrumbs());
            }
        });
        enumParameter.addEnumRelationValidator((context, e) -> {
            if (e != targetValue) {
                return;
            }
            String stringValue = parameter.rawGet((ExecutionContext)context);
            if (!validator.test(stringValue)) {
                throw new IllegalStateException(validator.errorMessage(stringValue, parameter.getName()) + " if " + enumParameter.getName() + " is " + String.valueOf(targetValue) + " in " + this.getBreadCrumbs());
            }
        });
    }

    protected <E extends Enum<E>> void validateAssetIfEnumIs(@Nonnull AssetHolder parameter, @Nonnull AssetValidator validator, String enumName, E targetValue, E enumValue) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(ValidateAssetIfEnumIsValidator.withAttributes(parameter.getName(), validator, enumName, targetValue));
            return;
        }
        if (targetValue != enumValue) {
            return;
        }
        if (parameter.isStatic()) {
            String value = parameter.get(null);
            if (!validator.test(value)) {
                throw new IllegalStateException(validator.errorMessage(value, parameter.getName()) + " if " + enumName + " is " + String.valueOf(targetValue) + " in " + this.getBreadCrumbs());
            }
            return;
        }
        parameter.addRelationValidator((context, s) -> {
            if (!validator.test((String)s)) {
                throw new IllegalStateException(validator.errorMessage((String)s, parameter.getName()) + " if " + enumName + " is " + String.valueOf(targetValue) + " in " + this.getBreadCrumbs());
            }
        });
    }

    protected void validateAny(String attribute1, boolean value1, String attribute2, boolean value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AnyBooleanValidator.withAttributes(attribute1, attribute2));
            return;
        }
        if (value1 || value2) {
            return;
        }
        this.addError(AnyBooleanValidator.errorMessage(new String[]{attribute1, attribute2}) + " in " + this.getBreadCrumbs());
    }

    protected void validateAny(@Nonnull BooleanHolder value1, @Nonnull BooleanHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AnyBooleanValidator.withAttributes(value1.getName(), value2.getName()));
            return;
        }
        if (value1.isStatic()) {
            boolean v12 = value1.get(null);
            if (!v12) {
                this.validateAny(value2, value1.getName(), v12);
            }
            return;
        }
        if (value2.isStatic()) {
            boolean v22 = value2.get(null);
            if (!v22) {
                this.validateAny(value1, value2.getName(), v22);
            }
            return;
        }
        value1.addRelationValidator((executionContext, v1) -> {
            boolean v2 = value2.rawGet((ExecutionContext)executionContext);
            if (!v1.booleanValue() && !v2) {
                throw new IllegalStateException(AnyBooleanValidator.errorMessage(new String[]{value1.getName(), value2.getName()}) + " in " + this.getBreadCrumbs());
            }
        });
        value2.addRelationValidator((executionContext, v2) -> {
            boolean v1 = value1.rawGet((ExecutionContext)executionContext);
            if (!v1 && !v2.booleanValue()) {
                throw new IllegalStateException(AnyBooleanValidator.errorMessage(new String[]{value1.getName(), value2.getName()}) + " in " + this.getBreadCrumbs());
            }
        });
    }

    protected void validateAny(@Nonnull BooleanHolder value1, String attribute2, boolean value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AnyBooleanValidator.withAttributes(value1.getName(), attribute2));
            return;
        }
        if (value2) {
            return;
        }
        if (value1.isStatic()) {
            if (value1.get(null)) {
                return;
            }
            this.addError(AnyBooleanValidator.errorMessage(new String[]{value1.getName(), attribute2}) + " in " + this.getBreadCrumbs());
        }
        value1.addRelationValidator((executionContext, v1) -> {
            if (!v1.booleanValue()) {
                throw new IllegalStateException(AnyBooleanValidator.errorMessage(new String[]{value1.getName(), attribute2}) + " in " + this.getBreadCrumbs());
            }
        });
    }

    protected void validateAny(String attribute1, boolean value1, String attribute2, boolean value2, String attribute3, boolean value3) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AnyBooleanValidator.withAttributes(new String[]{attribute1, attribute2, attribute3}));
            return;
        }
        if (value1 || value2 || value3) {
            return;
        }
        this.addError(AnyBooleanValidator.errorMessage(new String[]{attribute1, attribute2, attribute3}) + " in " + this.getBreadCrumbs());
    }

    protected void validateAny(@Nonnull String[] attributes, @Nonnull boolean[] values) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AnyBooleanValidator.withAttributes(attributes));
            return;
        }
        if (AnyBooleanValidator.test(values)) {
            return;
        }
        this.addError(AnyBooleanValidator.errorMessage(attributes) + " in " + this.getBreadCrumbs());
    }

    protected void validateAtMostOne(@Nonnull BooleanHolder value1, @Nonnull BooleanHolder value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AtMostOneBooleanValidator.withAttributes(value1.getName(), value2.getName()));
            return;
        }
        if (value1.isStatic()) {
            boolean v12 = value1.get(null);
            if (v12) {
                this.validateAtMostOne(value2, value1.getName(), v12);
            }
            return;
        }
        if (value2.isStatic()) {
            boolean v22 = value2.get(null);
            if (v22) {
                this.validateAtMostOne(value1, value2.getName(), v22);
            }
            return;
        }
        value1.addRelationValidator((executionContext, v1) -> {
            boolean v2 = value2.rawGet((ExecutionContext)executionContext);
            if (v1.booleanValue() && v2) {
                throw new IllegalStateException(AtMostOneBooleanValidator.errorMessage(new String[]{value1.getName(), value2.getName()}) + " in " + this.getBreadCrumbs());
            }
        });
        value2.addRelationValidator((executionContext, v2) -> {
            boolean v1 = value1.rawGet((ExecutionContext)executionContext);
            if (v1 && v2.booleanValue()) {
                throw new IllegalStateException(AtMostOneBooleanValidator.errorMessage(new String[]{value1.getName(), value2.getName()}) + " in " + this.getBreadCrumbs());
            }
        });
    }

    protected void validateAtMostOne(@Nonnull BooleanHolder value1, String attribute2, boolean value2) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(AtMostOneBooleanValidator.withAttributes(value1.getName(), attribute2));
            return;
        }
        if (!value2) {
            return;
        }
        if (value1.isStatic() && value1.get(null)) {
            this.addError(AtMostOneBooleanValidator.errorMessage(new String[]{value1.getName(), attribute2}) + " in " + this.getBreadCrumbs());
        }
        value1.addRelationValidator((executionContext, v1) -> {
            if (v1.booleanValue()) {
                throw new IllegalStateException(AtMostOneBooleanValidator.errorMessage(new String[]{value1.getName(), attribute2}) + " in " + this.getBreadCrumbs());
            }
        });
    }

    protected void validateBooleanImplicationAnyAntecedent(String[] attributes1, @Nonnull boolean[] values1, boolean antecedentState, String[] attributes2, @Nonnull boolean[] values2, boolean consequentState) {
        this.validateBooleanImplication(attributes1, values1, antecedentState, attributes2, values2, consequentState, true);
    }

    protected void validateBooleanImplicationAllAntecedents(String[] attributes1, @Nonnull boolean[] values1, boolean antecedentState, String[] attributes2, @Nonnull boolean[] values2, boolean consequentState) {
        this.validateBooleanImplication(attributes1, values1, antecedentState, attributes2, values2, consequentState, false);
    }

    @Nonnull
    protected ToIntFunction<BuilderSupport> requireStringValueStoreParameter(String parameter, ValueStoreValidator.UseType useType) {
        if (this.valueStoreUsages == null) {
            this.valueStoreUsages = new ObjectArrayList<ValueStoreValidator.ValueUsage>();
        }
        this.valueStoreUsages.add(new ValueStoreValidator.ValueUsage(parameter, ValueStore.Type.String, useType, this));
        return support -> support.getValueStoreStringSlot(parameter);
    }

    @Nonnull
    protected ToIntFunction<BuilderSupport> requireIntValueStoreParameter(String parameter, ValueStoreValidator.UseType useType) {
        if (this.valueStoreUsages == null) {
            this.valueStoreUsages = new ObjectArrayList<ValueStoreValidator.ValueUsage>();
        }
        this.valueStoreUsages.add(new ValueStoreValidator.ValueUsage(parameter, ValueStore.Type.Int, useType, this));
        return support -> support.getValueStoreIntSlot(parameter);
    }

    @Nonnull
    protected ToIntFunction<BuilderSupport> requireDoubleValueStoreParameter(String parameter, ValueStoreValidator.UseType useType) {
        if (this.valueStoreUsages == null) {
            this.valueStoreUsages = new ObjectArrayList<ValueStoreValidator.ValueUsage>();
        }
        this.valueStoreUsages.add(new ValueStoreValidator.ValueUsage(parameter, ValueStore.Type.Double, useType, this));
        return support -> support.getValueStoreDoubleSlot(parameter);
    }

    private void validateBooleanImplication(String[] attributes1, @Nonnull boolean[] values1, boolean antecedentState, String[] attributes2, @Nonnull boolean[] values2, boolean consequentState, boolean anyAntecedent) {
        BooleanImplicationValidator validator = BooleanImplicationValidator.withAttributes(attributes1, antecedentState, attributes2, consequentState, anyAntecedent);
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(validator);
            return;
        }
        if (validator.test(values1, values2)) {
            return;
        }
        this.addError(validator.errorMessage() + " in " + this.getBreadCrumbs());
    }

    protected void provideFeature(@Nonnull Feature feature) {
        this.provideFeatureOrParameters(new UnconditionalFeatureProviderEvaluator(feature));
    }

    protected void overrideParameters(@Nonnull String[] parameters, ParameterType ... types) {
        if (!this.isCreatingDescriptor() && this.evaluatorHelper.isDisallowParameterProviders()) {
            return;
        }
        this.provideFeatureOrParameters(new UnconditionalParameterProviderEvaluator(parameters, types));
    }

    protected void preventParameterOverride() {
        if (this.isCreatingDescriptor()) {
            return;
        }
        this.evaluatorHelper.disallowParameterProviders();
    }

    private void provideFeatureOrParameters(ProviderEvaluator evaluator) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addProviderEvaluator(evaluator);
            return;
        }
        this.evaluatorHelper.add(evaluator);
    }

    protected void provideFeature(@Nonnull EnumSet<Feature> feature) {
        feature.forEach(this::provideFeature);
    }

    protected void requireFeature(@Nonnull EnumSet<Feature> feature) {
        this.requireFeature(RequiresOneOfFeaturesValidator.withFeatures(feature));
    }

    protected <E extends Enum<E>> void requireFeatureIf(String enumName, E targetValue, E enumValue, @Nonnull EnumSet<Feature> feature) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(RequiresFeatureIfEnumValidator.withAttributes(enumName, targetValue, feature));
            return;
        }
        if (targetValue != enumValue) {
            return;
        }
        if (this.evaluatorHelper.belongsToFeatureRequiringComponent()) {
            this.evaluatorHelper.addComponentRequirementValidator((helper, executionContext) -> this.validateRequiresFeatureIf(enumName, targetValue, enumValue, feature, (FeatureEvaluatorHelper)helper));
            return;
        }
        if (RequiresFeatureIfEnumValidator.staticValidate(this.evaluatorHelper, feature, targetValue, enumValue)) {
            return;
        }
        if (this.evaluatorHelper.requiresProviderReferenceEvaluation()) {
            this.evaluatorHelper.addProviderReferenceValidator((manager, context) -> {
                this.resolveFeatureProviderReverences((BuilderManager)manager);
                this.validateRequiresFeatureIf(enumName, targetValue, enumValue, feature, this.evaluatorHelper);
            });
            return;
        }
        CharSequence[] description = BuilderBase.getDescriptionArray(feature);
        this.addError(String.format("If %s is %s, one of %s must be provided at %s", enumName, targetValue, String.join((CharSequence)", ", description), this.getBreadCrumbs()));
    }

    protected void requireFeatureIf(String attribute, boolean requiredValue, boolean value, @Nonnull EnumSet<Feature> feature) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(RequiresFeatureIfValidator.withAttributes(attribute, requiredValue, feature));
            return;
        }
        if (this.evaluatorHelper.belongsToFeatureRequiringComponent()) {
            this.evaluatorHelper.addComponentRequirementValidator((helper, executionContext) -> this.validateRequiresFeatureIf(attribute, requiredValue, value, feature, (FeatureEvaluatorHelper)helper));
            return;
        }
        if (RequiresFeatureIfValidator.staticValidate(this.evaluatorHelper, feature, requiredValue, value)) {
            return;
        }
        if (this.evaluatorHelper.requiresProviderReferenceEvaluation()) {
            this.evaluatorHelper.addProviderReferenceValidator((manager, context) -> {
                this.resolveFeatureProviderReverences((BuilderManager)manager);
                this.validateRequiresFeatureIf(attribute, requiredValue, value, feature, this.evaluatorHelper);
            });
            return;
        }
        CharSequence[] description = BuilderBase.getDescriptionArray(feature);
        this.addError(String.format("If %s is %s, one of %s must be provided at %s", attribute, requiredValue ? "set" : "not set", String.join((CharSequence)", ", description), this.getBreadCrumbs()));
    }

    protected void requireFeatureIf(@Nonnull BooleanHolder parameter, boolean requiredValue, @Nonnull EnumSet<Feature> feature) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(RequiresFeatureIfValidator.withAttributes(parameter.getName(), requiredValue, feature));
            return;
        }
        if (this.evaluatorHelper.belongsToFeatureRequiringComponent()) {
            this.evaluatorHelper.addComponentRequirementValidator((helper, executionContext) -> this.validateRequiresFeatureIf(parameter.getName(), requiredValue, parameter.get((ExecutionContext)executionContext), feature, (FeatureEvaluatorHelper)helper));
            return;
        }
        if (parameter.isStatic() && RequiresFeatureIfValidator.staticValidate(this.evaluatorHelper, feature, requiredValue, parameter.get(null))) {
            return;
        }
        if (this.evaluatorHelper.requiresProviderReferenceEvaluation()) {
            this.evaluatorHelper.addProviderReferenceValidator((manager, context) -> {
                this.resolveFeatureProviderReverences((BuilderManager)manager);
                this.validateRequiresFeatureIf(parameter.getName(), requiredValue, parameter.rawGet((ExecutionContext)context), feature, this.evaluatorHelper);
            });
            return;
        }
        parameter.addRelationValidator((context, value) -> this.validateRequiresFeatureIf(parameter.getName(), requiredValue, (boolean)value, feature, this.evaluatorHelper));
    }

    private boolean hasOverriddenParameter(String parameter, ParameterType type, @Nonnull FeatureEvaluatorHelper helper) {
        for (ProviderEvaluator provider : helper.getProviders()) {
            if (!(provider instanceof ParameterProviderEvaluator) || !((ParameterProviderEvaluator)provider).hasParameter(parameter, type)) continue;
            return true;
        }
        return false;
    }

    private void validateOverriddenParameter(String parameter, @Nonnull ParameterType type, @Nonnull FeatureEvaluatorHelper helper) {
        if (this.hasOverriddenParameter(parameter, type, helper)) {
            return;
        }
        throw new IllegalStateException(String.format("Parameter %s is missing and either not provided by a sensor, or provided with the wrong parameter type (expected %s) in context %s", parameter, type.get(), this.getBreadCrumbs()));
    }

    private <E extends Enum<E>> void validateRequiresFeatureIf(String attribute, E requiredValue, E value, @Nonnull EnumSet<Feature> feature, @Nonnull FeatureEvaluatorHelper helper) {
        if (RequiresFeatureIfEnumValidator.staticValidate(helper, feature, requiredValue, value)) {
            return;
        }
        CharSequence[] description = BuilderBase.getDescriptionArray(feature);
        throw new IllegalStateException(String.format("If %s is %s, one of %s must be provided at %s", attribute, requiredValue, String.join((CharSequence)", ", description), this.getBreadCrumbs()));
    }

    private void validateRequiresFeatureIf(String attribute, boolean requiredValue, boolean value, @Nonnull EnumSet<Feature> feature, @Nonnull FeatureEvaluatorHelper helper) {
        if (RequiresFeatureIfValidator.staticValidate(helper, feature, requiredValue, value)) {
            return;
        }
        CharSequence[] description = BuilderBase.getDescriptionArray(feature);
        throw new IllegalStateException(String.format("If %s is %s, one of %s must be provided at %s", attribute, requiredValue ? "set" : "not set", String.join((CharSequence)", ", description), this.getBreadCrumbs()));
    }

    private void requireFeature(@Nonnull RequiredFeatureValidator validator) {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(validator);
            return;
        }
        if (this.evaluatorHelper.belongsToFeatureRequiringComponent()) {
            this.evaluatorHelper.addComponentRequirementValidator((helper, executionContext) -> {
                if (validator.validate((FeatureEvaluatorHelper)helper)) {
                    return;
                }
                throw new IllegalStateException(validator.getErrorMessage(this.getBreadCrumbs()));
            });
            return;
        }
        if (validator.validate(this.evaluatorHelper)) {
            return;
        }
        if (this.evaluatorHelper.requiresProviderReferenceEvaluation()) {
            this.evaluatorHelper.addProviderReferenceValidator((manager, context) -> {
                this.resolveFeatureProviderReverences((BuilderManager)manager);
                if (validator.validate(this.evaluatorHelper)) {
                    return;
                }
                throw new IllegalStateException(validator.getErrorMessage(this.getBreadCrumbs()));
            });
            return;
        }
        this.addError(validator.getErrorMessage(this.getBreadCrumbs()));
    }

    @Nonnull
    public static String[] getDescriptionArray(@Nonnull EnumSet<Feature> feature) {
        String[] description = new String[feature.size()];
        Feature[] featureArray = (Feature[])feature.toArray(Feature[]::new);
        for (int i = 0; i < featureArray.length; ++i) {
            description[i] = featureArray[i].get();
        }
        return description;
    }

    private void resolveFeatureProviderReverences(BuilderManager manager) {
        for (ProviderEvaluator providerEvaluator : this.evaluatorHelper.getProviders()) {
            providerEvaluator.resolveReferences(manager);
        }
    }

    protected void registerStateSensor(String name, String subState, @Nonnull BiConsumer<Integer, Integer> setter) {
        if (this.isCreatingDescriptor()) {
            return;
        }
        this.stateHelper.getAndPutSensorIndex(name, subState, setter);
        this.getParent().setCurrentStateName(name);
    }

    protected void registerStateSetter(String name, String subState, @Nonnull BiConsumer<Integer, Integer> setter) {
        if (this.isCreatingDescriptor()) {
            return;
        }
        this.stateHelper.getAndPutSetterIndex(name, subState, setter);
    }

    protected void registerStateRequirer(String name, String subState, @Nonnull BiConsumer<Integer, Integer> setter) {
        if (this.isCreatingDescriptor()) {
            return;
        }
        this.stateHelper.getAndPutStateRequirerIndex(name, subState, setter);
    }

    protected void validateIsComponent() {
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(ComponentOnlyValidator.get());
            return;
        }
        if (!this.isComponent()) {
            this.addError(String.format("Element not valid outside of component at: %s", this.getBreadCrumbs()));
        }
    }

    protected void requireStateString(@Nonnull JsonElement data, String name, boolean componentAllowed, @Nonnull TriConsumer<String, String, Boolean> setter, BuilderDescriptorState state, String shortDescription, String longDescription) {
        String subState;
        StateStringValidator validator = StateStringValidator.get();
        this.requireString(data, name, (String v) -> {}, (StringValidator)validator, state, shortDescription, longDescription);
        if (this.isCreatingDescriptor()) {
            return;
        }
        String mainState = validator.hasMainState() ? validator.getMainState() : null;
        String string = subState = validator.hasSubState() ? validator.getSubState() : null;
        if (this.stateHelper.isComponent()) {
            if (!componentAllowed) {
                this.addError(String.format("Components not supported for state setter %s at %s", subState, this.getBreadCrumbs()));
            }
            if (!this.stateHelper.hasDefaultLocalState()) {
                this.addError("Component with local states must define a 'DefaultState' at the top of the file");
            }
            if (mainState != null) {
                this.addError(String.format("Components must not contain references to main states (%s) at %s", mainState, this.getBreadCrumbs()));
            }
            setter.accept(subState, null, false);
            return;
        }
        if (mainState == null) {
            mainState = this.stateHelper.getCurrentParentState();
        }
        boolean isDefaultSubState = false;
        if (subState == null) {
            subState = this.stateHelper.getDefaultSubState();
            isDefaultSubState = true;
        }
        if (mainState == null) {
            this.addError(String.format("Substate %s does not have a specified main state at %s", subState, this.getBreadCrumbs()));
        }
        setter.accept(mainState, subState, isDefaultSubState);
    }

    protected boolean getExistentStateSet(@Nonnull JsonElement data, String name, @Nonnull Consumer<Int2ObjectMap<IntSet>> setter, @Nonnull StateMappingHelper stateHelper, BuilderDescriptorState state, String shortDescription, @Nullable String longDescription) {
        StateStringValidator validator = StateStringValidator.requireMainState();
        if (this.isCreatingSchema()) {
            ArraySchema a = new ArraySchema();
            a.setItem(new StringSchema());
            Schema s2 = BuilderExpressionDynamic.computableSchema(a);
            s2.setTitle(name);
            s2.setDescription(longDescription == null ? shortDescription : longDescription);
            this.builderSchema.getProperties().put(name, s2);
            return false;
        }
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addAttribute(name, "StringList", state, shortDescription, longDescription).required().validator(validator);
            return false;
        }
        try {
            JsonElement element = this.getOptionalJsonElement(data, name);
            if (element == null) {
                setter.accept(null);
                return false;
            }
            String[][] strings = new String[1][1];
            this.validateAndSet(this.expectStringArray(element, null, name), StringArrayNoEmptyStringsValidator.get(), (String[] s) -> {
                strings[0] = s;
            }, name);
            String[] stringStates = strings[0];
            Int2ObjectOpenHashMap stateSets = new Int2ObjectOpenHashMap();
            for (String stringState : stringStates) {
                if (!validator.test(stringState)) {
                    throw new IllegalStateException(validator.errorMessage(stringState));
                }
                String subState = validator.hasSubState() ? validator.getSubState() : stateHelper.getDefaultSubState();
                stateHelper.getAndPutStateRequirerIndex(validator.getMainState(), subState, (m, s) -> stateSets.computeIfAbsent((int)m, k -> new IntOpenHashSet()).add((int)s));
            }
            stateSets.trim();
            setter.accept(stateSets);
            return true;
        }
        catch (Exception e) {
            this.addError(e);
            return false;
        }
    }

    protected boolean getDefaultSubState(@Nonnull JsonElement data, String name, @Nonnull Consumer<String> setter, StringValidator validator, BuilderDescriptorState state, String shortDescription, String longDescription) {
        String[] defaultSubState = new String[1];
        boolean read = this.getString(data, name, (String v) -> {
            defaultSubState[0] = v;
        }, "Default", validator, state, shortDescription, longDescription);
        if (!this.isCreatingDescriptor()) {
            this.stateHelper.setDefaultSubState(defaultSubState[0]);
        }
        setter.accept(defaultSubState[0]);
        return read;
    }

    protected void increaseDepth() {
        if (this.isCreatingDescriptor()) {
            return;
        }
        this.stateHelper.increaseDepth();
    }

    protected void decreaseDepth() {
        if (this.isCreatingDescriptor()) {
            return;
        }
        this.stateHelper.decreaseDepth();
    }

    protected void setNotComponent() {
        if (this.isCreatingDescriptor()) {
            return;
        }
        this.stateHelper.setNotComponent();
    }

    protected boolean isComponent() {
        if (this.isCreatingDescriptor()) {
            return false;
        }
        return this.stateHelper.isComponent();
    }

    protected void requireInstructionType(@Nonnull EnumSet<InstructionType> instructionType) {
        this.requireContext(instructionType, null);
    }

    protected void requireContext(@Nonnull EnumSet<InstructionType> instructionType, EnumSet<ComponentContext> componentContexts) {
        InstructionContextValidator validator = InstructionContextValidator.inInstructions(instructionType, componentContexts);
        if (this.isCreatingDescriptor()) {
            this.builderDescriptor.addValidator(validator);
            return;
        }
        if (this.instructionContextHelper.isComponent()) {
            this.instructionContextHelper.addComponentContextEvaluator((type, extraContext) -> {
                boolean correctInstruction = InstructionContextHelper.isInCorrectInstruction(instructionType, type);
                boolean correctExtraContext = InstructionContextHelper.extraContextMatches(componentContexts, extraContext);
                if (correctInstruction && correctExtraContext) {
                    return;
                }
                throw new IllegalStateException(InstructionContextValidator.getErrorMessage(this.getTypeName(), type, correctInstruction, extraContext, correctExtraContext, this.getBreadCrumbs()));
            });
            return;
        }
        boolean correctInstruction = this.instructionContextHelper.isInCorrectInstruction(instructionType);
        boolean correctExtraContext = this.instructionContextHelper.extraContextMatches(componentContexts);
        if (correctInstruction && correctExtraContext) {
            return;
        }
        this.addError(InstructionContextValidator.getErrorMessage(this.getTypeName(), this.instructionContextHelper.getInstructionContext(), correctInstruction, this.instructionContextHelper.getComponentContext(), correctExtraContext, this.getBreadCrumbs()));
    }

    @Override
    public IntSet getDependencies() {
        return this.builderParameters.getDependencies();
    }

    @Override
    public boolean validate(String configName, @Nonnull NPCLoadTimeValidationHelper validationHelper, ExecutionContext context, Scope globalScope, @Nonnull List<String> errors) {
        boolean result = true;
        if (this.dynamicHolders != null) {
            for (ValueHolder assetHolder : this.dynamicHolders) {
                result &= this.validateDynamicHolder(configName, assetHolder, context, errors);
            }
        }
        ValueStoreValidator valueStoreValidator = validationHelper.getValueStoreValidator();
        if (this.valueStoreUsages != null) {
            for (ValueStoreValidator.ValueUsage usage : this.valueStoreUsages) {
                valueStoreValidator.registerValueUsage(usage);
            }
        }
        return result &= this.runLoadTimeValidationHelper(configName, validationHelper, context, errors);
    }

    protected void runLoadTimeValidationHelper0(String configName, NPCLoadTimeValidationHelper loadTimeValidationHelper, ExecutionContext context, List<String> errors) {
    }

    private boolean runLoadTimeValidationHelper(String configName, NPCLoadTimeValidationHelper loadTimeValidationHelper, ExecutionContext context, @Nonnull List<String> errors) {
        try {
            this.runLoadTimeValidationHelper0(configName, loadTimeValidationHelper, context, errors);
        }
        catch (Exception e) {
            errors.add(String.format("%s: %s", configName, e.getMessage()));
            return false;
        }
        return true;
    }

    private boolean validateDynamicHolder(String configName, @Nonnull ValueHolder holder, ExecutionContext context, @Nonnull List<String> errors) {
        try {
            holder.validate(context);
        }
        catch (Exception e) {
            errors.add(String.format("%s: %s", configName, e.getMessage()));
            return false;
        }
        return true;
    }

    private void trackDynamicHolder(@Nonnull ValueHolder holder) {
        if (holder.isStatic()) {
            return;
        }
        if (this.dynamicHolders == null) {
            this.dynamicHolders = new ArrayList<ValueHolder>();
        }
        this.dynamicHolders.add(holder);
    }

    public static String readString(@Nonnull JsonObject object, String key) {
        return BuilderBase.expectStringElement(BuilderBase.expectKey(object, key), key);
    }

    public static String readString(@Nonnull JsonObject jsonObject, String key, String defaultValue) {
        JsonElement value = jsonObject.get(key);
        if (value == null) {
            return defaultValue;
        }
        return BuilderBase.expectStringElement(value, key);
    }

    public static boolean readBoolean(@Nonnull JsonObject jsonObject, String key, boolean defaultValue) {
        JsonElement value = jsonObject.get(key);
        if (value == null) {
            return defaultValue;
        }
        return BuilderBase.expectBooleanElement(value, key);
    }

    @Nonnull
    public static JsonElement expectKey(@Nonnull JsonObject jsonObject, String key) {
        JsonElement value = jsonObject.get(key);
        if (value == null) {
            throw new IllegalStateException("'" + key + "' missing in JSON object");
        }
        return value;
    }

    public static String expectStringElement(@Nonnull JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return element.getAsJsonPrimitive().getAsString();
        }
        throw new IllegalStateException("'" + key + "' must be a string");
    }

    public static boolean expectBooleanElement(@Nonnull JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsJsonPrimitive().getAsBoolean();
        }
        throw new IllegalStateException("'" + key + "' must be a boolean value");
    }

    public static JsonObject expectObject(@Nonnull JsonElement element) {
        if (!element.isJsonObject()) {
            throw new IllegalStateException("Expected a JSON object");
        }
        return element.getAsJsonObject();
    }

    public static JsonObject expectObject(@Nonnull JsonElement element, String key) {
        if (!element.isJsonObject()) {
            throw new IllegalStateException("'" + key + "' must be an object: " + String.valueOf(element));
        }
        return element.getAsJsonObject();
    }

    public static String[] readStringArray(@Nonnull JsonObject object, String key, @Nonnull StringValidator validator, String[] defaultValue) {
        JsonElement value = object.get(key);
        if (value == null) {
            return defaultValue;
        }
        return BuilderBase.readStringArray(value, key, validator);
    }

    @Nonnull
    public static String[] readStringArray(@Nonnull JsonElement element, String key, @Nonnull StringValidator validator) {
        if (!element.isJsonArray()) {
            throw new IllegalStateException(key + " must be an array: " + String.valueOf(element));
        }
        JsonArray array = element.getAsJsonArray();
        String[] ret = new String[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            String string = BuilderBase.expectStringElement(array.get(i), String.format("%s element at position %s", key, i));
            if (!validator.test(string)) {
                throw new IllegalStateException(validator.errorMessage(string));
            }
            ret[i] = string;
        }
        return ret;
    }

    protected void addError(String error) {
        this.readErrors.add(this.fileName + ": " + error);
    }

    protected void addError(@Nonnull Exception e) {
        this.addError(e.getMessage());
    }
}

