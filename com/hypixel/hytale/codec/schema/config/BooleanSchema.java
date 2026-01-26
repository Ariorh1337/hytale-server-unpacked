/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.config.Schema;
import javax.annotation.Nullable;

public class BooleanSchema
extends Schema {
    public static final BuilderCodec<BooleanSchema> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BooleanSchema.class, BooleanSchema::new, Schema.BASE_CODEC).addField(new KeyedCodec<Boolean>("default", Codec.BOOLEAN, false, true), (o, i) -> {
        o.default_ = i;
    }, o -> o.default_)).build();
    private Boolean default_;

    public Boolean getDefault() {
        return this.default_;
    }

    public void setDefault(Boolean default_) {
        this.default_ = default_;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BooleanSchema that = (BooleanSchema)o;
        return this.default_ != null ? this.default_.equals(that.default_) : that.default_ == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.default_ != null ? this.default_.hashCode() : 0);
        return result;
    }
}

