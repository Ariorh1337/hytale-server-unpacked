/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.validation;

import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;

@Deprecated(forRemoval=true)
public interface LegacyValidator<T>
extends Validator<T> {
    @Override
    public void accept(T var1, ValidationResults var2);

    @Override
    default public void updateSchema(SchemaContext context, Schema target) {
        System.err.println("updateSchema: " + this.getClass().getSimpleName());
    }
}

