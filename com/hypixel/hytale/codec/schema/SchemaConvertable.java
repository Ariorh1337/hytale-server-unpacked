/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema;

import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SchemaConvertable<T> {
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext var1);

    @Nonnull
    default public Schema toSchema(@Nonnull SchemaContext context, @Nullable T def) {
        return this.toSchema(context);
    }
}

