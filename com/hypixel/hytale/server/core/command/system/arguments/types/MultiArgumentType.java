/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.MultiArgumentContext;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.WrappedArgumentType;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MultiArgumentType<DataType>
extends ArgumentType<DataType> {
    @Nonnull
    private final Map<String, SingleArgumentType<?>> argumentValues = new Object2ObjectLinkedOpenHashMap();

    public MultiArgumentType(@Nonnull String name, @Nonnull String argumentUsage, String ... examples) {
        super(name, argumentUsage, 0, examples);
    }

    @Nonnull
    protected <D> WrappedArgumentType<D> withParameter(@Nonnull String name, @Nonnull String usage, @Nonnull SingleArgumentType<D> argumentType) {
        WrappedArgumentType<D> wrappedArgumentType = argumentType.withOverriddenUsage(usage);
        if (this.argumentValues.containsKey(name = name.toLowerCase())) {
            throw new IllegalArgumentException("Cannot register two MultiArgumentType parameters with the same name");
        }
        this.argumentValues.put(name, wrappedArgumentType);
        this.numberOfParameters = this.argumentValues.size();
        return wrappedArgumentType;
    }

    @Override
    @Nullable
    public DataType parse(@Nonnull String[] input, @Nonnull ParseResult parseResult) {
        MultiArgumentContext multiArgumentContext = new MultiArgumentContext();
        int endOfLastIndex = 0;
        for (SingleArgumentType<?> argumentValue : this.argumentValues.values()) {
            multiArgumentContext.registerArgumentValues(argumentValue, Arrays.copyOfRange(input, endOfLastIndex, endOfLastIndex + argumentValue.numberOfParameters), parseResult);
            if (parseResult.failed()) {
                return null;
            }
            endOfLastIndex += argumentValue.numberOfParameters;
        }
        return this.parse(multiArgumentContext, parseResult);
    }

    @Nullable
    public abstract DataType parse(@Nonnull MultiArgumentContext var1, @Nonnull ParseResult var2);
}

