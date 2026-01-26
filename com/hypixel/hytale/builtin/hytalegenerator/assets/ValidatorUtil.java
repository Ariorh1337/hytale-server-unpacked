/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets;

import com.hypixel.hytale.codec.validation.LegacyValidator;
import com.hypixel.hytale.codec.validation.ValidationResults;
import javax.annotation.Nonnull;

public class ValidatorUtil {
    @Nonnull
    public static <T> LegacyValidator<String> validEnumValue(final @Nonnull T[] values) {
        return new LegacyValidator<String>(){

            @Override
            public void accept(String providedValue, @Nonnull ValidationResults results) {
                for (Object value : values) {
                    if (!value.toString().equals(providedValue)) continue;
                    return;
                }
                results.fail("String not a valid enum value: " + providedValue);
            }
        };
    }
}

