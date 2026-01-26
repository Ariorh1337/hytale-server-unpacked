/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ProcessedArgumentType<InputType, OutputType>
extends ArgumentType<OutputType> {
    @Nonnull
    private final ArgumentType<InputType> inputTypeArgumentType;

    public ProcessedArgumentType(String name, Message argumentUsage, @Nonnull ArgumentType<InputType> inputTypeArgumentType, String ... examples) {
        super(name, argumentUsage, inputTypeArgumentType.numberOfParameters, examples);
        this.inputTypeArgumentType = inputTypeArgumentType;
    }

    @Nonnull
    public ArgumentType<InputType> getInputTypeArgumentType() {
        return this.inputTypeArgumentType;
    }

    @Override
    public boolean isListArgument() {
        return this.getInputTypeArgumentType().isListArgument();
    }

    @Override
    @Nullable
    public OutputType parse(@Nonnull String[] input, @Nonnull ParseResult parseResult) {
        InputType parsedData = this.inputTypeArgumentType.parse(input, parseResult);
        if (parseResult.failed()) {
            return null;
        }
        OutputType outputType = this.processInput(parsedData);
        if (parseResult.failed()) {
            return null;
        }
        return outputType;
    }

    public abstract OutputType processInput(InputType var1);
}

