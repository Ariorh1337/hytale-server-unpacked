/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util.expression;

import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.ValueType;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Scope {
    public Supplier<String> getStringSupplier(String var1);

    public DoubleSupplier getNumberSupplier(String var1);

    public BooleanSupplier getBooleanSupplier(String var1);

    public Supplier<String[]> getStringArraySupplier(String var1);

    public Supplier<double[]> getNumberArraySupplier(String var1);

    public Supplier<boolean[]> getBooleanArraySupplier(String var1);

    public Function getFunction(String var1);

    default public String getString(String name) {
        return this.getStringSupplier(name).get();
    }

    default public double getNumber(String name) {
        return this.getNumberSupplier(name).getAsDouble();
    }

    default public boolean getBoolean(String name) {
        return this.getBooleanSupplier(name).getAsBoolean();
    }

    default public String[] getStringArray(String name) {
        return this.getStringArraySupplier(name).get();
    }

    default public double[] getNumberArray(String name) {
        return this.getNumberArraySupplier(name).get();
    }

    default public boolean[] getBooleanArray(String name) {
        return this.getBooleanArraySupplier(name).get();
    }

    public boolean isConstant(String var1);

    @Nullable
    public ValueType getType(String var1);

    @Nonnull
    public static String encodeFunctionName(@Nonnull String name, @Nonnull ValueType[] values) {
        StringBuilder stringBuilder = new StringBuilder(name).append('@');
        for (int i = 0; i < values.length; ++i) {
            stringBuilder.append(Scope.encodeType(values[i]));
        }
        return stringBuilder.toString();
    }

    public static char encodeType(@Nonnull ValueType type) {
        return switch (type) {
            case ValueType.NUMBER -> 'n';
            case ValueType.STRING -> 's';
            case ValueType.BOOLEAN -> 'b';
            case ValueType.NUMBER_ARRAY -> 'N';
            case ValueType.STRING_ARRAY -> 'S';
            case ValueType.BOOLEAN_ARRAY -> 'B';
            default -> throw new IllegalStateException("Type cannot be encoded for function name: " + String.valueOf((Object)type));
        };
    }

    @FunctionalInterface
    public static interface Function {
        public void call(ExecutionContext var1, int var2);
    }
}

