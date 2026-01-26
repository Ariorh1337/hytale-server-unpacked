/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.BooleanSchema;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.StateMappingHelper;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpression;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticBooleanArray;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticEmptyArray;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticNumberArray;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticStringArray;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.StdScope;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import com.hypixel.hytale.server.npc.util.expression.compile.CompileContext;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderParameters {
    public static final String KEY_PARAMETERS = "Parameters";
    public static final String KEY_IMPORT_STATES = "_ImportStates";
    public static final String KEY_INTERFACE = "Interface";
    @Nonnull
    protected final Map<String, Parameter> parameters;
    protected StdScope scope;
    @Nullable
    protected CompileContext compileContext;
    protected final String fileName;
    protected final IntSet dependencies;
    protected final String interfaceCode;

    protected BuilderParameters(StdScope scope, String fileName, String interfaceCode) {
        this(scope, fileName, interfaceCode, new IntOpenHashSet());
    }

    protected BuilderParameters(StdScope scope, String fileName, String interfaceCode, IntSet dependencies) {
        this.scope = new StdScope(scope);
        this.compileContext = new CompileContext(this.scope);
        this.parameters = new HashMap<String, Parameter>();
        this.fileName = fileName;
        this.dependencies = dependencies;
        this.interfaceCode = interfaceCode;
    }

    protected BuilderParameters(@Nonnull BuilderParameters other) {
        this(other.scope, other.fileName, other.interfaceCode, other.dependencies);
    }

    public boolean isEmpty() {
        return this.parameters.isEmpty();
    }

    public void addParametersToScope() {
        this.parameters.forEach((name, parameter) -> parameter.expression.addToScope((String)name, this.scope));
    }

    public ValueType getParameterType(String name) {
        Parameter p = this.parameters.get(name);
        if (p == null || p.isPrivate()) {
            return ValueType.VOID;
        }
        return p.getExpression().getType();
    }

    public void readJSON(@Nonnull JsonObject jsonObject, @Nonnull StateMappingHelper stateHelper) {
        JsonElement parameterValue = jsonObject.get(KEY_PARAMETERS);
        if (parameterValue == null) {
            return;
        }
        JsonObject modify = BuilderBase.expectObject(parameterValue, KEY_PARAMETERS);
        modify.entrySet().forEach(stringElementPair -> {
            String key = (String)stringElementPair.getKey();
            if (this.parameters.containsKey(key)) {
                throw new IllegalStateException("Duplicate entry '" + key + "' in 'Parameters' block");
            }
            if (key.equals(KEY_IMPORT_STATES)) {
                if (!((JsonElement)stringElementPair.getValue()).isJsonArray()) {
                    throw new IllegalStateException(String.format("%s in parameter block must be a Json Array", KEY_IMPORT_STATES));
                }
                stateHelper.setComponentImportStateMappings(((JsonElement)stringElementPair.getValue()).getAsJsonArray());
                return;
            }
            this.parameters.put(key, Parameter.fromJSON((JsonElement)stringElementPair.getValue(), this, key));
        });
    }

    public void createCompileContext() {
        this.compileContext = new CompileContext();
    }

    public void disposeCompileContext() {
        this.compileContext = null;
    }

    @Nullable
    public CompileContext getCompileContext() {
        return this.compileContext;
    }

    public ValueType compile(@Nonnull String expression) {
        return this.getCompileContext().compile(expression, this.getScope(), false);
    }

    public List<ExecutionContext.Instruction> getInstructions() {
        return this.getCompileContext().getInstructions();
    }

    @Nullable
    public ExecutionContext.Operand getConstantOperand() {
        return this.getCompileContext().getAsOperand();
    }

    public StdScope getScope() {
        return this.scope;
    }

    @Nonnull
    public StdScope createScope() {
        return StdScope.copyOf(this.scope);
    }

    public void validateNoDuplicateParameters(@Nonnull BuilderParameters other) {
        other.parameters.keySet().forEach(key -> {
            if (this.parameters.containsKey(key)) {
                throw new IllegalStateException("Parameter '" + key + "' in 'Parameters' block hides parameter from parent scope");
            }
        });
    }

    public String getFileName() {
        return this.fileName;
    }

    public IntSet getDependencies() {
        return this.dependencies;
    }

    public String getInterfaceCode() {
        return this.interfaceCode;
    }

    public void addDependency(int d) {
        this.dependencies.add(d);
    }

    @Nonnull
    public static ObjectSchema toSchema(@Nonnull SchemaContext context) {
        ObjectSchema schema = new ObjectSchema();
        schema.setTitle(KEY_PARAMETERS);
        schema.setProperties(Map.of(KEY_IMPORT_STATES, new ArraySchema(new StringSchema())));
        schema.setAdditionalProperties(Parameter.toSchema(context));
        return schema;
    }

    public static class Parameter {
        public static final String KEY_VALUE = "Value";
        public static final String KEY_TYPE_HINT = "TypeHint";
        public static final String KEY_VALIDATE = "Validate";
        public static final String KEY_CONFINE = "Confine";
        public static final String KEY_DESCRIPTION = "Description";
        public static final String KEY_PRIVATE = "Private";
        private final BuilderExpression expression;
        private final String description;
        private final String code;
        private List<ExecutionContext.Instruction> instructionList;
        private final boolean isValidation;
        private final boolean isPrivate;

        public Parameter(BuilderExpression expression, String description, String code, boolean isValidation, boolean isPrivate) {
            this.expression = expression;
            this.description = description;
            this.code = code;
            this.isValidation = isValidation;
            this.isPrivate = isPrivate;
        }

        public BuilderExpression getExpression() {
            return this.expression;
        }

        public String getDescription() {
            return this.description;
        }

        public boolean isValidation() {
            return this.isValidation;
        }

        public boolean isPrivate() {
            return this.isPrivate;
        }

        @Nonnull
        public static ObjectSchema toSchema(@Nonnull SchemaContext context) {
            ObjectSchema props = new ObjectSchema();
            props.setTitle("Parameter");
            LinkedHashMap<String, Schema> map = new LinkedHashMap<String, Schema>();
            map.put(KEY_VALUE, BuilderExpression.toSchema(context));
            map.put(KEY_TYPE_HINT, new StringSchema());
            map.put(KEY_VALIDATE, new StringSchema());
            map.put(KEY_CONFINE, new StringSchema());
            map.put(KEY_DESCRIPTION, new StringSchema());
            map.put(KEY_PRIVATE, new BooleanSchema());
            props.setProperties(map);
            return props;
        }

        @Nonnull
        private static Parameter fromJSON(@Nonnull JsonElement element, @Nonnull BuilderParameters builderParameters, String parameterName) {
            boolean hasValidate;
            JsonObject jsonObject = BuilderBase.expectObject(element);
            BuilderExpression expression = BuilderExpression.fromJSON(BuilderBase.expectKey(jsonObject, KEY_VALUE), builderParameters, true);
            if (expression instanceof BuilderExpressionStaticEmptyArray) {
                if (!jsonObject.has(KEY_TYPE_HINT)) {
                    throw new IllegalStateException("TypeHint missing for parameter " + parameterName);
                }
                String type = BuilderBase.readString(jsonObject, KEY_TYPE_HINT);
                if ("STRING".equalsIgnoreCase(type)) {
                    expression = BuilderExpressionStaticStringArray.INSTANCE_EMPTY;
                } else if ("NUMBER".equalsIgnoreCase(type)) {
                    expression = BuilderExpressionStaticNumberArray.INSTANCE_EMPTY;
                } else if ("BOOLEAN".equalsIgnoreCase(type)) {
                    expression = BuilderExpressionStaticBooleanArray.INSTANCE_EMPTY;
                } else {
                    throw new IllegalStateException("TypeHint must be one of STRING, NUMBER, BOOLEAN for parameter " + parameterName);
                }
            }
            String validate = BuilderBase.readString(jsonObject, KEY_VALIDATE, null);
            String confine = BuilderBase.readString(jsonObject, KEY_CONFINE, null);
            boolean bl = hasValidate = validate != null;
            if (hasValidate && confine != null) {
                throw new IllegalStateException("Only either 'Confine' or 'Validate' allowed for parameter " + parameterName);
            }
            String code = hasValidate ? validate : confine;
            return new Parameter(expression, BuilderBase.readString(jsonObject, KEY_DESCRIPTION, null), code, hasValidate, BuilderBase.readBoolean(jsonObject, KEY_PRIVATE, false));
        }
    }
}

