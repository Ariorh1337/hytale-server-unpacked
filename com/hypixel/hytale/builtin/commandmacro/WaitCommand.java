/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.commandmacro;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class WaitCommand
extends AbstractAsyncCommand {
    private static final long MILLISECONDS_TO_SECONDS_MULTIPLIER = 1000L;
    public static final Runnable EMPTY_RUNNABLE = () -> {};
    private final RequiredArg<Float> timeArg = (RequiredArg)((RequiredArg)this.withRequiredArg("time", "server.commands.wait.arg.time", ArgTypes.FLOAT).addValidator(Validators.greaterThan(Float.valueOf(0.0f)))).addValidator(Validators.lessThan(Float.valueOf(1000.0f)));
    private final FlagArg printArg = this.withFlagArg("print", "server.commands.wait.arg.print");

    public WaitCommand() {
        super("wait", "server.commands.wait.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        CommandSender sender = context.sender();
        Runnable runnable = (Boolean)this.printArg.get(context) != false ? () -> sender.sendMessage(Message.translation("server.commands.wait.complete")) : EMPTY_RUNNABLE;
        return CompletableFuture.runAsync(runnable, CompletableFuture.delayedExecutor((long)(((Float)this.timeArg.get(context)).floatValue() * 1000.0f), TimeUnit.MILLISECONDS));
    }
}

