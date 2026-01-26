/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.expression;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.hypixel.hytale.codec.schema.NamedSchema;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.SchemaConvertable;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.BooleanSchema;
import com.hypixel.hytale.codec.schema.config.NumberSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamic;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticBoolean;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticBooleanArray;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticEmptyArray;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticNumber;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticNumberArray;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticString;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionStaticStringArray;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.StdScope;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BuilderExpression {
    public static final String STATIC = "<STATIC>";

    public abstract ValueType getType();

    public abstract boolean isStatic();

    public double getNumber(ExecutionContext executionContext) {
        throw new IllegalStateException("BuilderExpression: Reading number is not supported");
    }

    public String getString(ExecutionContext executionContext) {
        throw new IllegalStateException("BuilderExpression: Reading string is not supported");
    }

    public boolean getBoolean(ExecutionContext executionContext) {
        throw new IllegalStateException("BuilderExpression: Reading boolean is not supported");
    }

    public double[] getNumberArray(ExecutionContext executionContext) {
        throw new IllegalStateException("BuilderExpression: Reading number array is not supported");
    }

    public int[] getIntegerArray(ExecutionContext executionContext) {
        throw new IllegalStateException("BuilderExpression: Reading integer is not supported");
    }

    @Nullable
    public String[] getStringArray(ExecutionContext executionContext) {
        throw new IllegalStateException("BuilderExpression: Reading string array is not supported");
    }

    public boolean[] getBooleanArray(ExecutionContext executionContext) {
        throw new IllegalStateException("BuilderExpression: Reading boolean array is not supported");
    }

    public void addToScope(String name, StdScope scope) {
        throw new IllegalStateException("This type of builder expression cannot be added to a scope");
    }

    public void updateScope(StdScope scope, String name, ExecutionContext executionContext) {
        throw new IllegalStateException("This type of builder expression cannot update a scope");
    }

    public String getExpression() {
        return STATIC;
    }

    @Nonnull
    public static BuilderExpression fromOperand(@Nonnull ExecutionContext.Operand operand) {
        return switch (operand.type) {
            case ValueType.NUMBER -> new BuilderExpressionStaticNumber(operand.number);
            case ValueType.STRING -> new BuilderExpressionStaticString(operand.string);
            case ValueType.BOOLEAN -> new BuilderExpressionStaticBoolean(operand.bool);
            case ValueType.EMPTY_ARRAY -> BuilderExpressionStaticEmptyArray.INSTANCE;
            case ValueType.NUMBER_ARRAY -> new BuilderExpressionStaticNumberArray(operand.numberArray);
            case ValueType.STRING_ARRAY -> new BuilderExpressionStaticStringArray(operand.stringArray);
            case ValueType.BOOLEAN_ARRAY -> new BuilderExpressionStaticBooleanArray(operand.boolArray);
            default -> throw new IllegalStateException("Operand cannot be converted to builder expression");
        };
    }

    @Nonnull
    public static BuilderExpression fromJSON(@Nonnull JsonElement jsonElement, @Nonnull BuilderParameters builderParameters, boolean constantsOnly) {
        BuilderExpression builderExpression = BuilderExpression.fromJSON(jsonElement, builderParameters);
        if (constantsOnly && !builderExpression.isStatic()) {
            throw new IllegalArgumentException("Only constant string, number or boolean or arrays allowed, found: " + String.valueOf(jsonElement));
        }
        return builderExpression;
    }

    @Nonnull
    public static BuilderExpression fromJSON(@Nonnull JsonElement jsonElement, @Nonnull BuilderParameters builderParameters, ValueType expectedType) {
        BuilderExpression builderExpression = BuilderExpression.fromJSON(jsonElement, builderParameters);
        if (!ValueType.isAssignableType(builderExpression.getType(), expectedType)) {
            throw new IllegalStateException("Expression type mismatch. Got " + String.valueOf((Object)builderExpression.getType()) + " but expected " + String.valueOf((Object)expectedType) + " from: " + String.valueOf(jsonElement));
        }
        return builderExpression;
    }

    @Nonnull
    public static BuilderExpression fromJSON(@Nonnull JsonElement jsonElement, @Nonnull BuilderParameters builderParameters) {
        BuilderExpression result;
        if (jsonElement.isJsonObject()) {
            return BuilderExpressionDynamic.fromJSON(jsonElement, builderParameters);
        }
        if (jsonElement.isJsonPrimitive()) {
            BuilderExpression jsonPrimitive = BuilderExpression.readJSONPrimitive(jsonElement);
            if (jsonPrimitive != null) {
                return jsonPrimitive;
            }
        } else if (jsonElement.isJsonArray() && (result = BuilderExpression.readStaticArray(jsonElement)) != null) {
            return result;
        }
        throw new IllegalArgumentException("Illegal JSON value for expression: " + String.valueOf(jsonElement));
    }

    @Nullable
    private static BuilderExpression readJSONPrimitive(@Nonnull JsonElement jsonElement) {
        JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
        if (jsonPrimitive.isString()) {
            return new BuilderExpressionStaticString(jsonPrimitive.getAsString());
        }
        if (jsonPrimitive.isBoolean()) {
            return new BuilderExpressionStaticBoolean(jsonPrimitive.getAsBoolean());
        }
        if (jsonPrimitive.isNumber()) {
            return new BuilderExpressionStaticNumber(jsonPrimitive.getAsDouble());
        }
        return null;
    }

    @Nullable
    private static BuilderExpression readStaticArray(@Nonnull JsonElement jsonElement) {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.isEmpty()) {
            return BuilderExpressionStaticEmptyArray.INSTANCE;
        }
        JsonElement firstElement = jsonArray.get(0);
        BuilderExpression result = null;
        if (firstElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = firstElement.getAsJsonPrimitive();
            if (jsonPrimitive.isString()) {
                result = BuilderExpressionStaticStringArray.fromJSON(jsonArray);
            } else if (jsonPrimitive.isBoolean()) {
                result = BuilderExpressionStaticBooleanArray.fromJSON(jsonArray);
            } else if (jsonPrimitive.isNumber()) {
                result = BuilderExpressionStaticNumberArray.fromJSON(jsonArray);
            }
        }
        return result;
    }

    public void compile(BuilderParameters builderParameters) {
    }

    @Nonnull
    public static Schema toSchema(@Nonnull SchemaContext context) {
        return context.refDefinition(SchemaGenerator.INSTANCE);
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
            return "NPC:Type:BuilderExpression";
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            Schema s = new Schema();
            s.setTitle("Expression");
            s.setAnyOf(new ArraySchema(), new NumberSchema(), new StringSchema(), new BooleanSchema(), BuilderExpressionDynamic.toSchema());
            return s;
        }
    }
}

