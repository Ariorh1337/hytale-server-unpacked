/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ListArgumentType<DataType>
extends ArgumentType<List<DataType>> {
    @Nonnull
    private final ArgumentType<DataType> argumentType;

    public ListArgumentType(@Nonnull ArgumentType<DataType> argumentType) {
        super(Message.translation("server.commands.parsing.argtype.list.name").insert(" (").insert(argumentType.getName()).insert(")"), Message.translation("server.commands.parsing.argtype.list.usage"), argumentType.getNumberOfParameters(), argumentType.getExamples());
        this.argumentType = argumentType;
    }

    @Override
    public boolean isListArgument() {
        return true;
    }

    @Override
    @Nullable
    public List<DataType> parse(@Nonnull String[] input, @Nonnull ParseResult parseResult) {
        ObjectArrayList returnList = new ObjectArrayList();
        for (int i = 0; i < input.length; i += this.argumentType.getNumberOfParameters()) {
            returnList.add(this.argumentType.parse(Arrays.copyOfRange(input, i, i + this.argumentType.getNumberOfParameters()), parseResult));
            if (!parseResult.failed()) continue;
            return null;
        }
        return returnList;
    }
}

