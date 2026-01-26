/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.system;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.AbstractOptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import javax.annotation.Nonnull;

public abstract class WrappedArg<BasicType> {
    @Nonnull
    protected final Argument<?, BasicType> arg;

    public WrappedArg(@Nonnull Argument<?, BasicType> arg) {
        this.arg = arg;
    }

    public boolean provided(@Nonnull CommandContext context) {
        return this.arg.provided(context);
    }

    @Nonnull
    public String getName() {
        return this.arg.getName();
    }

    @Nonnull
    public String getDescription() {
        return this.arg.getDescription();
    }

    @Nonnull
    public <D extends WrappedArg<BasicType>> D addAliases(String ... aliases) {
        Argument<?, BasicType> argument = this.arg;
        if (!(argument instanceof AbstractOptionalArg)) {
            throw new UnsupportedOperationException("You are trying to add aliases to a wrapped arg that is wrapping a RequiredArgument. RequiredArguments do not accept aliases");
        }
        AbstractOptionalArg abstractOptionalArg = (AbstractOptionalArg)argument;
        abstractOptionalArg.addAliases(aliases);
        return (D)this;
    }

    @Nonnull
    public Argument<?, BasicType> getArg() {
        return this.arg;
    }

    protected BasicType get(@Nonnull CommandContext context) {
        return this.arg.get(context);
    }
}

