/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.NamedSchema;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.SchemaConvertable;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.EmptyBuilderModifier;
import com.hypixel.hytale.server.npc.asset.builder.StateMappingHelper;
import com.hypixel.hytale.server.npc.asset.builder.StatePair;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpression;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StateStringValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.config.balancing.BalanceAsset;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.util.expression.StdScope;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderModifier {
    public static final String KEY_MODIFY = "Modify";
    public static final String KEY_EXPORT_STATES = "_ExportStates";
    public static final String KEY_INTERFACE_PARAMETERS = "_InterfaceParameters";
    public static final String KEY_COMBAT_CONFIG = "_CombatConfig";
    public static final String KEY_INTERACTION_VARS = "_InteractionVars";
    private final Object2ObjectMap<String, ExpressionHolder> builderExpressionMap;
    private final StatePair[] exportedStateIndexes;
    private final StateMappingHelper stateHelper;
    private final String combatConfig;
    private final Map<String, String> interactionVars;

    protected BuilderModifier(Object2ObjectMap<String, ExpressionHolder> builderExpressionMap, StatePair[] exportedStateIndexes, StateMappingHelper stateHelper, String combatConfig, Map<String, String> interactionVars) {
        this.builderExpressionMap = builderExpressionMap;
        this.exportedStateIndexes = exportedStateIndexes;
        this.stateHelper = stateHelper;
        this.combatConfig = combatConfig;
        this.interactionVars = interactionVars;
    }

    public String getCombatConfig() {
        return this.combatConfig;
    }

    public Map<String, String> getInteractionVars() {
        return this.interactionVars;
    }

    public boolean isEmpty() {
        return this.builderExpressionMap.isEmpty();
    }

    public int exportedStateCount() {
        return this.exportedStateIndexes.length;
    }

    public void applyComponentStateMap(@Nonnull BuilderSupport support) {
        support.setModifiedStateMap(this.stateHelper, this.exportedStateIndexes);
    }

    public void popComponentStateMap(@Nonnull BuilderSupport support) {
        support.popModifiedStateMap();
    }

    @Nonnull
    public Scope createScope(@Nonnull BuilderSupport builderSupport, @Nonnull BuilderParameters builderParameters, Scope globalScope) {
        ExecutionContext executionContext = builderSupport.getExecutionContext();
        return this.createScope(executionContext, builderParameters, globalScope);
    }

    @Nonnull
    public Scope createScope(ExecutionContext executionContext, @Nonnull BuilderParameters builderParameters, @Nullable Scope globalScope) {
        StdScope scope = builderParameters.createScope();
        if (globalScope != null) {
            StdScope mergedScope = new StdScope(globalScope);
            mergedScope.merge(scope);
            scope = mergedScope;
        }
        StdScope finalScope = scope;
        ObjectIterator<Object2ObjectMap.Entry<String, ExpressionHolder>> iterator = Object2ObjectMaps.fastIterator(this.builderExpressionMap);
        while (iterator.hasNext()) {
            Object2ObjectMap.Entry pair = (Object2ObjectMap.Entry)iterator.next();
            String name = (String)pair.getKey();
            ExpressionHolder holder = (ExpressionHolder)pair.getValue();
            ValueType valueType = builderParameters.getParameterType(name);
            BuilderExpression expression = holder.getExpression(builderParameters.getInterfaceCode());
            if (expression == null) continue;
            if (valueType == ValueType.VOID) {
                throw new SkipSentryException(new IllegalStateException("Parameter " + name + " does not exist or is private"));
            }
            if (!ValueType.isAssignableType(expression.getType(), valueType)) {
                throw new SkipSentryException(new IllegalStateException("Parameter " + name + " has type " + String.valueOf((Object)expression.getType()) + " but should be " + String.valueOf((Object)valueType)));
            }
            expression.updateScope(finalScope, name, executionContext);
        }
        return scope;
    }

    /*
     * WARNING - void declaration
     */
    @Nonnull
    public static BuilderModifier fromJSON(@Nonnull JsonObject jsonObject, @Nonnull BuilderParameters builderParameters, @Nonnull StateMappingHelper helper, @Nonnull ExtraInfo extraInfo) {
        void var11_18;
        JsonObject modify = null;
        JsonElement modifyObject = jsonObject.get(KEY_MODIFY);
        if (modifyObject != null) {
            modify = BuilderBase.expectObject(modifyObject, KEY_MODIFY);
        }
        if (modify == null || modify.entrySet().isEmpty()) {
            return EmptyBuilderModifier.INSTANCE;
        }
        Object2ObjectOpenHashMap<String, ExpressionHolder> map = new Object2ObjectOpenHashMap<String, ExpressionHolder>();
        ObjectArrayList exportedStateIndexes = new ObjectArrayList();
        for (Map.Entry<String, JsonElement> stringElementPair : modify.entrySet()) {
            String key2 = stringElementPair.getKey();
            if (map.containsKey(key2)) {
                throw new SkipSentryException(new IllegalStateException("Duplicate entry '" + (String)key2 + "' in 'Modify' block"));
            }
            if (key2.equals(KEY_INTERFACE_PARAMETERS) || key2.equals(KEY_COMBAT_CONFIG) || key2.equals(KEY_INTERACTION_VARS)) continue;
            if (key2.equals(KEY_EXPORT_STATES)) {
                if (!stringElementPair.getValue().isJsonArray()) {
                    throw new SkipSentryException(new IllegalStateException(String.format("%s in modifier block must be a Json Array", KEY_EXPORT_STATES)));
                }
                StateStringValidator stateStringValidator = StateStringValidator.requireMainState();
                JsonArray array = stringElementPair.getValue().getAsJsonArray();
                for (int i = 0; i < array.size(); ++i) {
                    String state = array.get(i).getAsString();
                    if (!stateStringValidator.test(state)) {
                        throw new SkipSentryException(new IllegalStateException(stateStringValidator.errorMessage(state)));
                    }
                    String substate = stateStringValidator.hasSubState() ? stateStringValidator.getSubState() : helper.getDefaultSubState();
                    helper.getAndPutSetterIndex(stateStringValidator.getMainState(), substate, (m, s) -> exportedStateIndexes.add(new StatePair(validator.getMainState(), (int)m, (int)s)));
                }
                continue;
            }
            BuilderExpression builderExpression = BuilderExpression.fromJSON(stringElementPair.getValue(), builderParameters, false);
            map.put(key2, new ExpressionHolder(builderExpression));
        }
        JsonElement interfaceValue = modify.get(KEY_INTERFACE_PARAMETERS);
        if (interfaceValue != null) {
            JsonObject interfaceParameters = BuilderBase.expectObject(interfaceValue, KEY_INTERFACE_PARAMETERS);
            for (Map.Entry entry : interfaceParameters.entrySet()) {
                String interfaceKey = (String)entry.getKey();
                JsonObject parameters = BuilderBase.expectObject((JsonElement)entry.getValue());
                for (Map.Entry<String, JsonElement> parameterEntry : parameters.entrySet()) {
                    ExpressionHolder holder = map.computeIfAbsent(parameterEntry.getKey(), key -> new ExpressionHolder());
                    if (holder.hasInterfaceMappedExpression(interfaceKey)) {
                        throw new SkipSentryException(new IllegalStateException("Duplicate entry '" + parameterEntry.getKey() + "' in 'Modify' block for interface '" + interfaceKey));
                    }
                    holder.addInterfaceMappedExpression(interfaceKey, BuilderExpression.fromJSON(parameterEntry.getValue(), builderParameters, false));
                }
            }
        }
        String combatConfig = null;
        JsonElement combatConfigValue = modify.get(KEY_COMBAT_CONFIG);
        if (combatConfigValue != null) {
            combatConfig = BalanceAsset.CHILD_ASSET_CODEC.decode(BsonUtil.translateJsonToBson(combatConfigValue), extraInfo);
            extraInfo.getValidationResults()._processValidationResults();
            extraInfo.getValidationResults().logOrThrowValidatorExceptions(HytaleLogger.getLogger());
        }
        Object var11_16 = null;
        JsonElement interactionVarsValue = modify.get(KEY_INTERACTION_VARS);
        if (interactionVarsValue != null) {
            Object object = RootInteraction.CHILD_ASSET_CODEC_MAP.decode(BsonUtil.translateJsonToBson(interactionVarsValue), extraInfo);
            extraInfo.getValidationResults()._processValidationResults();
            extraInfo.getValidationResults().logOrThrowValidatorExceptions(HytaleLogger.getLogger());
        }
        return new BuilderModifier(map, (StatePair[])exportedStateIndexes.toArray(StatePair[]::new), helper, combatConfig, (Map<String, String>)var11_18);
    }

    public static void readModifierObject(@Nonnull JsonObject jsonObject, @Nonnull BuilderParameters builderParameters, @Nonnull StringHolder holder, @Nonnull Consumer<StringHolder> referenceConsumer, @Nonnull Consumer<BuilderModifier> builderModifierConsumer, @Nonnull StateMappingHelper helper, @Nonnull ExtraInfo extraInfo) {
        holder.readJSON(BuilderBase.expectKey(jsonObject, "Reference"), StringNotEmptyValidator.get(), "Reference", builderParameters);
        BuilderModifier modifier = BuilderModifier.fromJSON(jsonObject, builderParameters, helper, extraInfo);
        referenceConsumer.accept(holder);
        builderModifierConsumer.accept(modifier);
    }

    @Nonnull
    public static Schema toSchema(@Nonnull SchemaContext context) {
        return context.refDefinition(SchemaGenerator.INSTANCE);
    }

    private static class ExpressionHolder {
        private final BuilderExpression expression;
        private Object2ObjectMap<String, BuilderExpression> interfaceMappedExpressions;

        public ExpressionHolder() {
            this(null);
        }

        public ExpressionHolder(BuilderExpression expression) {
            this.expression = expression;
        }

        public boolean hasInterfaceMappedExpression(String interfaceKey) {
            return this.interfaceMappedExpressions != null && this.interfaceMappedExpressions.containsKey(interfaceKey);
        }

        public void addInterfaceMappedExpression(String interfaceKey, BuilderExpression expression) {
            if (this.interfaceMappedExpressions == null) {
                this.interfaceMappedExpressions = new Object2ObjectOpenHashMap<String, BuilderExpression>();
            }
            this.interfaceMappedExpressions.put(interfaceKey, expression);
        }

        public BuilderExpression getExpression(@Nullable String interfaceKey) {
            if (interfaceKey == null || this.interfaceMappedExpressions == null || !this.interfaceMappedExpressions.containsKey(interfaceKey)) {
                return this.expression;
            }
            return (BuilderExpression)this.interfaceMappedExpressions.get(interfaceKey);
        }
    }

    private static class SchemaGenerator
    implements SchemaConvertable<Void>,
    NamedSchema {
        @Nonnull
        public static SchemaGenerator INSTANCE = new SchemaGenerator();

        private SchemaGenerator() {
        }

        @Override
        @Nonnull
        public String getSchemaName() {
            return "NPC:Type:BuilderModifier";
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            ObjectSchema s = new ObjectSchema();
            s.setTitle("BuilderModifier");
            LinkedHashMap<String, Schema> props = new LinkedHashMap<String, Schema>();
            s.setProperties(props);
            props.put(BuilderModifier.KEY_EXPORT_STATES, new ArraySchema(new StringSchema()));
            props.put(BuilderModifier.KEY_INTERFACE_PARAMETERS, new ObjectSchema());
            Schema combatConfigKeySchema = context.refDefinition(Codec.STRING);
            combatConfigKeySchema.setTitle("Reference to " + BalanceAsset.class.getSimpleName());
            Schema combatConfigNestedSchema = context.refDefinition(BalanceAsset.CHILD_ASSET_CODEC);
            Schema combatConfigSchema = Schema.anyOf(combatConfigKeySchema, combatConfigNestedSchema);
            props.put(BuilderModifier.KEY_COMBAT_CONFIG, combatConfigSchema);
            ObjectSchema interactionVars = new ObjectSchema();
            interactionVars.setTitle("Map");
            Schema childSchema = context.refDefinition(RootInteraction.CHILD_ASSET_CODEC);
            interactionVars.setAdditionalProperties(childSchema);
            props.put(BuilderModifier.KEY_INTERACTION_VARS, interactionVars);
            s.setAdditionalProperties(BuilderExpression.toSchema(context));
            return s;
        }
    }
}

