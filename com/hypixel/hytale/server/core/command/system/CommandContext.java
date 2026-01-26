/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.system.AbstractOptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.exceptions.SenderTypeException;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CommandContext {
    @Nonnull
    private final AbstractCommand calledCommand;
    @Nonnull
    private final String inputString;
    @Nonnull
    private final CommandSender sender;
    @Nonnull
    private final Map<Argument<?, ?>, Object> argValues;
    @Nonnull
    private final Map<Argument<?, ?>, String[]> argInput;

    public CommandContext(@Nonnull AbstractCommand calledCommand, @Nonnull CommandSender sender, @Nonnull String inputString) {
        this.calledCommand = calledCommand;
        this.inputString = inputString;
        this.sender = sender;
        this.argValues = new Object2ObjectOpenHashMap();
        this.argInput = new Object2ObjectOpenHashMap();
    }

    <DataType> void appendArgumentData(@Nonnull Argument<?, DataType> argument, @Nonnull String[] data, boolean asListArgument, @Nonnull ParseResult parseResult) {
        if (data.length == 0 && argument instanceof DefaultArg) {
            DefaultArg defaultArg = (DefaultArg)argument;
            this.argValues.put(argument, defaultArg.getDefaultValue());
            return;
        }
        int numParameters = argument.getArgumentType().getNumberOfParameters();
        if (asListArgument && data.length % numParameters != 0 || !asListArgument && data.length != numParameters) {
            parseResult.fail(Message.translation("server.commands.parsing.error.wrongNumberOfParametersForArgument").param("argument", argument.getName()).param("expected", argument.getArgumentType().getNumberOfParameters()).param("actual", data.length).param("input", String.join((CharSequence)" ", data)));
            return;
        }
        DataType convertedValue = argument.getArgumentType().parse(data, parseResult);
        if (parseResult.failed()) {
            return;
        }
        argument.validate(convertedValue, parseResult);
        if (parseResult.failed()) {
            return;
        }
        this.argValues.put(argument, convertedValue);
        this.argInput.put(argument, data);
    }

    public <DataType> DataType get(@Nonnull Argument<?, DataType> argument) {
        if (!this.argValues.containsKey(argument) && argument instanceof AbstractOptionalArg.DefaultValueArgument) {
            AbstractOptionalArg.DefaultValueArgument defaultValueArgument = (AbstractOptionalArg.DefaultValueArgument)((Object)argument);
            Object defaultValue = defaultValueArgument.getDefaultValue();
            this.argValues.put(argument, defaultValue);
            return defaultValue;
        }
        return (DataType)this.argValues.get(argument);
    }

    public String[] getInput(@Nonnull Argument<?, ?> argument) {
        return this.argInput.get(argument);
    }

    public boolean provided(@Nonnull Argument<?, ?> argument) {
        return this.argValues.containsKey(argument);
    }

    @Nonnull
    public String getInputString() {
        return this.inputString;
    }

    public void sendMessage(@Nonnull Message message) {
        this.sender.sendMessage(message);
    }

    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    @Nonnull
    public <T extends CommandSender> T senderAs(@Nonnull Class<T> senderType) {
        try {
            return (T)((CommandSender)senderType.cast(this.sender));
        }
        catch (ClassCastException e) {
            throw new SenderTypeException(senderType);
        }
    }

    @Nullable
    public Ref<EntityStore> senderAsPlayerRef() {
        return this.senderAs(Player.class).getReference();
    }

    @Nonnull
    public CommandSender sender() {
        return this.sender;
    }

    @Nonnull
    public AbstractCommand getCalledCommand() {
        return this.calledCommand;
    }
}

