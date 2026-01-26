/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.StringArrayValidator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StringArrayNotEmptyValidator
extends StringArrayValidator {
    private static final StringArrayNotEmptyValidator INSTANCE = new StringArrayNotEmptyValidator();

    private StringArrayNotEmptyValidator() {
    }

    @Override
    public boolean test(@Nullable String[] list) {
        if (list == null || list.length == 0) {
            return false;
        }
        for (String s : list) {
            if (s != null && !s.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nonnull
    public String errorMessage(@Nullable String name, String[] list) {
        name = name == null ? "StringList" : "'" + (String)name + "'";
        return (String)name + " must not be empty or contain empty strings";
    }

    @Override
    @Nonnull
    public String errorMessage(String[] list) {
        return this.errorMessage(null, list);
    }

    public static StringArrayNotEmptyValidator get() {
        return INSTANCE;
    }
}

