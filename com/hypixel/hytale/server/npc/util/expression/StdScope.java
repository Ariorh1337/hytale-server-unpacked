/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util.expression;

import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StdScope
implements Scope {
    protected static final SymbolStringArray VAR_EMPTY_STRING_ARRAY = new SymbolStringArray(false, () -> ArrayUtil.EMPTY_STRING_ARRAY);
    protected static final SymbolNumberArray VAR_EMPTY_NUMBER_ARRAY = new SymbolNumberArray(false, () -> ArrayUtil.EMPTY_DOUBLE_ARRAY);
    protected static final SymbolBooleanArray VAR_EMPTY_BOOLEAN_ARRAY = new SymbolBooleanArray(false, () -> ArrayUtil.EMPTY_BOOLEAN_ARRAY);
    protected static final SymbolStringArray VAR_NULL_STRING_ARRAY = new SymbolStringArray(false, () -> null);
    protected static final SymbolNumberArray VAR_NULL_NUMBER_ARRAY = new SymbolNumberArray(false, () -> null);
    protected static final SymbolBooleanArray VAR_NULL_BOOLEAN_ARRAY = new SymbolBooleanArray(false, () -> null);
    protected static final SymbolString VAR_NULL_STRING = new SymbolString(false, () -> null);
    protected static final SymbolString VAR_EMPTY_STRING = new SymbolString(false, () -> "");
    protected static final SymbolBoolean VAR_BOOLEAN_TRUE = new SymbolBoolean(false, () -> true);
    protected static final SymbolBoolean VAR_BOOLEAN_FALSE = new SymbolBoolean(false, () -> false);
    protected static final SymbolStringArray CONST_EMPTY_STRING_ARRAY = new SymbolStringArray(true, () -> ArrayUtil.EMPTY_STRING_ARRAY);
    protected static final SymbolNumberArray CONST_EMPTY_NUMBER_ARRAY = new SymbolNumberArray(true, () -> ArrayUtil.EMPTY_DOUBLE_ARRAY);
    protected static final SymbolBooleanArray CONST_EMPTY_BOOLEAN_ARRAY = new SymbolBooleanArray(true, () -> ArrayUtil.EMPTY_BOOLEAN_ARRAY);
    protected static final SymbolStringArray CONST_NULL_STRING_ARRAY = new SymbolStringArray(true, () -> null);
    protected static final SymbolNumberArray CONST_NULL_NUMBER_ARRAY = new SymbolNumberArray(true, () -> null);
    protected static final SymbolBooleanArray CONST_NULL_BOOLEAN_ARRAY = new SymbolBooleanArray(true, () -> null);
    protected static final SymbolString CONST_NULL_STRING = new SymbolString(true, () -> null);
    protected static final SymbolString CONST_EMPTY_STRING = new SymbolString(true, () -> "");
    protected static final SymbolBoolean CONST_BOOLEAN_TRUE = new SymbolBoolean(true, () -> true);
    protected static final SymbolBoolean CONST_BOOLEAN_FALSE = new SymbolBoolean(true, () -> false);
    protected Scope parent;
    protected Map<String, Symbol> symbolTable;

    public StdScope(Scope parent) {
        this.parent = parent;
        this.symbolTable = new HashMap<String, Symbol>();
    }

    @Nonnull
    public static StdScope copyOf(@Nonnull StdScope other) {
        StdScope scope = new StdScope(other.parent);
        scope.mergeSymbols(other);
        return scope;
    }

    @Nonnull
    public StdScope merge(@Nonnull StdScope other) {
        this.mergeSymbols(other);
        return this;
    }

    @Nonnull
    public static StdScope mergeScopes(@Nonnull StdScope first, @Nonnull StdScope second) {
        return StdScope.copyOf(first).merge(second);
    }

    protected void mergeSymbols(@Nonnull StdScope other) {
        other.symbolTable.forEach(this::add);
    }

    protected void add(String name, Symbol symbol) {
        if (this.symbolTable.containsKey(name)) {
            throw new IllegalStateException("Trying to add symbol twice to scope " + name);
        }
        this.symbolTable.put(name, symbol);
    }

    public void addConst(String name, @Nullable String value) {
        if (value == null) {
            this.add(name, CONST_NULL_STRING);
        } else if (value.isEmpty()) {
            this.add(name, CONST_EMPTY_STRING);
        } else {
            this.add(name, new SymbolString(true, () -> value));
        }
    }

    public void addConst(String name, double value) {
        this.add(name, new SymbolNumber(true, () -> value));
    }

    public void addConst(String name, boolean value) {
        this.add(name, value ? CONST_BOOLEAN_TRUE : CONST_BOOLEAN_FALSE);
    }

    public void addConst(String name, @Nullable String[] value) {
        if (value == null) {
            this.add(name, CONST_NULL_STRING_ARRAY);
        } else if (value.length == 0) {
            this.add(name, CONST_EMPTY_STRING_ARRAY);
        } else {
            this.add(name, new SymbolStringArray(true, () -> value));
        }
    }

    public void addConst(String name, @Nullable double[] value) {
        if (value == null) {
            this.add(name, CONST_NULL_NUMBER_ARRAY);
        } else if (value.length == 0) {
            this.add(name, CONST_EMPTY_NUMBER_ARRAY);
        } else {
            this.add(name, new SymbolNumberArray(true, () -> value));
        }
    }

    public void addConst(String name, @Nullable boolean[] value) {
        if (value == null) {
            this.add(name, CONST_NULL_BOOLEAN_ARRAY);
        } else if (value.length == 0) {
            this.add(name, CONST_EMPTY_BOOLEAN_ARRAY);
        } else {
            this.add(name, new SymbolBooleanArray(true, () -> value));
        }
    }

    public void addConstEmptyArray(String name) {
        this.add(name, new Symbol(true, ValueType.EMPTY_ARRAY));
    }

    public void addVar(String name, @Nullable String value) {
        if (value == null) {
            this.add(name, VAR_NULL_STRING);
        } else if (value.isEmpty()) {
            this.add(name, VAR_EMPTY_STRING);
        } else {
            this.add(name, new SymbolString(false, () -> value));
        }
    }

    public void addVar(String name, double value) {
        this.add(name, new SymbolNumber(false, () -> value));
    }

    public void addVar(String name, boolean value) {
        this.add(name, value ? VAR_BOOLEAN_TRUE : VAR_BOOLEAN_FALSE);
    }

    public void addVar(String name, @Nullable String[] value) {
        if (value == null) {
            this.add(name, VAR_NULL_STRING_ARRAY);
        } else if (value.length == 0) {
            this.add(name, VAR_EMPTY_STRING_ARRAY);
        } else {
            this.add(name, new SymbolStringArray(false, () -> value));
        }
    }

    public void addVar(String name, @Nullable double[] value) {
        if (value == null) {
            this.add(name, VAR_NULL_NUMBER_ARRAY);
        } else if (value.length == 0) {
            this.add(name, VAR_EMPTY_NUMBER_ARRAY);
        } else {
            this.add(name, new SymbolNumberArray(false, () -> value));
        }
    }

    public void addVar(String name, @Nullable boolean[] value) {
        if (value == null) {
            this.add(name, VAR_NULL_BOOLEAN_ARRAY);
        } else if (value.length == 0) {
            this.add(name, VAR_EMPTY_BOOLEAN_ARRAY);
        } else {
            this.add(name, new SymbolBooleanArray(false, () -> value));
        }
    }

    public void addInvariant(@Nonnull String name, Scope.Function function, ValueType returnType, ValueType ... argumentTypes) {
        this.add(Scope.encodeFunctionName(name, argumentTypes), new SymbolFunction(true, returnType, function));
        this.add(name, new SymbolFunction(false, returnType, null));
    }

    public void addVariant(@Nonnull String name, Scope.Function function, ValueType returnType, ValueType ... argumentTypes) {
        this.add(Scope.encodeFunctionName(name, argumentTypes), new SymbolFunction(false, returnType, function));
        this.add(name, new SymbolFunction(false, returnType, null));
    }

    public void addSupplier(String name, Supplier<String> value) {
        this.add(name, new SymbolString(false, value));
    }

    public void addSupplier(String name, DoubleSupplier value) {
        this.add(name, new SymbolNumber(false, value));
    }

    public void addSupplier(String name, BooleanSupplier value) {
        this.add(name, new SymbolBoolean(false, value));
    }

    public void addStringArraySupplier(String name, Supplier<String[]> value) {
        this.add(name, new SymbolStringArray(false, value));
    }

    public void addDoubleArraySupplier(String name, Supplier<double[]> value) {
        this.add(name, new SymbolNumberArray(false, value));
    }

    public void addBooleanArraySupplier(String name, Supplier<boolean[]> value) {
        this.add(name, new SymbolBooleanArray(false, value));
    }

    protected Symbol get(String name) {
        return this.symbolTable.get(name);
    }

    @Nonnull
    protected Symbol get(String name, ValueType valueType) {
        Symbol symbol = this.symbolTable.get(name);
        if (symbol == null) {
            throw new IllegalStateException("Can't find symbol " + name + " in symbol table");
        }
        if (!ValueType.isAssignableType(valueType, symbol.valueType)) {
            throw new IllegalStateException("Type mismatch with " + name + ". Got " + String.valueOf((Object)valueType) + " but expected " + String.valueOf((Object)symbol.valueType));
        }
        return symbol;
    }

    protected void replace(String name, @Nonnull Symbol symbol) {
        Symbol oldSymbol = this.get(name, symbol.valueType);
        if (oldSymbol.isConstant) {
            throw new IllegalStateException("Can't replace a constant in symbol table: " + name);
        }
        if (symbol.isConstant) {
            throw new IllegalStateException("Can't replace a variable with a constant: " + name);
        }
        this.symbolTable.put(name, symbol);
    }

    public void changeValue(String name, @Nullable String value) {
        if (value == null) {
            this.replace(name, VAR_NULL_STRING);
        } else if (value.isEmpty()) {
            this.replace(name, VAR_EMPTY_STRING);
        } else {
            this.replace(name, new SymbolString(false, () -> value));
        }
    }

    public void changeValue(String name, double value) {
        this.replace(name, new SymbolNumber(false, () -> value));
    }

    public void changeValue(String name, boolean value) {
        this.replace(name, value ? VAR_BOOLEAN_TRUE : VAR_BOOLEAN_FALSE);
    }

    public void changeValue(String name, @Nullable String[] value) {
        if (value == null) {
            this.replace(name, VAR_NULL_STRING_ARRAY);
        } else if (value.length == 0) {
            this.replace(name, VAR_EMPTY_STRING_ARRAY);
        } else {
            this.replace(name, new SymbolStringArray(false, () -> value));
        }
    }

    public void changeValue(String name, @Nullable double[] value) {
        if (value == null) {
            this.replace(name, VAR_NULL_NUMBER_ARRAY);
        } else if (value.length == 0) {
            this.replace(name, VAR_EMPTY_NUMBER_ARRAY);
        } else {
            this.replace(name, new SymbolNumberArray(false, () -> value));
        }
    }

    public void changeValue(String name, @Nullable boolean[] value) {
        if (value == null) {
            this.replace(name, VAR_NULL_BOOLEAN_ARRAY);
        } else if (value.length == 0) {
            this.replace(name, VAR_EMPTY_BOOLEAN_ARRAY);
        } else {
            this.replace(name, new SymbolBooleanArray(false, () -> value));
        }
    }

    public void changeValueToEmptyArray(String name) {
        Symbol symbol = this.get(name);
        Objects.requireNonNull(symbol, "Can't find symbol in symbol table in changeValue()");
        if (symbol.isConstant) {
            throw new IllegalStateException("Can't replace a constant in symbol table: " + name);
        }
        switch (symbol.valueType) {
            default: {
                throw new IllegalStateException("Can't assign an empty array to symbol " + name + "  of type " + String.valueOf((Object)symbol.valueType));
            }
            case EMPTY_ARRAY: {
                return;
            }
            case NUMBER_ARRAY: {
                this.symbolTable.put(name, VAR_EMPTY_NUMBER_ARRAY);
                break;
            }
            case STRING_ARRAY: {
                this.symbolTable.put(name, VAR_EMPTY_STRING_ARRAY);
                break;
            }
            case BOOLEAN_ARRAY: {
                this.symbolTable.put(name, VAR_EMPTY_BOOLEAN_ARRAY);
            }
        }
    }

    @Override
    public Supplier<String> getStringSupplier(String name) {
        Symbol symbol = this.get(name);
        if (symbol == null) {
            if (this.parent != null) {
                return this.parent.getStringSupplier(name);
            }
            throw new IllegalStateException("Unable to find symbol: " + name);
        }
        if (symbol instanceof SymbolString) {
            return ((SymbolString)symbol).value;
        }
        throw new IllegalStateException("Symbol is not a string: " + name);
    }

    @Override
    public DoubleSupplier getNumberSupplier(String name) {
        Symbol symbol = this.get(name);
        if (symbol == null) {
            if (this.parent != null) {
                return this.parent.getNumberSupplier(name);
            }
            throw new IllegalStateException("Unable to find symbol: " + name);
        }
        if (symbol instanceof SymbolNumber) {
            return ((SymbolNumber)symbol).value;
        }
        throw new IllegalStateException("Symbol is not a number: " + name);
    }

    @Override
    public BooleanSupplier getBooleanSupplier(String name) {
        Symbol symbol = this.get(name);
        if (symbol == null) {
            if (this.parent != null) {
                return this.parent.getBooleanSupplier(name);
            }
            throw new IllegalStateException("Unable to find symbol: " + name);
        }
        if (symbol instanceof SymbolBoolean) {
            return ((SymbolBoolean)symbol).value;
        }
        throw new IllegalStateException("Symbol is not a boolean: " + name);
    }

    @Override
    public Supplier<String[]> getStringArraySupplier(String name) {
        Symbol symbol = this.get(name);
        if (symbol == null) {
            if (this.parent != null) {
                return this.parent.getStringArraySupplier(name);
            }
            throw new IllegalStateException("Unable to find symbol: " + name);
        }
        if (symbol.valueType == ValueType.EMPTY_ARRAY) {
            return () -> ArrayUtil.EMPTY_STRING_ARRAY;
        }
        if (symbol instanceof SymbolStringArray) {
            return ((SymbolStringArray)symbol).value;
        }
        throw new IllegalStateException("Symbol is not a string array: " + name);
    }

    @Override
    public Supplier<double[]> getNumberArraySupplier(String name) {
        Symbol symbol = this.get(name);
        if (symbol == null) {
            if (this.parent != null) {
                return this.parent.getNumberArraySupplier(name);
            }
            throw new IllegalStateException("Unable to find symbol: " + name);
        }
        if (symbol.valueType == ValueType.EMPTY_ARRAY) {
            return () -> ArrayUtil.EMPTY_DOUBLE_ARRAY;
        }
        if (symbol instanceof SymbolNumberArray) {
            return ((SymbolNumberArray)symbol).value;
        }
        throw new IllegalStateException("Symbol is not a number array: " + name);
    }

    @Override
    public Supplier<boolean[]> getBooleanArraySupplier(String name) {
        Symbol symbol = this.get(name);
        if (symbol == null) {
            if (this.parent != null) {
                return this.parent.getBooleanArraySupplier(name);
            }
            throw new IllegalStateException("Unable to find symbol: " + name);
        }
        if (symbol.valueType == ValueType.EMPTY_ARRAY) {
            return () -> ArrayUtil.EMPTY_BOOLEAN_ARRAY;
        }
        if (symbol instanceof SymbolBooleanArray) {
            return ((SymbolBooleanArray)symbol).value;
        }
        throw new IllegalStateException("Symbol is not a boolean array: " + name);
    }

    @Override
    public Scope.Function getFunction(String name) {
        Symbol symbol = this.get(name);
        if (symbol == null) {
            if (this.parent != null) {
                return this.parent.getFunction(name);
            }
            throw new IllegalStateException("Unable to find function: " + name);
        }
        if (symbol instanceof SymbolFunction) {
            return ((SymbolFunction)symbol).value;
        }
        throw new IllegalStateException("Symbol is not a function: " + name);
    }

    @Override
    public boolean isConstant(String name) {
        Symbol symbol = this.get(name);
        if (symbol != null) {
            return symbol.isConstant;
        }
        if (this.parent == null) {
            throw new IllegalStateException("Unable to find symbol: " + name);
        }
        return this.parent.isConstant(name);
    }

    @Override
    @Nullable
    public ValueType getType(String name) {
        Symbol symbol = this.get(name);
        if (symbol != null) {
            return symbol.valueType;
        }
        if (this.parent != null) {
            return this.parent.getType(name);
        }
        return null;
    }

    protected static class SymbolString
    extends Symbol {
        public final Supplier<String> value;

        public SymbolString(boolean isConstant, Supplier<String> value) {
            super(isConstant, ValueType.STRING);
            this.value = value;
        }
    }

    protected static class Symbol {
        public final boolean isConstant;
        public final ValueType valueType;

        public Symbol(boolean isConstant, ValueType valueType) {
            this.isConstant = isConstant;
            this.valueType = valueType;
        }
    }

    protected static class SymbolNumber
    extends Symbol {
        public final DoubleSupplier value;

        public SymbolNumber(boolean isConstant, DoubleSupplier value) {
            super(isConstant, ValueType.NUMBER);
            this.value = value;
        }
    }

    protected static class SymbolBoolean
    extends Symbol {
        public final BooleanSupplier value;

        public SymbolBoolean(boolean isConstant, BooleanSupplier value) {
            super(isConstant, ValueType.BOOLEAN);
            this.value = value;
        }
    }

    protected static class SymbolStringArray
    extends Symbol {
        public final Supplier<String[]> value;

        public SymbolStringArray(boolean isConstant, Supplier<String[]> value) {
            super(isConstant, ValueType.STRING_ARRAY);
            this.value = value;
        }
    }

    protected static class SymbolNumberArray
    extends Symbol {
        public final Supplier<double[]> value;

        public SymbolNumberArray(boolean isConstant, Supplier<double[]> value) {
            super(isConstant, ValueType.NUMBER_ARRAY);
            this.value = value;
        }
    }

    protected static class SymbolBooleanArray
    extends Symbol {
        public final Supplier<boolean[]> value;

        public SymbolBooleanArray(boolean isConstant, Supplier<boolean[]> value) {
            super(isConstant, ValueType.BOOLEAN_ARRAY);
            this.value = value;
        }
    }

    protected static class SymbolFunction
    extends Symbol {
        public final Scope.Function value;

        public SymbolFunction(boolean isConstant, ValueType returnType, Scope.Function value) {
            super(isConstant, returnType);
            this.value = value;
        }
    }
}

