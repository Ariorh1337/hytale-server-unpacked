/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.common.util.StringUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnumArgumentType<E extends Enum<E>>
extends SingleArgumentType<E> {
    @Nonnull
    private final Class<? extends E> enumClass;
    @Nonnull
    private final E[] enumConstants;
    @Nonnull
    private final List<String> enumNames;

    public EnumArgumentType(@Nonnull String name, @Nonnull Class<E> enumClass) {
        super(name, EnumArgumentType.getArgumentUsageString((Enum[])((Enum[])enumClass.getEnumConstants())), EnumArgumentType.getExamples((Enum[])((Enum[])enumClass.getEnumConstants())));
        this.enumClass = enumClass;
        this.enumConstants = (Enum[])enumClass.getEnumConstants();
        this.enumNames = new ObjectArrayList<String>();
        for (E enumConstant : this.enumConstants) {
            this.enumNames.add(((Enum)enumConstant).name());
        }
    }

    @Override
    @Nullable
    public E parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
        String inputLowerCase = input.toLowerCase();
        for (E enumConstant : this.enumConstants) {
            if (!((Enum)enumConstant).name().toLowerCase().equals(inputLowerCase)) continue;
            return enumConstant;
        }
        parseResult.fail(Message.empty().insert(Message.translation("server.commands.errors.noSuchEnum").param("type", this.enumClass.getSimpleName()).param("name", input)).insert(Message.raw(" ")).insert(Message.translation("server.general.failed.didYouMean").param("choices", StringUtil.sortByFuzzyDistance(inputLowerCase, this.enumNames, CommandUtil.RECOMMEND_COUNT).toString())));
        return null;
    }

    @Nonnull
    public static <X extends Enum<X>> String getArgumentUsageString(@Nonnull X[] enumConstants) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < enumConstants.length; ++i) {
            stringBuilder.append(((Enum)enumConstants[i]).name().toLowerCase());
            if (i == enumConstants.length - 1) continue;
            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    @Nonnull
    public static <X extends Enum<X>> String[] getExamples(@Nonnull X[] enumConstants) {
        String[] examples = new String[enumConstants.length];
        for (int i = 0; i < enumConstants.length; ++i) {
            examples[i] = ((Enum)enumConstants[i]).name().toLowerCase();
        }
        return examples;
    }
}

