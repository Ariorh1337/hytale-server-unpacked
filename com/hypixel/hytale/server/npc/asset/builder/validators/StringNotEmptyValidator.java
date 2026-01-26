/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StringNotEmptyValidator
extends StringValidator {
    private static final StringNotEmptyValidator INSTANCE = new StringNotEmptyValidator();

    private StringNotEmptyValidator() {
    }

    @Override
    public boolean test(@Nullable String value) {
        return value != null && !value.isEmpty();
    }

    @Override
    @Nonnull
    public String errorMessage(String value) {
        return this.errorMessage0(value, "Value");
    }

    @Override
    @Nonnull
    public String errorMessage(String value, String name) {
        return this.errorMessage0(value, "\"" + name + "\"");
    }

    @Nonnull
    private String errorMessage0(String value, String name) {
        return name + " must not be an empty string";
    }

    public static StringNotEmptyValidator get() {
        return INSTANCE;
    }
}

