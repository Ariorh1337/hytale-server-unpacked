/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.WrappedArgumentType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SingleArgumentType<DataType>
extends ArgumentType<DataType> {
    protected SingleArgumentType(Message name, @Nonnull String argumentUsage, String ... examples) {
        super(name, Message.translation(argumentUsage), 1, examples);
    }

    protected SingleArgumentType(String name, @Nonnull Message argumentUsage, String ... examples) {
        super(name, argumentUsage, 1, examples);
    }

    public SingleArgumentType(String name, @Nonnull String argumentUsage, String ... examples) {
        super(name, argumentUsage, 1, examples);
    }

    @Override
    @Nullable
    public DataType parse(@Nonnull String[] input, @Nonnull ParseResult parseResult) {
        return this.parse(input[0], parseResult);
    }

    @Nullable
    public abstract DataType parse(String var1, ParseResult var2);

    @Nonnull
    public WrappedArgumentType<DataType> withOverriddenUsage(@Nonnull String usage, String ... examples) {
        return new WrappedArgumentType<DataType>(this, this.getName(), this, usage, examples){

            @Override
            public DataType parse(String input, ParseResult parseResult) {
                return this.wrappedArgumentType.parse(new String[]{input}, parseResult);
            }
        };
    }

    @Nonnull
    public WrappedArgumentType<DataType> withOverriddenUsage(@Nonnull String usage) {
        return new WrappedArgumentType<DataType>(this, this.getName(), this, usage, this.examples){

            @Override
            public DataType parse(String input, ParseResult parseResult) {
                return this.wrappedArgumentType.parse(new String[]{input}, parseResult);
            }
        };
    }
}

