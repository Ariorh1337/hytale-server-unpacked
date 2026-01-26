/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonValue;

public class ArraySchema
extends Schema {
    public static final BuilderCodec<ArraySchema> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ArraySchema.class, ArraySchema::new, Schema.BASE_CODEC).addField(new KeyedCodec<Object>("items", new ItemOrItems(), false, true), (o, i) -> {
        o.items = i;
    }, o -> o.items)).addField(new KeyedCodec<Integer>("minItems", Codec.INTEGER, false, true), (o, i) -> {
        o.minItems = i;
    }, o -> o.minItems)).addField(new KeyedCodec<Integer>("maxItems", Codec.INTEGER, false, true), (o, i) -> {
        o.maxItems = i;
    }, o -> o.maxItems)).addField(new KeyedCodec<Boolean>("uniqueItems", Codec.BOOLEAN, false, true), (o, i) -> {
        o.uniqueItems = i;
    }, o -> o.uniqueItems)).build();
    private Object items;
    private Integer minItems;
    private Integer maxItems;
    private Boolean uniqueItems;

    public ArraySchema() {
    }

    public ArraySchema(Schema item) {
        this.setItem(item);
    }

    @Nullable
    public Object getItems() {
        return this.items;
    }

    public void setItem(Schema items) {
        this.items = items;
    }

    public void setItems(Schema ... items) {
        this.items = items;
    }

    @Nullable
    public Integer getMinItems() {
        return this.minItems;
    }

    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    @Nullable
    public Integer getMaxItems() {
        return this.maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public boolean getUniqueItems() {
        return this.uniqueItems;
    }

    public void setUniqueItems(boolean uniqueItems) {
        this.uniqueItems = uniqueItems;
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
        ArraySchema that = (ArraySchema)o;
        if (this.items != null ? !this.items.equals(that.items) : that.items != null) {
            return false;
        }
        if (this.minItems != null ? !this.minItems.equals(that.minItems) : that.minItems != null) {
            return false;
        }
        if (this.maxItems != null ? !this.maxItems.equals(that.maxItems) : that.maxItems != null) {
            return false;
        }
        return this.uniqueItems != null ? this.uniqueItems.equals(that.uniqueItems) : that.uniqueItems == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.items != null ? this.items.hashCode() : 0);
        result = 31 * result + (this.minItems != null ? this.minItems.hashCode() : 0);
        result = 31 * result + (this.maxItems != null ? this.maxItems.hashCode() : 0);
        result = 31 * result + (this.uniqueItems != null ? this.uniqueItems.hashCode() : 0);
        return result;
    }

    @Deprecated
    private static class ItemOrItems
    implements Codec<Object> {
        @Nonnull
        private ArrayCodec<Schema> array = new ArrayCodec(Schema.CODEC, Schema[]::new);

        private ItemOrItems() {
        }

        @Override
        public Object decode(@Nonnull BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
            if (bsonValue.isArray()) {
                return this.array.decode(bsonValue, extraInfo);
            }
            return Schema.CODEC.decode(bsonValue, extraInfo);
        }

        @Override
        public BsonValue encode(Object o, ExtraInfo extraInfo) {
            if (o instanceof Schema[]) {
                return this.array.encode((T[])((Schema[])o), extraInfo);
            }
            return Schema.CODEC.encode((Schema)o, extraInfo);
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            return Schema.anyOf(Schema.CODEC.toSchema(context), this.array.toSchema(context));
        }
    }
}

