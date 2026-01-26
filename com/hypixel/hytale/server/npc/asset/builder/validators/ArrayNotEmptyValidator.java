/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectArrayHelper;
import com.hypixel.hytale.server.npc.asset.builder.validators.ArrayValidator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArrayNotEmptyValidator
extends ArrayValidator {
    private static final ArrayNotEmptyValidator INSTANCE = new ArrayNotEmptyValidator();

    private ArrayNotEmptyValidator() {
    }

    @Override
    public boolean test(@Nonnull BuilderObjectArrayHelper<?, ?> builderObjectArrayHelper) {
        return builderObjectArrayHelper.isPresent();
    }

    @Override
    @Nonnull
    public String errorMessage(String name, BuilderObjectArrayHelper<?, ?> builderObjectArrayHelper) {
        return ArrayNotEmptyValidator.errorMessage(name);
    }

    @Override
    @Nonnull
    public String errorMessage(BuilderObjectArrayHelper<?, ?> builderObjectArrayHelper) {
        return ArrayNotEmptyValidator.errorMessage(null);
    }

    @Nonnull
    public static String errorMessage(@Nullable String name) {
        name = name == null ? "Array" : "'" + (String)name + "'";
        return (String)name + " must not be empty";
    }

    public static ArrayNotEmptyValidator get() {
        return INSTANCE;
    }
}

