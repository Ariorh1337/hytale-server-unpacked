/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.expression;

import com.hypixel.hytale.server.npc.asset.builder.expression.BuilderExpressionDynamic;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.StdScope;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import javax.annotation.Nonnull;

public class BuilderExpressionDynamicBoolean
extends BuilderExpressionDynamic {
    public BuilderExpressionDynamicBoolean(String expression, ExecutionContext.Instruction[] instructionSequence) {
        super(expression, instructionSequence);
    }

    @Override
    @Nonnull
    public ValueType getType() {
        return ValueType.BOOLEAN;
    }

    @Override
    public boolean getBoolean(@Nonnull ExecutionContext executionContext) {
        this.execute(executionContext);
        return executionContext.popBoolean();
    }

    @Override
    public void updateScope(@Nonnull StdScope scope, String name, @Nonnull ExecutionContext executionContext) {
        scope.changeValue(name, this.getBoolean(executionContext));
    }
}

