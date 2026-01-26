/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.metadata;

import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.metadata.Metadata;
import javax.annotation.Nonnull;

public class HytaleType
implements Metadata {
    private final String type;

    public HytaleType(String type) {
        this.type = type;
    }

    @Override
    public void modify(@Nonnull Schema schema) {
        schema.getHytale().setType(this.type);
    }
}

