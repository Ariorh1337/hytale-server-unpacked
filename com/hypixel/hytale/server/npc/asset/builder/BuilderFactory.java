/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.codec.schema.NamedSchema;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.SchemaConvertable;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderComponent;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderFactory<T>
implements SchemaConvertable<Void>,
NamedSchema {
    public static final String DEFAULT_TYPE = "Type";
    public static final String COMPONENT_TYPE = "Component";
    private final String typeTag;
    private final Supplier<Builder<T>> defaultBuilder;
    private final Class<T> category;
    private final Map<String, Supplier<Builder<T>>> buildersSuppliers = new HashMap<String, Supplier<Builder<T>>>();

    public BuilderFactory(Class<T> category, String typeTag) {
        this(category, typeTag, null);
    }

    public BuilderFactory(Class<T> category, String typeTag, Supplier<Builder<T>> defaultBuilder) {
        this.category = category;
        this.typeTag = typeTag;
        this.defaultBuilder = defaultBuilder;
        this.add(COMPONENT_TYPE, () -> new BuilderComponent(category));
    }

    @Nonnull
    public BuilderFactory<T> add(String name, Supplier<Builder<T>> builder) {
        if (this.buildersSuppliers.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Builder with name %s already exists", name));
        }
        if (this.typeTag.isEmpty()) {
            throw new IllegalArgumentException("Can't add named builder to array builder factory");
        }
        this.buildersSuppliers.put(name, builder);
        return this;
    }

    public Class<T> getCategory() {
        return this.category;
    }

    public Builder<T> createBuilder(@Nonnull JsonElement config) {
        if (!config.isJsonObject()) {
            if (this.defaultBuilder == null) {
                throw new IllegalArgumentException(String.format("Array builder must have default builder defined: %s", config));
            }
            return this.defaultBuilder.get();
        }
        return this.createBuilder(config.getAsJsonObject(), this.typeTag);
    }

    public String getKeyName(@Nonnull JsonElement config) {
        if (!config.isJsonObject()) {
            return "-";
        }
        JsonElement element = config.getAsJsonObject().get(this.typeTag);
        return element != null ? element.getAsString() : "???";
    }

    @Nonnull
    public Builder<T> createBuilder(String name) {
        if (!this.buildersSuppliers.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Builder %s does not exist", name));
        }
        Builder<T> builder = this.buildersSuppliers.get(name).get();
        if (builder.category() != this.getCategory()) {
            throw new IllegalArgumentException(String.format("Builder %s has category %s which does not match %s", name, builder.category().getName(), this.getCategory().getName()));
        }
        builder.setTypeName(name);
        return builder;
    }

    @Nullable
    public Builder<T> tryCreateDefaultBuilder() {
        return this.defaultBuilder != null ? this.defaultBuilder.get() : null;
    }

    @Nonnull
    public List<String> getBuilderNames() {
        return new ObjectArrayList<String>(this.buildersSuppliers.keySet());
    }

    private Builder<T> createBuilder(@Nonnull JsonObject config, @Nonnull String tag) {
        if (config == null) {
            throw new IllegalArgumentException("JSON config cannot be null when creating builder");
        }
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("Tag cannot be null or empty when creating builder with content %s", config));
        }
        JsonElement element = config.get(tag);
        if (element == null && this.defaultBuilder != null) {
            return this.defaultBuilder.get();
        }
        if (element == null) {
            throw new IllegalArgumentException(String.format("Builder tag of type %s must be supplied if no default is defined in %s", tag, config));
        }
        return this.createBuilder(element.getAsString());
    }

    @Override
    @Nonnull
    public String getSchemaName() {
        return "NPCType:" + this.getCategory().getSimpleName();
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        return this.toSchema(context, false);
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context, boolean isRoot) {
        int index = 0;
        Schema[] schemas = new Schema[this.getBuilderNames().size()];
        ObjectSchema check = new ObjectSchema();
        check.setRequired(this.typeTag);
        StringSchema keys = new StringSchema();
        keys.setEnum((String[])this.getBuilderNames().toArray(String[]::new));
        check.setProperties(Map.of(this.typeTag, keys));
        Schema root = new Schema();
        if (this.defaultBuilder != null || !this.getBuilderNames().isEmpty()) {
            root.setIf(check);
            root.setThen(Schema.anyOf(schemas));
        } else {
            root.setAnyOf(schemas);
        }
        for (String builderName : this.getBuilderNames()) {
            Builder<T> builder = this.createBuilder(builderName);
            Schema schemaRef = context.refDefinition(builder);
            ObjectSchema schema = (ObjectSchema)context.getRawDefinition(builder);
            LinkedHashMap<String, Schema> newProps = new LinkedHashMap<String, Schema>();
            Schema type = StringSchema.constant(builderName);
            if (builder instanceof BuilderBase) {
                type.setDescription(((BuilderBase)builder).getLongDescription());
            }
            newProps.put(this.typeTag, type);
            if (isRoot) {
                newProps.put("TestType", new StringSchema());
                newProps.put("FailReason", new StringSchema());
                newProps.put("Parameters", BuilderParameters.toSchema(context));
            }
            newProps.putAll(schema.getProperties());
            schema.setProperties(newProps);
            Schema cond = new Schema();
            ObjectSchema checkType = new ObjectSchema();
            checkType.setProperties(Map.of(this.typeTag, StringSchema.constant(builderName)));
            checkType.setRequired(this.typeTag);
            cond.setIf(checkType);
            cond.setThen(schemaRef);
            cond.setElse(false);
            schemas[index++] = cond;
        }
        if (this.defaultBuilder != null) {
            Builder<T> builder = this.defaultBuilder.get();
            Schema schemaRef = context.refDefinition(builder);
            root.setElse(schemaRef);
        } else {
            root.setElse(false);
        }
        root.setHytaleSchemaTypeField(new Schema.SchemaTypeField(this.typeTag, null, (String[])this.getBuilderNames().toArray(String[]::new)));
        root.setTitle(this.getCategory().getSimpleName());
        return root;
    }
}

