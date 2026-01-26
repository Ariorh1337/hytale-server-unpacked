/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ObjectSchema
extends Schema {
    public static final BuilderCodec<ObjectSchema> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ObjectSchema.class, ObjectSchema::new, Schema.BASE_CODEC).addField(new KeyedCodec("properties", new MapCodec(Schema.CODEC, LinkedHashMap::new), false, true), (o, i) -> {
        o.properties = i;
    }, o -> o.properties)).addField(new KeyedCodec<Object>("additionalProperties", new Schema.BooleanOrSchema(), false, true), (o, i) -> {
        o.additionalProperties = i;
    }, o -> o.additionalProperties)).addField(new KeyedCodec<StringSchema>("propertyNames", StringSchema.CODEC, false, true), (o, i) -> {
        o.propertyNames = i;
    }, o -> o.propertyNames)).build();
    private Map<String, Schema> properties;
    @Nullable
    private Object additionalProperties;
    private StringSchema propertyNames;
    private Schema unevaluatedProperties;

    public Map<String, Schema> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, Schema> properties) {
        this.properties = properties;
    }

    @Nullable
    public Object getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(boolean additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setAdditionalProperties(Schema additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public StringSchema getPropertyNames() {
        return this.propertyNames;
    }

    public void setPropertyNames(StringSchema propertyNames) {
        this.propertyNames = propertyNames;
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
        ObjectSchema that = (ObjectSchema)o;
        if (this.properties != null ? !this.properties.equals(that.properties) : that.properties != null) {
            return false;
        }
        if (this.additionalProperties != null ? !this.additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null) {
            return false;
        }
        if (this.propertyNames != null ? !this.propertyNames.equals(that.propertyNames) : that.propertyNames != null) {
            return false;
        }
        return this.unevaluatedProperties != null ? this.unevaluatedProperties.equals(that.unevaluatedProperties) : that.unevaluatedProperties == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        result = 31 * result + (this.additionalProperties != null ? this.additionalProperties.hashCode() : 0);
        result = 31 * result + (this.propertyNames != null ? this.propertyNames.hashCode() : 0);
        result = 31 * result + (this.unevaluatedProperties != null ? this.unevaluatedProperties.hashCode() : 0);
        return result;
    }

    @Nonnull
    public String toString() {
        return "ObjectSchema{properties=" + String.valueOf(this.properties) + ", additionalProperties=" + String.valueOf(this.additionalProperties) + "} " + super.toString();
    }
}

