/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.system;

import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import com.hypixel.hytale.server.core.command.system.arguments.system.WrappedArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import java.util.function.Function;
import javax.annotation.Nonnull;

public record ArgWrapper<W extends WrappedArg<BasicType>, BasicType>(@Nonnull ArgumentType<BasicType> argumentType, @Nonnull Function<Argument<?, BasicType>, W> wrappedArgProviderFunction) {
    public W wrapArg(@Nonnull Argument<?, BasicType> argument) {
        return (W)((WrappedArg)this.wrappedArgProviderFunction.apply(argument));
    }
}

