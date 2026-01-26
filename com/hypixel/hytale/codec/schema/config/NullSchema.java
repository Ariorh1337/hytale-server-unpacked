/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.config;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.config.Schema;

public class NullSchema
extends Schema {
    public static final BuilderCodec<NullSchema> CODEC = BuilderCodec.builder(NullSchema.class, NullSchema::new, Schema.BASE_CODEC).build();
    public static final NullSchema INSTANCE = new NullSchema();
}

