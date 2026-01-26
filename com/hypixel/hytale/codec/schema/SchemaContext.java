/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema;

import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.NamedSchema;
import com.hypixel.hytale.codec.schema.SchemaConvertable;
import com.hypixel.hytale.codec.schema.config.NullSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class SchemaContext {
    @Nonnull
    private final Map<String, Schema> definitions = new Object2ObjectLinkedOpenHashMap<String, Schema>();
    @Nonnull
    private final Map<String, Schema> otherDefinitions = new Object2ObjectLinkedOpenHashMap<String, Schema>();
    @Nonnull
    private final Map<Object, String> nameMap = new Object2ObjectOpenHashMap<Object, String>();
    @Nonnull
    private final Object2IntMap<String> nameCollisionCount = new Object2IntOpenHashMap<String>();
    @Nonnull
    private final Map<SchemaConvertable<?>, String> fileReferences = new Object2ObjectOpenHashMap();

    public void addFileReference(@Nonnull String fileName, @Nonnull SchemaConvertable<?> codec) {
        this.fileReferences.put(codec, fileName + "#");
    }

    @Nullable
    public Schema getFileReference(@Nonnull SchemaConvertable<?> codec) {
        String file = this.fileReferences.get(codec);
        if (file != null) {
            return Schema.ref(file);
        }
        return null;
    }

    @Nonnull
    public Schema refDefinition(@Nonnull SchemaConvertable<?> codec) {
        return this.refDefinition(codec, null);
    }

    @Nonnull
    public <T> Schema refDefinition(@Nonnull SchemaConvertable<T> convertable, @Nullable T def) {
        Schema ref = this.getFileReference(convertable);
        if (ref != null) {
            return ref;
        }
        if (convertable instanceof BuilderCodec) {
            BuilderCodec builderCodec = (BuilderCodec)convertable;
            String name = this.resolveName(builderCodec);
            if (!this.definitions.containsKey(name)) {
                this.definitions.put(name, NullSchema.INSTANCE);
                this.definitions.put(name, convertable.toSchema(this));
            }
            Schema c = Schema.ref("common.json#/definitions/" + name);
            if (def != null) {
                c.setDefaultRaw((BsonDocument)builderCodec.encode((Object)def, (ExtraInfo)EmptyExtraInfo.EMPTY));
            }
            return c;
        }
        if (convertable instanceof NamedSchema) {
            NamedSchema namedSchema = (NamedSchema)((Object)convertable);
            String name = this.resolveName(namedSchema);
            if (!this.otherDefinitions.containsKey(name)) {
                this.otherDefinitions.put(name, NullSchema.INSTANCE);
                this.otherDefinitions.put(name, convertable.toSchema(this));
            }
            return Schema.ref("other.json#/definitions/" + name);
        }
        return convertable.toSchema(this, def);
    }

    @Nullable
    public Schema getRawDefinition(@Nonnull BuilderCodec<?> codec) {
        String name = this.resolveName(codec);
        return this.definitions.get(name);
    }

    @Nullable
    public Schema getRawDefinition(@Nonnull NamedSchema namedSchema) {
        return this.otherDefinitions.get(this.resolveName(namedSchema));
    }

    @Nonnull
    public Map<String, Schema> getDefinitions() {
        return this.definitions;
    }

    @Nonnull
    public Map<String, Schema> getOtherDefinitions() {
        return this.otherDefinitions;
    }

    private String resolveName(@Nonnull NamedSchema namedSchema) {
        return this.nameMap.computeIfAbsent(namedSchema, key -> {
            String n = ((NamedSchema)key).getSchemaName();
            int count = this.nameCollisionCount.getInt(n);
            this.nameCollisionCount.put(n, count + 1);
            if (count > 0) {
                return n + "@" + count;
            }
            return n;
        });
    }

    @Nonnull
    private String resolveName(@Nonnull BuilderCodec<?> codec) {
        return this.nameMap.computeIfAbsent(codec.getInnerClass(), key -> {
            String n = ((Class)key).getSimpleName();
            int count = this.nameCollisionCount.getInt(n);
            this.nameCollisionCount.put(n, count + 1);
            if (count > 0) {
                return n + "@" + count;
            }
            return n;
        });
    }

    static {
        Schema.init();
    }
}

