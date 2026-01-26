/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonValue;

public class NumberSchema
extends Schema {
    public static final BuilderCodec<NumberSchema> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(NumberSchema.class, NumberSchema::new, Schema.BASE_CODEC).addField(new KeyedCodec<Object>("minimum", DoubleOrSchema.INSTANCE, false, true), (o, i) -> {
        o.minimum = i;
    }, o -> o.minimum)).addField(new KeyedCodec<Object>("exclusiveMinimum", DoubleOrSchema.INSTANCE, false, true), (o, i) -> {
        o.exclusiveMinimum = i;
    }, o -> o.exclusiveMinimum)).addField(new KeyedCodec<Object>("maximum", DoubleOrSchema.INSTANCE, false, true), (o, i) -> {
        o.maximum = i;
    }, o -> o.maximum)).addField(new KeyedCodec<Object>("exclusiveMaximum", DoubleOrSchema.INSTANCE, false, true), (o, i) -> {
        o.exclusiveMaximum = i;
    }, o -> o.exclusiveMaximum)).addField(new KeyedCodec<double[]>("enum", Codec.DOUBLE_ARRAY, false, true), (o, i) -> {
        o.enum_ = i;
    }, o -> o.enum_)).addField(new KeyedCodec<Double>("const", Codec.DOUBLE, false, true), (o, i) -> {
        o.const_ = i;
    }, o -> o.const_)).addField(new KeyedCodec<Double>("default", Codec.DOUBLE, false, true), (o, i) -> {
        o.default_ = i;
    }, o -> o.default_)).build();
    private Object minimum;
    private Object exclusiveMinimum;
    private Object maximum;
    private Object exclusiveMaximum;
    private double[] enum_;
    private Double const_;
    private Double default_;

    @Nullable
    public Object getMinimum() {
        return this.minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    @Nullable
    public Object getExclusiveMinimum() {
        return this.exclusiveMinimum;
    }

    public void setExclusiveMinimum(double exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    @Nullable
    public Object getMaximum() {
        return this.maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    @Nullable
    public Object getExclusiveMaximum() {
        return this.exclusiveMaximum;
    }

    public void setExclusiveMaximum(double exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public void setMinimum(Schema minimum) {
        this.minimum = minimum;
    }

    public void setExclusiveMinimum(Schema exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public void setMaximum(Schema maximum) {
        this.maximum = maximum;
    }

    public void setExclusiveMaximum(Schema exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public double[] getEnum() {
        return this.enum_;
    }

    public void setEnum(double[] enum_) {
        this.enum_ = enum_;
    }

    @Nullable
    public Double getConst() {
        return this.const_;
    }

    public void setConst(Double const_) {
        this.const_ = const_;
    }

    public Double getDefault() {
        return this.default_;
    }

    public void setDefault(Double default_) {
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
        NumberSchema that = (NumberSchema)o;
        if (this.minimum != null ? !this.minimum.equals(that.minimum) : that.minimum != null) {
            return false;
        }
        if (this.exclusiveMinimum != null ? !this.exclusiveMinimum.equals(that.exclusiveMinimum) : that.exclusiveMinimum != null) {
            return false;
        }
        if (this.maximum != null ? !this.maximum.equals(that.maximum) : that.maximum != null) {
            return false;
        }
        if (this.exclusiveMaximum != null ? !this.exclusiveMaximum.equals(that.exclusiveMaximum) : that.exclusiveMaximum != null) {
            return false;
        }
        if (!Arrays.equals(this.enum_, that.enum_)) {
            return false;
        }
        if (this.const_ != null ? !this.const_.equals(that.const_) : that.const_ != null) {
            return false;
        }
        return this.default_ != null ? this.default_.equals(that.default_) : that.default_ == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.minimum != null ? this.minimum.hashCode() : 0);
        result = 31 * result + (this.exclusiveMinimum != null ? this.exclusiveMinimum.hashCode() : 0);
        result = 31 * result + (this.maximum != null ? this.maximum.hashCode() : 0);
        result = 31 * result + (this.exclusiveMaximum != null ? this.exclusiveMaximum.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.enum_);
        result = 31 * result + (this.const_ != null ? this.const_.hashCode() : 0);
        result = 31 * result + (this.default_ != null ? this.default_.hashCode() : 0);
        return result;
    }

    @Nonnull
    public static Schema constant(double c) {
        NumberSchema s = new NumberSchema();
        s.setConst(c);
        return s;
    }

    @Deprecated
    private static class DoubleOrSchema
    implements Codec<Object> {
        private static final DoubleOrSchema INSTANCE = new DoubleOrSchema();

        private DoubleOrSchema() {
        }

        @Override
        public Object decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
            if (bsonValue.isNumber()) {
                return Codec.DOUBLE.decode(bsonValue, extraInfo);
            }
            return Schema.CODEC.decode(bsonValue, extraInfo);
        }

        @Override
        public BsonValue encode(Object o, ExtraInfo extraInfo) {
            if (o instanceof Double) {
                return Codec.DOUBLE.encode((Double)o, extraInfo);
            }
            return Schema.CODEC.encode((Schema)o, extraInfo);
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            return Schema.anyOf(new NumberSchema(), Schema.CODEC.toSchema(context));
        }
    }
}

