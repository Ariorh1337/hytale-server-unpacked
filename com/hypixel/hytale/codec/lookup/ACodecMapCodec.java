/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.lookup;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.InheritCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.codec.validation.ValidatableCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public abstract class ACodecMapCodec<K, T, C extends Codec<? extends T>>
implements Codec<T>,
ValidatableCodec<T>,
InheritCodec<T> {
    protected final String key;
    protected final Codec<K> keyCodec;
    protected final Map<K, C> idToCodec = new ConcurrentHashMap<K, C>();
    protected final Map<Class<? extends T>, K> classToId = new ConcurrentHashMap<Class<? extends T>, K>();
    protected final Map<K, Class<? extends T>> idToClass = new ConcurrentHashMap<K, Class<? extends T>>();
    @Nonnull
    protected AtomicReference<CodecPriority<C>[]> codecs = new AtomicReference<CodecPriority[]>(new CodecPriority[0]);
    protected final boolean allowDefault;
    protected final boolean encodeDefaultKey;

    public ACodecMapCodec(Codec<K> keyCodec) {
        this(keyCodec, false);
    }

    public ACodecMapCodec(Codec<K> keyCodec, boolean allowDefault) {
        this("Id", keyCodec, allowDefault);
    }

    public ACodecMapCodec(String id, Codec<K> keyCodec) {
        this(id, keyCodec, false);
    }

    public ACodecMapCodec(String key, Codec<K> keyCodec, boolean allowDefault) {
        this(key, keyCodec, allowDefault, true);
    }

    public ACodecMapCodec(String key, Codec<K> keyCodec, boolean allowDefault, boolean encodeDefaultKey) {
        this.key = key;
        this.allowDefault = allowDefault;
        this.encodeDefaultKey = encodeDefaultKey;
        this.keyCodec = keyCodec;
    }

    @Nonnull
    public ACodecMapCodec<K, T, C> register(K id, Class<? extends T> aClass, C codec) {
        this.register(Priority.NORMAL, id, aClass, codec);
        return this;
    }

    public ACodecMapCodec<K, T, C> register(@Nonnull Priority priority, K id, Class<? extends T> aClass, C codec) {
        CodecPriority[] newCodecs;
        CodecPriority<C>[] current;
        this.idToCodec.put(id, codec);
        this.classToId.put(aClass, (Class<? extends T>)id);
        this.idToClass.put(id, aClass);
        if (codec instanceof ValidatableCodec) {
            ((ValidatableCodec)codec).validateDefaults(new ExtraInfo(), new HashSet());
        }
        if (!this.allowDefault && !priority.equals(Priority.NORMAL)) {
            throw new IllegalStateException("Defaults disallowed but non-normal priority provided");
        }
        if (!this.allowDefault) {
            return this;
        }
        CodecPriority<C> codecPriority = new CodecPriority<C>(codec, priority);
        do {
            int index;
            int insertionPoint = (index = Arrays.binarySearch(current = this.codecs.get(), codecPriority, Comparator.comparingInt(a -> a.priority().getLevel()))) >= 0 ? index + 1 : -(index + 1);
            newCodecs = new CodecPriority[current.length + 1];
            System.arraycopy(current, 0, newCodecs, 0, insertionPoint);
            newCodecs[insertionPoint] = codecPriority;
            System.arraycopy(current, insertionPoint, newCodecs, insertionPoint + 1, current.length - insertionPoint);
        } while (!this.codecs.compareAndSet(current, newCodecs));
        return this;
    }

    public void remove(Class<? extends T> aClass) {
        CodecPriority[] newCodecs;
        CodecPriority<C>[] current;
        K id = this.classToId.remove(aClass);
        Codec codec = (Codec)this.idToCodec.remove(id);
        this.idToClass.remove(id);
        if (!this.allowDefault) {
            return;
        }
        do {
            current = this.codecs.get();
            int index = -1;
            for (int i = 0; i < current.length; ++i) {
                CodecPriority<C> c = current[i];
                if (c.codec() != codec) continue;
                index = i;
                break;
            }
            if (index == -1) {
                return;
            }
            newCodecs = new CodecPriority[current.length - 1];
            System.arraycopy(current, 0, newCodecs, 0, index);
            System.arraycopy(current, index + 1, newCodecs, index, current.length - index - 1);
        } while (!this.codecs.compareAndSet(current, newCodecs));
    }

    @Nullable
    public C getDefaultCodec() {
        CodecPriority<C>[] c = this.codecs.get();
        if (c.length == 0) {
            return null;
        }
        return (C)((Codec)c[0].codec());
    }

    public C getCodecFor(K key) {
        return (C)((Codec)this.idToCodec.get(key));
    }

    public C getCodecFor(Class<? extends T> key) {
        return (C)((Codec)this.idToCodec.get(this.classToId.get(key)));
    }

    public Class<? extends T> getClassFor(K key) {
        return this.idToClass.get(key);
    }

    public K getIdFor(Class<? extends T> key) {
        return this.classToId.get(key);
    }

    public Set<K> getRegisteredIds() {
        return Collections.unmodifiableSet(this.idToCodec.keySet());
    }

    @Override
    public T decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
        Codec codec;
        BsonDocument document = bsonValue.asDocument();
        BsonValue id = document.get(this.key);
        Codec codec2 = codec = id == null ? null : (Codec)this.idToCodec.get(this.keyCodec.decode(id, extraInfo));
        if (codec == null) {
            C defaultCodec = this.getDefaultCodec();
            if (defaultCodec == null) {
                throw new UnknownIdException("No codec registered with for '" + this.key + "': " + String.valueOf(id));
            }
            return defaultCodec.decode(document, extraInfo);
        }
        return codec.decode(document, extraInfo);
    }

    @Override
    @Nullable
    public T decodeAndInherit(@Nonnull BsonDocument document, T parent, ExtraInfo extraInfo) {
        BsonValue id = document.get(this.key);
        Codec codec = (Codec)this.idToCodec.get(id == null ? null : id.asString().getValue());
        if (codec == null) {
            C defaultCodec = this.getDefaultCodec();
            if (defaultCodec == null) {
                throw new UnknownIdException("No codec registered with for '" + this.key + "': " + String.valueOf(id));
            }
            if (defaultCodec instanceof InheritCodec) {
                return ((InheritCodec)defaultCodec).decodeAndInherit(document, parent, extraInfo);
            }
            return defaultCodec.decode(document, extraInfo);
        }
        if (codec instanceof InheritCodec) {
            return ((InheritCodec)codec).decodeAndInherit(document, parent, extraInfo);
        }
        return codec.decode(document, extraInfo);
    }

    @Override
    public void decodeAndInherit(@Nonnull BsonDocument document, T t, T parent, ExtraInfo extraInfo) {
        BsonValue id = document.get(this.key);
        Codec codec = (Codec)this.idToCodec.get(id == null ? null : id.asString().getValue());
        if (codec == null) {
            C defaultCodec = this.getDefaultCodec();
            if (defaultCodec == null) {
                throw new UnknownIdException("No codec registered with for '" + this.key + "': " + String.valueOf(id));
            }
            if (defaultCodec instanceof InheritCodec) {
                ((InheritCodec)defaultCodec).decodeAndInherit(document, t, parent, extraInfo);
                return;
            }
            throw new UnsupportedOperationException();
        }
        if (codec instanceof InheritCodec) {
            ((InheritCodec)codec).decodeAndInherit(document, t, parent, extraInfo);
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public BsonValue encode(@Nonnull T t, ExtraInfo extraInfo) {
        Class<?> aClass = t.getClass();
        K id = this.classToId.get(aClass);
        C defaultCodec = this.getDefaultCodec();
        if (id == null && defaultCodec == null) {
            throw new UnknownIdException("No id registered with for '" + String.valueOf(aClass) + "': " + String.valueOf(t));
        }
        Codec<Object> codec = (Codec)this.idToCodec.get(id);
        if (codec == null) {
            if (defaultCodec == null) {
                throw new UnknownIdException("No codec registered with for '" + String.valueOf(aClass) + "': " + String.valueOf(t));
            }
            codec = defaultCodec;
        }
        BsonValue encode = codec.encode(t, extraInfo);
        if (id == null) {
            return encode;
        }
        BsonDocument document = new BsonDocument();
        if (this.encodeDefaultKey || codec != defaultCodec) {
            document.put(this.key, this.keyCodec.encode(id, extraInfo));
        }
        document.putAll(encode.asDocument());
        return document;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public T decodeJson(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.mark();
        Object id = null;
        if (RawJsonReader.seekToKey(reader, this.key)) {
            id = this.keyCodec.decodeJson(reader, extraInfo);
        }
        reader.reset();
        extraInfo.ignoreUnusedKey(this.key);
        try {
            Codec codec;
            Codec codec2 = codec = id == null ? null : (Codec)this.idToCodec.get(id);
            if (codec == null) {
                C defaultCodec = this.getDefaultCodec();
                if (defaultCodec == null) {
                    throw new UnknownIdException("No codec registered with for '" + this.key + "': " + String.valueOf(id));
                }
                Object t = defaultCodec.decodeJson(reader, extraInfo);
                return t;
            }
            Object t = codec.decodeJson(reader, extraInfo);
            return t;
        }
        finally {
            extraInfo.popIgnoredUnusedKey();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public T decodeAndInheritJson(@Nonnull RawJsonReader reader, @Nullable T parent, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.mark();
        Object id = null;
        if (RawJsonReader.seekToKey(reader, this.key)) {
            id = this.keyCodec.decodeJson(reader, extraInfo);
        } else if (parent != null) {
            id = this.getIdFor(parent.getClass());
        }
        reader.reset();
        extraInfo.ignoreUnusedKey(this.key);
        try {
            Codec codec;
            Codec codec2 = codec = id == null ? null : (Codec)this.idToCodec.get(id);
            if (codec == null) {
                C defaultCodec = this.getDefaultCodec();
                if (defaultCodec == null) {
                    throw new UnknownIdException("No codec registered with for '" + this.key + "': " + String.valueOf(id));
                }
                if (defaultCodec instanceof InheritCodec) {
                    T t = ((InheritCodec)defaultCodec).decodeAndInheritJson(reader, parent, extraInfo);
                    return t;
                }
                Object t = defaultCodec.decodeJson(reader, extraInfo);
                return t;
            }
            if (codec instanceof InheritCodec) {
                T t = ((InheritCodec)codec).decodeAndInheritJson(reader, parent, extraInfo);
                return t;
            }
            Object t = codec.decodeJson(reader, extraInfo);
            return t;
        }
        finally {
            extraInfo.popIgnoredUnusedKey();
        }
    }

    @Override
    public void decodeAndInheritJson(@Nonnull RawJsonReader reader, T t, @Nullable T parent, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.mark();
        Object id = null;
        if (RawJsonReader.seekToKey(reader, this.key)) {
            id = this.keyCodec.decodeJson(reader, extraInfo);
        } else if (parent != null) {
            id = this.getIdFor(parent.getClass());
        }
        reader.reset();
        extraInfo.ignoreUnusedKey(this.key);
        try {
            Codec codec;
            Codec codec2 = codec = id == null ? null : (Codec)this.idToCodec.get(id);
            if (codec == null) {
                C defaultCodec = this.getDefaultCodec();
                if (defaultCodec == null) {
                    throw new UnknownIdException("No codec registered with for '" + this.key + "': " + String.valueOf(id));
                }
                if (defaultCodec instanceof InheritCodec) {
                    ((InheritCodec)defaultCodec).decodeAndInheritJson(reader, t, parent, extraInfo);
                    return;
                }
                throw new UnsupportedOperationException();
            }
            if (codec instanceof InheritCodec) {
                ((InheritCodec)codec).decodeAndInheritJson(reader, t, parent, extraInfo);
                return;
            }
            throw new UnsupportedOperationException();
        }
        finally {
            extraInfo.popIgnoredUnusedKey();
        }
    }

    @Override
    public void validate(@Nonnull T t, ExtraInfo extraInfo) {
        K id = this.getIdFor(t.getClass());
        C codec = this.getCodecFor(id);
        if (this.keyCodec instanceof ValidatableCodec) {
            ((ValidatableCodec)this.keyCodec).validate(id, extraInfo);
        }
        if (codec instanceof ValidatableCodec) {
            ((ValidatableCodec)codec).validate(t, extraInfo);
        }
    }

    @Override
    public void validateDefaults(ExtraInfo extraInfo, @Nonnull Set<Codec<?>> tested) {
        if (!tested.add(this)) {
            return;
        }
        ValidatableCodec.validateDefaults(this.keyCodec, extraInfo, tested);
        for (Codec codec : this.idToCodec.values()) {
            ValidatableCodec.validateDefaults(codec, extraInfo, tested);
        }
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        Schema s;
        ObjectArrayList options = new ObjectArrayList();
        Map.Entry[] entries = (Map.Entry[])this.idToCodec.entrySet().toArray(Map.Entry[]::new);
        Arrays.sort(entries, Comparator.comparing(e -> {
            if (e.getKey() instanceof Comparable) {
                return (Comparable)e.getKey();
            }
            return e.getKey().toString();
        }));
        Object def = this.allowDefault ? this.getDefaultCodec() : null;
        String defKey = null;
        for (Map.Entry entry : entries) {
            BuilderCodec bc;
            Schema subSchema;
            Schema schema;
            Codec c = (Codec)entry.getValue();
            if (c == def) {
                defKey = entry.getKey().toString();
            }
            if ((schema = context.refDefinition(c)).getRef() != null && c instanceof BuilderCodec && (subSchema = context.getRawDefinition(bc = (BuilderCodec)c)) instanceof ObjectSchema) {
                ObjectSchema objectSchema = (ObjectSchema)subSchema;
                this.mutateChildSchema(entry.getKey().toString(), context, bc, objectSchema);
            }
            options.add(schema);
        }
        if (options.isEmpty()) {
            s = new ObjectSchema();
            ((ObjectSchema)s).setAdditionalProperties(false);
            return s;
        }
        s = Schema.anyOf((Schema[])options.toArray(Schema[]::new));
        s.getHytale().setMergesProperties(true);
        s.setTitle("Type Selector");
        s.setHytaleSchemaTypeField(new Schema.SchemaTypeField(this.key, defKey, (String[])Arrays.stream(entries).map(e -> e.getKey().toString()).toArray(String[]::new)));
        return s;
    }

    protected void mutateChildSchema(String key, @Nonnull SchemaContext context, BuilderCodec<? extends T> c, @Nonnull ObjectSchema objectSchema) {
        Object def = null;
        if (this.allowDefault) {
            def = this.getDefaultCodec();
        }
        Schema keySchema = this.keyCodec.toSchema(context);
        if (def == c) {
            keySchema.setTypes(new String[]{"null", "string"});
            Schema origKey = keySchema;
            keySchema = new Schema();
            StringSchema enum_ = new StringSchema();
            enum_.setEnum((String[])this.idToCodec.entrySet().stream().filter(v -> v.getValue() != c).map(Map.Entry::getKey).map(Object::toString).toArray(String[]::new));
            keySchema.setAllOf(origKey, Schema.not(enum_));
        } else {
            ((StringSchema)keySchema).setConst(key);
        }
        keySchema.setMarkdownDescription("This field controls the type, it must be set to the constant value \"" + key + "\" to function as this type.");
        LinkedHashMap<String, Schema> props = new LinkedHashMap<String, Schema>();
        props.put(this.key, keySchema);
        Map<String, Schema> otherProps = objectSchema.getProperties();
        otherProps.remove(this.key);
        props.putAll(otherProps);
        objectSchema.setProperties(props);
    }

    private record CodecPriority<C>(C codec, Priority priority) {
    }

    public static class UnknownIdException
    extends CodecException {
        public UnknownIdException(String message) {
            super(message);
        }
    }
}

