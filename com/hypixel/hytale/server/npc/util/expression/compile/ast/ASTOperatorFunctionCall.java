/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util.expression.compile.ast;

import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import com.hypixel.hytale.server.npc.util.expression.compile.CompileContext;
import com.hypixel.hytale.server.npc.util.expression.compile.Token;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.AST;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperand;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperandIdentifier;
import com.hypixel.hytale.server.npc.util.expression.compile.ast.ASTOperator;
import java.text.ParseException;
import java.util.List;
import java.util.Stack;
import javax.annotation.Nonnull;

public class ASTOperatorFunctionCall
extends ASTOperator {
    private final String functionName;

    public ASTOperatorFunctionCall(@Nonnull ValueType returnType, String functionName, int tokenPosition) {
        super(returnType, Token.FUNCTION_CALL, tokenPosition);
        this.functionName = functionName;
        this.codeGen = scope -> ExecutionContext.genCALL(this.functionName, this.getArguments().size(), scope);
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    public static void fromParsedFunction(int argumentCount, @Nonnull CompileContext compileContext) throws ParseException {
        int firstArgument;
        int len;
        Stack<AST> operandStack = compileContext.getOperandStack();
        AST functionNameAST = (AST)operandStack.get((len = operandStack.size()) - argumentCount - 1);
        if (!(functionNameAST instanceof ASTOperandIdentifier)) {
            throw new ParseException("Expected identifier for function name but found type " + String.valueOf((Object)functionNameAST.getValueType()), functionNameAST.getTokenPosition());
        }
        ASTOperandIdentifier identifier = (ASTOperandIdentifier)functionNameAST;
        StringBuilder name = new StringBuilder(identifier.getIdentifier()).append('@');
        boolean isConstant = true;
        for (int i = firstArgument = len - argumentCount; i < len; ++i) {
            AST ast = (AST)operandStack.get(i);
            name.append(Scope.encodeType(ast.getValueType()));
            isConstant &= ast.isConstant();
        }
        String functionName = name.toString();
        Scope scope = compileContext.getScope();
        ValueType resultType = scope.getType(functionName);
        if (resultType == null) {
            throw new IllegalStateException("Unable to find function (or argument types are not matching):" + functionName);
        }
        if (isConstant &= scope.isConstant(functionName)) {
            List<ExecutionContext.Instruction> instructionList = compileContext.getInstructions();
            ExecutionContext executionContext = compileContext.getExecutionContext();
            instructionList.clear();
            for (int i = firstArgument; i < len; ++i) {
                ((AST)operandStack.get(i)).genCode(instructionList, null);
            }
            instructionList.add(ExecutionContext.genCALL(functionName, argumentCount, null));
            ValueType ret = executionContext.execute(instructionList, scope);
            if (ret == ValueType.VOID) {
                throw new IllegalStateException("Failed to evaluate constant function AST");
            }
            operandStack.setSize(firstArgument - 1);
            operandStack.push(ASTOperand.createFromOperand(functionNameAST.getToken(), functionNameAST.getTokenPosition(), executionContext.top()));
            return;
        }
        ASTOperatorFunctionCall function = new ASTOperatorFunctionCall(resultType, functionName, functionNameAST.getTokenPosition());
        for (int i = firstArgument; i < len; ++i) {
            function.addArgument((AST)operandStack.get(i));
        }
        operandStack.setSize(firstArgument - 1);
        operandStack.push(function);
    }
}

