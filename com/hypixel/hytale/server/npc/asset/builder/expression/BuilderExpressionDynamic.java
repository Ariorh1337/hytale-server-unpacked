/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.expression;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpression;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamicBoolean;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamicBooleanArray;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamicNumber;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamicNumberArray;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamicString;
import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamicStringArray;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class BuilderExpressionDynamic
extends BuilderExpression {
    public static final String KEY_COMPUTE = "Compute";
    private final String expression;
    private final ExecutionContext.Instruction[] instructionSequence;

    public BuilderExpressionDynamic(String expression, ExecutionContext.Instruction[] instructionSequence) {
        this.expression = expression;
        this.instructionSequence = instructionSequence;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public String getExpression() {
        return this.expression;
    }

    protected void execute(@Nonnull ExecutionContext executionContext) {
        Objects.requireNonNull(executionContext, "ExecutionContext not initialised");
        if (executionContext.execute(this.instructionSequence) != this.getType()) {
            throw new IllegalStateException("Expression returned wrong type " + String.valueOf((Object)executionContext.getType()) + " but expected " + String.valueOf((Object)this.getType()) + ": " + this.expression);
        }
    }

    @Nonnull
    public static BuilderExpression fromJSON(@Nonnull JsonElement jsonElement, @Nonnull BuilderParameters builderParameters) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement computeValue = jsonObject.get(KEY_COMPUTE);
        if (computeValue == null) {
            throw new IllegalArgumentException("JSON expression missing 'Compute' member: " + String.valueOf(jsonElement));
        }
        String expression = BuilderBase.expectStringElement(computeValue, KEY_COMPUTE);
        ValueType type = builderParameters.compile(expression);
        ExecutionContext.Operand operand = builderParameters.getConstantOperand();
        if (operand != null) {
            return BuilderExpression.fromOperand(operand);
        }
        ExecutionContext.Instruction[] instructionSequence = (ExecutionContext.Instruction[])builderParameters.getInstructions().toArray(ExecutionContext.Instruction[]::new);
        return switch (type) {
            case ValueType.NUMBER -> new BuilderExpressionDynamicNumber(expression, instructionSequence);
            case ValueType.STRING -> new BuilderExpressionDynamicString(expression, instructionSequence);
            case ValueType.BOOLEAN -> new BuilderExpressionDynamicBoolean(expression, instructionSequence);
            case ValueType.NUMBER_ARRAY -> new BuilderExpressionDynamicNumberArray(expression, instructionSequence);
            case ValueType.STRING_ARRAY -> new BuilderExpressionDynamicStringArray(expression, instructionSequence);
            case ValueType.BOOLEAN_ARRAY -> new BuilderExpressionDynamicBooleanArray(expression, instructionSequence);
            default -> throw new IllegalStateException("Unable to create dynamic expression from type " + String.valueOf((Object)type));
        };
    }

    @Nonnull
    public static Schema toSchema() {
        ObjectSchema s = new ObjectSchema();
        s.setTitle("ExpressionDynamic");
        s.setProperties(Map.of(KEY_COMPUTE, new StringSchema()));
        s.setRequired(KEY_COMPUTE);
        s.setAdditionalProperties(false);
        return s;
    }

    @Nonnull
    public static Schema computableSchema(Schema toWrap) {
        return Schema.anyOf(toWrap, BuilderExpressionDynamic.toSchema());
    }
}

