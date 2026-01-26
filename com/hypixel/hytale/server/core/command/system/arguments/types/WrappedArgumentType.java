/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.MultiArgumentContext;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WrappedArgumentType<DataType>
extends SingleArgumentType<DataType> {
    protected final ArgumentType<DataType> wrappedArgumentType;

    public WrappedArgumentType(Message name, ArgumentType<DataType> wrappedArgumentType, @Nonnull String argumentUsage, String ... examples) {
        super(name, argumentUsage, examples);
        this.wrappedArgumentType = wrappedArgumentType;
    }

    @Override
    @Nonnull
    public String[] getExamples() {
        if (Arrays.equals(this.examples, EMPTY_EXAMPLES)) {
            return this.wrappedArgumentType.getExamples();
        }
        return this.examples;
    }

    @Nullable
    public DataType get(@Nonnull MultiArgumentContext context) {
        return context.get(this);
    }
}

