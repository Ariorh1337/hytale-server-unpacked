/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AtMostOneBooleanValidator
extends Validator {
    @Nonnull
    private final String[] attributes;

    private AtMostOneBooleanValidator(@Nonnull String[] attributes) {
        Objects.requireNonNull(attributes);
        this.attributes = attributes;
    }

    public static boolean test(@Nonnull boolean[] values) {
        int count = 0;
        for (boolean value : values) {
            if (!value) continue;
            ++count;
        }
        return count <= 1;
    }

    @Nonnull
    public static String errorMessage(String[] attributes) {
        return "At most one of " + String.join((CharSequence)" ", attributes) + " can be true";
    }

    @Nonnull
    public String errorMessage() {
        return AtMostOneBooleanValidator.errorMessage(this.attributes);
    }

    @Nonnull
    public static AtMostOneBooleanValidator withAttributes(String attribute1, String attribute2) {
        return new AtMostOneBooleanValidator(new String[]{attribute1, attribute2});
    }

    @Nonnull
    public static AtMostOneBooleanValidator withAttributes(@Nonnull String[] attributes) {
        return new AtMostOneBooleanValidator(attributes);
    }
}

