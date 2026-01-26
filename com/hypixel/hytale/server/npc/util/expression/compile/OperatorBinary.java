/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util.expression.compile;

import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import com.hypixel.hytale.server.npc.util.expression.compile.Token;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OperatorBinary {
    private Token token;
    private ValueType lhs;
    private ValueType rhs;
    private ValueType result;
    private Function<Scope, ExecutionContext.Instruction> codeGen;
    @Nonnull
    private static OperatorBinary[] operators = new OperatorBinary[]{OperatorBinary.of(Token.EXPONENTIATION, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.EXPONENTIATION), OperatorBinary.of(Token.REMAINDER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.REMAINDER), OperatorBinary.of(Token.DIVIDE, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.DIVIDE), OperatorBinary.of(Token.MULTIPLY, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.MULTIPLY), OperatorBinary.of(Token.MINUS, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.MINUS), OperatorBinary.of(Token.PLUS, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.PLUS), OperatorBinary.of(Token.GREATER_EQUAL, ValueType.NUMBER, ValueType.NUMBER, ValueType.BOOLEAN, scope -> ExecutionContext.GREATER_EQUAL), OperatorBinary.of(Token.GREATER, ValueType.NUMBER, ValueType.NUMBER, ValueType.BOOLEAN, scope -> ExecutionContext.GREATER), OperatorBinary.of(Token.LESS_EQUAL, ValueType.NUMBER, ValueType.NUMBER, ValueType.BOOLEAN, scope -> ExecutionContext.LESS_EQUAL), OperatorBinary.of(Token.LESS, ValueType.NUMBER, ValueType.NUMBER, ValueType.BOOLEAN, scope -> ExecutionContext.LESS), OperatorBinary.of(Token.NOT_EQUAL, ValueType.NUMBER, ValueType.NUMBER, ValueType.BOOLEAN, scope -> ExecutionContext.NOT_EQUAL), OperatorBinary.of(Token.EQUAL, ValueType.NUMBER, ValueType.NUMBER, ValueType.BOOLEAN, scope -> ExecutionContext.EQUAL), OperatorBinary.of(Token.NOT_EQUAL, ValueType.BOOLEAN, ValueType.BOOLEAN, ValueType.BOOLEAN, scope -> ExecutionContext.NOT_EQUAL_BOOL), OperatorBinary.of(Token.EQUAL, ValueType.BOOLEAN, ValueType.BOOLEAN, ValueType.BOOLEAN, scope -> ExecutionContext.EQUAL_BOOL), OperatorBinary.of(Token.BITWISE_AND, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.BITWISE_AND), OperatorBinary.of(Token.BITWISE_XOR, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.BITWISE_XOR), OperatorBinary.of(Token.BITWISE_OR, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, scope -> ExecutionContext.BITWISE_OR), OperatorBinary.of(Token.LOGICAL_AND, ValueType.BOOLEAN, ValueType.BOOLEAN, ValueType.BOOLEAN, scope -> ExecutionContext.LOGICAL_AND), OperatorBinary.of(Token.LOGICAL_OR, ValueType.BOOLEAN, ValueType.BOOLEAN, ValueType.BOOLEAN, scope -> ExecutionContext.LOGICAL_OR)};

    private OperatorBinary(Token token, ValueType lhs, ValueType rhs, ValueType result, Function<Scope, ExecutionContext.Instruction> codeGen) {
        this.token = token;
        this.lhs = lhs;
        this.rhs = rhs;
        this.result = result;
        this.codeGen = codeGen;
    }

    public ValueType getResultType() {
        return this.result;
    }

    public Function<Scope, ExecutionContext.Instruction> getCodeGen() {
        return this.codeGen;
    }

    @Nonnull
    private static OperatorBinary of(Token token, ValueType lhs, ValueType rhs, ValueType result, Function<Scope, ExecutionContext.Instruction> codeGen) {
        return new OperatorBinary(token, lhs, rhs, result, codeGen);
    }

    @Nullable
    public static OperatorBinary findOperator(Token token, ValueType lhs, ValueType rhs) {
        for (OperatorBinary op : operators) {
            if (op.token != token || op.lhs != lhs || op.rhs != rhs) continue;
            return op;
        }
        return null;
    }
}

