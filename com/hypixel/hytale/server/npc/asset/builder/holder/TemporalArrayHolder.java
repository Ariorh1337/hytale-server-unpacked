/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.holder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.TemporalArrayValidator;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TemporalArrayHolder
extends StringArrayHolder {
    protected TemporalArrayValidator validator;
    private TemporalAmount[] cachedTemporalArray;

    public static TemporalAmount[] convertStringToTemporalArray(@Nullable String[] source) {
        if (source == null) {
            return null;
        }
        int length = source.length;
        TemporalAmount[] result = new TemporalAmount[length];
        for (int i = 0; i < length; ++i) {
            String text = source[i];
            String period = !source[i].isEmpty() && source[i].charAt(0) == 'P' ? text : "P" + text;
            try {
                result[i] = Period.parse(period);
                continue;
            }
            catch (DateTimeParseException e) {
                try {
                    result[i] = Duration.parse(period);
                    continue;
                }
                catch (DateTimeParseException e2) {
                    throw new IllegalStateException(String.format("Cannot parse text %s to Duration or Period", source[i]));
                }
            }
        }
        return result;
    }

    public void readJSON(@Nonnull JsonElement requiredJsonElement, int minLength, int maxLength, TemporalArrayValidator validator, String name, @Nonnull BuilderParameters builderParameters) {
        this.readJSON(requiredJsonElement, minLength, maxLength, name, builderParameters);
        this.validator = validator;
        if (this.isStatic()) {
            String[] array = this.expression.getStringArray(null);
            this.cachedTemporalArray = TemporalArrayHolder.convertStringToTemporalArray(array);
            this.validate(this.cachedTemporalArray);
        }
    }

    @Nullable
    public TemporalAmount[] getTemporalArray(ExecutionContext executionContext) {
        TemporalAmount[] value = this.rawGetTemporalArray(executionContext);
        return value;
    }

    @Nullable
    public TemporalAmount[] rawGetTemporalArray(ExecutionContext executionContext) {
        if (this.isStatic()) {
            return this.cachedTemporalArray;
        }
        String[] array = this.expression.getStringArray(executionContext);
        TemporalAmount[] value = TemporalArrayHolder.convertStringToTemporalArray(array);
        this.validate(value);
        return value;
    }

    public void validate(@Nullable TemporalAmount[] value) {
        if (value != null) {
            this.validateLength(value.length);
        }
        if (this.validator != null && !this.validator.test(value)) {
            throw new IllegalStateException(this.validator.errorMessage(this.name, value));
        }
    }
}

