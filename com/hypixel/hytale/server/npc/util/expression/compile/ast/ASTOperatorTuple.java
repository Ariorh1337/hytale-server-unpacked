/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util.expression.compile.ast;

import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import com.hypixel.hytale.server.npc.util.expression.compile.CompileContext;
import com.hypixel.hytale.server.npc.util.expression.compile.Parser;
import com.hypixel.hytale.server.npc.util.expression.compile.Token;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.AST;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperand;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperandBooleanArray;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperandEmptyArray;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperandNumberArray;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperandStringArray;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperator;
import java.util.Stack;
import javax.annotation.Nonnull;

public class ASTOperatorTuple
extends ASTOperator {
    public ASTOperatorTuple(@Nonnull ValueType arrayType, @Nonnull Token token, int tokenPosition) {
        super(arrayType, token, tokenPosition);
        this.codeGen = scope -> ExecutionContext.genPACK(this.getValueType(), this.getArguments().size());
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    public static void fromParsedTuple(@Nonnull Parser.ParsedToken openingToken, int argumentCount, @Nonnull CompileContext compileContext) {
        Token token = openingToken.token;
        if (token != Token.OPEN_SQUARE_BRACKET) {
            throw new IllegalStateException("Bad opening bracket for tuple: " + token.get());
        }
        int tokenPosition = openingToken.tokenPosition;
        Stack<AST> operandStack = compileContext.getOperandStack();
        if (argumentCount == 0) {
            operandStack.push(new ASTOperandEmptyArray(token, tokenPosition));
            return;
        }
        int len = operandStack.size();
        int firstArgument = len - argumentCount;
        ValueType argumentType = ((AST)operandStack.get(firstArgument)).getValueType();
        ValueType arrayType = switch (argumentType) {
            case ValueType.NUMBER -> ValueType.NUMBER_ARRAY;
            case ValueType.STRING -> ValueType.STRING_ARRAY;
            case ValueType.BOOLEAN -> ValueType.BOOLEAN_ARRAY;
            default -> throw new IllegalStateException("Invalid type in array: " + String.valueOf((Object)argumentType));
        };
        boolean isConstant = true;
        for (int i = firstArgument; i < len; ++i) {
            AST ast = (AST)operandStack.get(i);
            isConstant &= ast.isConstant();
            if (ast.getValueType() == argumentType) continue;
            throw new IllegalStateException("Mismatching types in array. Expected " + String.valueOf((Object)argumentType) + ", found " + String.valueOf((Object)ast.getValueType()));
        }
        if (isConstant) {
            ASTOperand item = switch (arrayType) {
                case ValueType.NUMBER_ARRAY -> new ASTOperandNumberArray(token, tokenPosition, operandStack, firstArgument, argumentCount);
                case ValueType.STRING_ARRAY -> new ASTOperandStringArray(token, tokenPosition, operandStack, firstArgument, argumentCount);
                case ValueType.BOOLEAN_ARRAY -> new ASTOperandBooleanArray(token, tokenPosition, operandStack, firstArgument, argumentCount);
                default -> throw new IllegalStateException("Unexpected array type when creating constant array: " + String.valueOf((Object)arrayType));
            };
            operandStack.setSize(firstArgument);
            operandStack.push(item);
            return;
        }
        ASTOperatorTuple ast = new ASTOperatorTuple(arrayType, token, tokenPosition);
        for (int i = firstArgument; i < len; ++i) {
            ast.addArgument((AST)operandStack.get(i));
        }
        operandStack.setSize(firstArgument);
        operandStack.push(ast);
    }
}

