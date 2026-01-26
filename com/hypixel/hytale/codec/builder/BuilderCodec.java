/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.builder;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.DirectDecodeCodec;
import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.InheritCodec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.RawJsonCodec;
import com.hypixel.hytale.codec.VersionedExtraInfo;
import com.hypixel.hytale.codec.builder.BuilderField;
import com.hypixel.hytale.codec.builder.StringTreeMap;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.NullSchema;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.metadata.Metadata;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.codec.validation.ValidatableCodec;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.function.consumer.TriConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class BuilderCodec<T>
implements Codec<T>,
DirectDecodeCodec<T>,
RawJsonCodec<T>,
InheritCodec<T>,
ValidatableCodec<T> {
    public static final int UNSET_VERSION = Integer.MIN_VALUE;
    public static final int UNSET_MAX_VERSION = Integer.MAX_VALUE;
    public static final int INITIAL_VERSION = 0;
    public static final BuilderCodec<?>[] EMPTY_ARRAY = new BuilderCodec[0];
    private static final KeyedCodec<Integer> VERSION = new KeyedCodec<Integer>("Version", INTEGER);
    protected final Class<T> tClass;
    protected final Supplier<T> supplier;
    @Nullable
    protected final BuilderCodec<? super T> parentCodec;
    @Nonnull
    protected final Map<String, List<BuilderField<T, ?>>> entries;
    @Nonnull
    protected final Map<String, List<BuilderField<T, ?>>> unmodifiableEntries;
    protected final BiConsumer<T, ValidationResults> validator;
    protected final BiConsumer<T, ExtraInfo> afterDecode;
    protected final boolean hasNonNullValidator;
    protected final String documentation;
    protected final List<Metadata> metadata;
    protected final int codecVersion;
    protected final int minCodecVersion;
    protected final boolean versioned;
    protected final boolean useLegacyVersion;
    @Nonnull
    protected final StringTreeMap<KeyEntry<T>> stringTreeMap;

    protected BuilderCodec(@Nonnull BuilderBase<T, ?> builder) {
        int minCodecVersion;
        int codecVersion;
        this.tClass = builder.tClass;
        this.supplier = builder.supplier;
        this.parentCodec = builder.parentCodec;
        this.entries = Objects.requireNonNull(builder.entries, "entries parameter can't be null");
        this.unmodifiableEntries = Collections.unmodifiableMap(builder.entries);
        this.stringTreeMap = builder.stringTreeMap;
        this.validator = builder.validator;
        this.afterDecode = builder.afterDecode;
        this.documentation = builder.documentation;
        this.metadata = builder.metadata;
        boolean hasNonNullValidator = false;
        for (List<BuilderField<T, ?>> fields : this.entries.values()) {
            fields.sort(Comparator.comparingInt(BuilderField::getMinVersion));
            for (BuilderField builderField : fields) {
                hasNonNullValidator |= builderField.hasNonNullValidator();
            }
        }
        this.hasNonNullValidator = hasNonNullValidator;
        if (builder.codecVersion != Integer.MIN_VALUE) {
            codecVersion = builder.codecVersion;
        } else {
            int highestFieldVersion = Integer.MIN_VALUE;
            for (List list : this.entries.values()) {
                for (BuilderField field : list) {
                    highestFieldVersion = Math.max(highestFieldVersion, field.getHighestSupportedVersion());
                }
            }
            codecVersion = highestFieldVersion;
        }
        if (builder.minCodecVersion != Integer.MAX_VALUE) {
            minCodecVersion = builder.minCodecVersion;
        } else {
            int lowestFieldVersion = Integer.MAX_VALUE;
            for (List<BuilderField<T, ?>> fields : this.entries.values()) {
                for (BuilderField<T, ?> field : fields) {
                    int min = field.getMinVersion();
                    if (min == Integer.MIN_VALUE) continue;
                    lowestFieldVersion = Math.min(lowestFieldVersion, min);
                }
            }
            minCodecVersion = lowestFieldVersion;
        }
        if (this.parentCodec != null) {
            codecVersion = Math.max(codecVersion, this.parentCodec.codecVersion);
            minCodecVersion = Math.min(minCodecVersion, this.parentCodec.minCodecVersion);
            this.versioned = builder.versioned || this.parentCodec.versioned;
            this.useLegacyVersion = builder.useLegacyVersion || this.parentCodec.useLegacyVersion;
        } else {
            this.versioned = builder.versioned;
            this.useLegacyVersion = builder.useLegacyVersion;
        }
        this.codecVersion = codecVersion;
        this.minCodecVersion = minCodecVersion;
    }

    public Class<T> getInnerClass() {
        return this.tClass;
    }

    public Supplier<T> getSupplier() {
        return this.supplier;
    }

    public T getDefaultValue() {
        return this.getDefaultValue(ExtraInfo.THREAD_LOCAL.get());
    }

    public T getDefaultValue(ExtraInfo extraInfo) {
        T t = this.supplier.get();
        this.afterDecode(t, extraInfo);
        return t;
    }

    @Nonnull
    public Map<String, List<BuilderField<T, ?>>> getEntries() {
        return this.unmodifiableEntries;
    }

    public BiConsumer<T, ExtraInfo> getAfterDecode() {
        return this.afterDecode;
    }

    @Nullable
    public BuilderCodec<? super T> getParent() {
        return this.parentCodec;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public int getCodecVersion() {
        return this.codecVersion;
    }

    public void inherit(T t, @Nonnull T parent, @Nonnull ExtraInfo extraInfo) {
        if (this.parentCodec != null) {
            this.parentCodec.inherit(t, parent, extraInfo);
        }
        if (this.getInnerClass().isAssignableFrom(parent.getClass())) {
            for (List<BuilderField<T, ?>> entry : this.entries.values()) {
                BuilderField<T, ?> field = BuilderCodec.findField(entry, extraInfo);
                if (field == null) continue;
                field.inherit(t, parent, extraInfo);
            }
        }
    }

    public void afterDecode(T t, ExtraInfo extraInfo) {
        if (this.parentCodec != null) {
            this.parentCodec.afterDecode(t, extraInfo);
        }
        if (this.afterDecode != null) {
            this.afterDecode.accept(t, extraInfo);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void afterDecodeAndValidate(T t, @Nonnull ExtraInfo extraInfo) {
        if (this.parentCodec != null) {
            this.parentCodec.afterDecodeAndValidate(t, extraInfo);
        }
        if (this.afterDecode != null) {
            this.afterDecode.accept(t, extraInfo);
        }
        ValidationResults results = extraInfo.getValidationResults();
        if (this.hasNonNullValidator) {
            for (List<BuilderField<T, ?>> entry : this.entries.values()) {
                BuilderField<T, ?> field = BuilderCodec.findField(entry, extraInfo);
                if (field == null) continue;
                extraInfo.pushKey(field.codec.getKey());
                try {
                    field.nullValidate(t, results, extraInfo);
                }
                finally {
                    extraInfo.popKey();
                }
            }
            if (this.validator != null) {
                this.validator.accept(t, results);
            }
            results._processValidationResults();
        } else if (this.validator != null) {
            this.validator.accept(t, results);
            results._processValidationResults();
        }
    }

    @Override
    public T decode(@Nonnull BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
        if (this.supplier == null) {
            throw new CodecException("This BuilderCodec is for an abstract or direct codec. To use this codec you must specify an existing object to decode into.");
        }
        T t = this.supplier.get();
        this.decode(bsonValue.asDocument(), t, extraInfo);
        return t;
    }

    @Override
    @Nonnull
    public BsonDocument encode(T t, @Nonnull ExtraInfo extraInfo) {
        BsonDocument document = new BsonDocument();
        if (this.versioned) {
            if (this.codecVersion != 0) {
                VERSION.put(document, this.codecVersion, extraInfo);
            }
            extraInfo = new VersionedExtraInfo(this.codecVersion, extraInfo);
        }
        return this.encode0(t, document, extraInfo);
    }

    @Override
    public void decode(@Nonnull BsonValue bsonValue, T t, @Nonnull ExtraInfo extraInfo) {
        this.decode0(bsonValue.asDocument(), t, extraInfo);
        this.afterDecodeAndValidate(t, extraInfo);
    }

    protected void decode0(@Nonnull BsonDocument document, T t, ExtraInfo extraInfo) {
        if (this.versioned) {
            extraInfo = this.decodeVersion(document, extraInfo);
        }
        for (Map.Entry<String, BsonValue> entry : document.entrySet()) {
            String key = entry.getKey();
            BuilderField<T, ?> field = BuilderCodec.findEntry(this, key, extraInfo);
            if (field != null) {
                field.decode(document, t, extraInfo);
                continue;
            }
            extraInfo.addUnknownKey(key);
        }
    }

    @Nonnull
    protected BsonDocument encode0(T t, @Nonnull BsonDocument document, @Nonnull ExtraInfo extraInfo) {
        if (this.parentCodec != null) {
            this.parentCodec.encode0(t, document, extraInfo);
        }
        for (List<BuilderField<T, ?>> entry : this.entries.values()) {
            BuilderField<T, ?> field = BuilderCodec.findField(entry, extraInfo);
            if (field == null) continue;
            field.encode(document, t, extraInfo);
        }
        return document;
    }

    @Override
    public T decodeJson(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        if (this.supplier == null) {
            throw new CodecException("This BuilderCodec is for an abstract or direct codec. To use this codec you must specify an existing object to decode into.");
        }
        T t = this.supplier.get();
        this.decodeJson0(reader, t, extraInfo);
        this.afterDecodeAndValidate(t, extraInfo);
        return t;
    }

    public void decodeJson0(@Nonnull RawJsonReader reader, T t, ExtraInfo extraInfo) throws IOException {
        if (this.versioned) {
            extraInfo = this.decodeVersion(reader, extraInfo);
        }
        reader.expect('{');
        reader.consumeWhiteSpace();
        if (reader.tryConsume('}')) {
            return;
        }
        while (true) {
            this.readEntry(reader, t, extraInfo);
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect('}', ',')) {
                return;
            }
            reader.consumeWhiteSpace();
        }
    }

    protected void readEntry(@Nonnull RawJsonReader reader, T t, @Nonnull ExtraInfo extraInfo) throws IOException {
        KeyEntry<T> keyEntry;
        reader.mark();
        StringTreeMap<KeyEntry<T>> treeMapEntry = this.stringTreeMap.findEntry(reader);
        if (treeMapEntry == null || (keyEntry = treeMapEntry.getValue()) == null) {
            reader.reset();
            this.readUnknownField(reader, extraInfo);
            return;
        }
        switch (keyEntry.getType().ordinal()) {
            case 0: {
                String key = treeMapEntry.getKey();
                List<BuilderField<T, ?>> fields = keyEntry.getFields();
                this.readField(reader, t, extraInfo, key, fields);
                break;
            }
            case 1: {
                reader.unmark();
                this.skipField(reader);
                break;
            }
            case 2: {
                if (extraInfo.getKeysSize() == 0) {
                    reader.unmark();
                    this.skipField(reader);
                    break;
                }
                reader.reset();
                this.readUnknownField(reader, extraInfo);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown field entry type: " + String.valueOf((Object)keyEntry.getType()));
            }
        }
    }

    private void skipField(@Nonnull RawJsonReader reader) throws IOException {
        reader.consumeWhiteSpace();
        reader.expect(':');
        reader.consumeWhiteSpace();
        reader.skipValue();
    }

    private void readField(@Nonnull RawJsonReader reader, T t, @Nonnull ExtraInfo extraInfo, String key, @Nonnull List<BuilderField<T, ?>> fields) throws IOException {
        BuilderField<T, ?> entry = null;
        for (BuilderField<T, ?> field : fields) {
            if (!field.supportsVersion(extraInfo.getVersion())) continue;
            entry = field;
            break;
        }
        if (entry == null) {
            reader.reset();
            this.readUnknownField(reader, extraInfo);
            return;
        }
        reader.unmark();
        reader.consumeWhiteSpace();
        reader.expect(':');
        reader.consumeWhiteSpace();
        extraInfo.pushKey(key, reader);
        try {
            entry.decodeJson(reader, t, extraInfo);
        }
        catch (Exception e) {
            throw new CodecException("Failed to decode", reader, extraInfo, (Throwable)e);
        }
        finally {
            extraInfo.popKey();
        }
    }

    private void readUnknownField(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        extraInfo.readUnknownKey(reader);
        reader.consumeWhiteSpace();
        reader.expect(':');
        reader.consumeWhiteSpace();
        reader.skipValue();
    }

    public void decodeJson(@Nonnull RawJsonReader reader, T t, @Nonnull ExtraInfo extraInfo) throws IOException {
        this.decodeJson0(reader, t, extraInfo);
        this.afterDecodeAndValidate(t, extraInfo);
    }

    @Override
    public T decodeAndInherit(@Nonnull BsonDocument document, T parent, ExtraInfo extraInfo) {
        T t = this.supplier.get();
        this.decodeAndInherit(document, t, parent, extraInfo);
        return t;
    }

    @Override
    public void decodeAndInherit(@Nonnull BsonDocument document, T t, @Nullable T parent, ExtraInfo extraInfo) {
        if (this.versioned) {
            extraInfo = this.decodeVersion(document, extraInfo);
        }
        if (parent != null) {
            this.inherit(t, parent, extraInfo);
        }
        this.decodeAndInherit0(document, t, parent, extraInfo);
        this.afterDecodeAndValidate(t, extraInfo);
    }

    protected void decodeAndInherit0(@Nonnull BsonDocument document, T t, T parent, @Nonnull ExtraInfo extraInfo) {
        for (Map.Entry<String, BsonValue> entry : document.entrySet()) {
            String key = entry.getKey();
            BuilderField<T, ?> field = BuilderCodec.findEntry(this, key, extraInfo);
            if (field != null) {
                if (field.codec.getChildCodec() instanceof BuilderCodec) {
                    BuilderCodec.decodeAndInherit(field, document, t, parent, extraInfo);
                    continue;
                }
                field.decodeAndInherit(document, t, parent, extraInfo);
                continue;
            }
            extraInfo.addUnknownKey(key);
        }
    }

    private static <Type, FieldType> void decodeAndInherit(@Nonnull BuilderField<Type, FieldType> entry, @Nonnull BsonDocument document, Type t, @Nullable Type parent, @Nonnull ExtraInfo extraInfo) {
        KeyedCodec codec = entry.codec;
        String key = codec.getKey();
        BsonValue bsonValue = document.get(key);
        if (Codec.isNullBsonValue(bsonValue)) {
            if (bsonValue != null && bsonValue.isNull()) {
                entry.setValue(t, null, extraInfo);
            }
            return;
        }
        extraInfo.pushKey(key);
        try {
            BuilderCodec inheritCodec = (BuilderCodec)codec.getChildCodec();
            Object value = inheritCodec.getSupplier().get();
            Object parentValue = parent != null ? (Object)entry.getter.apply(parent, extraInfo) : null;
            inheritCodec.decodeAndInherit(bsonValue.asDocument(), value, parentValue, extraInfo);
            entry.setValue(t, value, extraInfo);
        }
        catch (Exception e) {
            throw new CodecException("Failed to decode", bsonValue, extraInfo, (Throwable)e);
        }
        finally {
            extraInfo.popKey();
        }
    }

    @Override
    public T decodeAndInheritJson(@Nonnull RawJsonReader reader, T parent, ExtraInfo extraInfo) throws IOException {
        T t = this.supplier.get();
        this.decodeAndInheritJson(reader, t, parent, extraInfo);
        return t;
    }

    @Override
    public void decodeAndInheritJson(@Nonnull RawJsonReader reader, T t, @Nullable T parent, ExtraInfo extraInfo) throws IOException {
        if (this.versioned) {
            extraInfo = this.decodeVersion(reader, extraInfo);
        }
        if (parent != null) {
            this.inherit(t, parent, extraInfo);
        }
        this.decodeAndInheritJson0(reader, t, parent, extraInfo);
        this.afterDecodeAndValidate(t, extraInfo);
    }

    public void decodeAndInheritJson0(@Nonnull RawJsonReader reader, T t, T parent, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.expect('{');
        reader.consumeWhiteSpace();
        if (reader.tryConsume('}')) {
            return;
        }
        while (true) {
            this.readAndInheritEntry(reader, t, parent, extraInfo);
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect('}', ',')) {
                return;
            }
            reader.consumeWhiteSpace();
        }
    }

    protected void readAndInheritEntry(@Nonnull RawJsonReader reader, T t, T parent, @Nonnull ExtraInfo extraInfo) throws IOException {
        KeyEntry<T> keyEntry;
        reader.mark();
        StringTreeMap<KeyEntry<T>> treeMapEntry = this.stringTreeMap.findEntry(reader);
        if (treeMapEntry == null || (keyEntry = treeMapEntry.getValue()) == null) {
            reader.reset();
            this.readUnknownField(reader, extraInfo);
            return;
        }
        switch (keyEntry.getType().ordinal()) {
            case 0: {
                String key = treeMapEntry.getKey();
                List<BuilderField<T, ?>> fields = keyEntry.getFields();
                this.readAndInheritField(reader, t, parent, extraInfo, key, fields);
                break;
            }
            case 1: {
                reader.unmark();
                this.skipField(reader);
                break;
            }
            case 2: {
                if (extraInfo.getKeysSize() == 0) {
                    reader.unmark();
                    this.skipField(reader);
                    break;
                }
                reader.reset();
                this.readUnknownField(reader, extraInfo);
            }
        }
    }

    private void readAndInheritField(@Nonnull RawJsonReader reader, T t, T parent, @Nonnull ExtraInfo extraInfo, String key, @Nonnull List<BuilderField<T, ?>> fields) throws IOException {
        BuilderField<T, ?> entry = null;
        for (BuilderField<T, ?> field : fields) {
            if (!field.supportsVersion(extraInfo.getVersion())) continue;
            entry = field;
            break;
        }
        if (entry == null) {
            reader.reset();
            this.readUnknownField(reader, extraInfo);
            return;
        }
        reader.unmark();
        reader.consumeWhiteSpace();
        reader.expect(':');
        reader.consumeWhiteSpace();
        extraInfo.pushKey(key, reader);
        try {
            if (entry.codec.getChildCodec() instanceof BuilderCodec) {
                BuilderCodec.decodeAndInheritJson(entry, reader, t, parent, extraInfo);
            } else {
                entry.decodeAndInheritJson(reader, t, parent, extraInfo);
            }
        }
        catch (Exception e) {
            throw new CodecException("Failed to decode", reader, extraInfo, (Throwable)e);
        }
        finally {
            extraInfo.popKey();
        }
    }

    private static <Type, FieldType> void decodeAndInheritJson(@Nonnull BuilderField<Type, FieldType> entry, @Nonnull RawJsonReader reader, Type t, @Nullable Type parent, @Nonnull ExtraInfo extraInfo) throws IOException {
        int read = reader.peek();
        if (read == -1) {
            throw new IOException("Unexpected EOF!");
        }
        switch (read) {
            case 78: 
            case 110: {
                reader.readNullValue();
                entry.setValue(t, null, extraInfo);
                return;
            }
        }
        BuilderCodec inheritCodec = (BuilderCodec)entry.codec.getChildCodec();
        Object value = inheritCodec.getSupplier().get();
        Object parentValue = parent != null ? (Object)entry.getter.apply(parent, extraInfo) : null;
        inheritCodec.decodeAndInheritJson(reader, value, parentValue, extraInfo);
        entry.setValue(t, value, extraInfo);
    }

    @Nonnull
    protected ExtraInfo decodeVersion(BsonDocument document, @Nonnull ExtraInfo extraInfo) {
        if (this.useLegacyVersion && extraInfo.getLegacyVersion() != Integer.MAX_VALUE) {
            return new VersionedExtraInfo(extraInfo.getLegacyVersion(), extraInfo);
        }
        int version = VERSION.get(document, extraInfo).orElse(0);
        if (version > this.codecVersion) {
            throw new IllegalArgumentException("Version " + version + " is newer than expected version " + this.codecVersion);
        }
        if (this.minCodecVersion != Integer.MAX_VALUE && version < this.minCodecVersion) {
            throw new IllegalArgumentException("Version " + version + " is older than min supported version " + this.minCodecVersion);
        }
        return new VersionedExtraInfo(version, extraInfo);
    }

    @Nonnull
    protected ExtraInfo decodeVersion(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        if (this.useLegacyVersion && extraInfo.getLegacyVersion() != Integer.MAX_VALUE) {
            return new VersionedExtraInfo(extraInfo.getLegacyVersion(), extraInfo);
        }
        reader.mark();
        int version = 0;
        if (RawJsonReader.seekToKey(reader, VERSION.getKey())) {
            version = reader.readIntValue();
        }
        if (version > this.codecVersion) {
            throw new IllegalArgumentException("Version " + version + " is newer than expected version " + this.codecVersion);
        }
        if (this.minCodecVersion != Integer.MAX_VALUE && version < this.minCodecVersion) {
            throw new IllegalArgumentException("Version " + version + " is older than min supported version " + this.minCodecVersion);
        }
        reader.reset();
        extraInfo.ignoreUnusedKey(VERSION.getKey());
        return new VersionedExtraInfo(version, extraInfo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void validate(T t, @Nonnull ExtraInfo extraInfo) {
        if (this.parentCodec != null) {
            this.parentCodec.validate(t, extraInfo);
        }
        for (List<BuilderField<T, ?>> entry : this.entries.values()) {
            BuilderField<T, ?> field = BuilderCodec.findField(entry, extraInfo);
            if (field == null) continue;
            extraInfo.pushKey(field.codec.getKey());
            try {
                field.validate(t, extraInfo);
            }
            finally {
                extraInfo.popKey();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void validateDefaults(@Nonnull ExtraInfo extraInfo, @Nonnull Set<Codec<?>> tested) {
        if (!tested.add(this)) {
            return;
        }
        T t = this.supplier.get();
        this.afterDecode(t, extraInfo);
        BuilderCodec<T> codec = this;
        while (codec != null) {
            for (List<BuilderField<T, ?>> entry : codec.entries.values()) {
                BuilderField<T, ?> field = BuilderCodec.findField(entry, extraInfo);
                if (field == null) continue;
                extraInfo.pushKey(field.codec.getKey());
                try {
                    field.validateDefaults(t, extraInfo, tested);
                }
                finally {
                    extraInfo.popKey();
                }
            }
            codec = codec.parentCodec;
        }
    }

    @Override
    @Nonnull
    public ObjectSchema toSchema(@Nonnull SchemaContext context) {
        T t = this.getDefaultValue();
        return this.toSchema(context, (Object)t);
    }

    @Override
    @Nonnull
    public ObjectSchema toSchema(@Nonnull SchemaContext context, @Nullable T def) {
        ObjectSchema schema = new ObjectSchema();
        schema.setAdditionalProperties(false);
        schema.setTitle(this.tClass.getSimpleName());
        schema.setMarkdownDescription(this.documentation);
        schema.getHytale().setMergesProperties(true);
        Object2ObjectLinkedOpenHashMap<String, Schema> properties = new Object2ObjectLinkedOpenHashMap<String, Schema>();
        if (this.versioned) {
            properties.put(VERSION.getKey(), VERSION.getChildCodec().toSchema(context));
        }
        Schema comment = new Schema();
        comment.getHytale().setUiPropertyTitle("Comment");
        comment.setDoNotSuggest(true);
        comment.setDescription("Comments don't have any function other than allowing users to add certain internal comments or notes to an asset");
        UIDisplayMode.HIDDEN.modify(comment);
        properties.put("$Title", comment);
        properties.put("$Comment", comment);
        properties.put("$Author", comment);
        properties.put("$TODO", comment);
        properties.put("$Position", comment);
        properties.put("$FloatingFunctionNodes", comment);
        properties.put("$Groups", comment);
        properties.put("$WorkspaceID", comment);
        properties.put("$NodeId", comment);
        properties.put("$NodeEditorMetadata", comment);
        schema.setProperties(properties);
        BuilderCodec.createSchemaFields(context, def, this, properties);
        if (this.metadata != null) {
            for (int i = 0; i < this.metadata.size(); ++i) {
                Metadata meta = this.metadata.get(i);
                meta.modify(schema);
            }
        }
        return schema;
    }

    private static <T> void createSchemaFields(@Nonnull SchemaContext context, @Nullable T def, @Nonnull BuilderCodec<T> codec, @Nonnull Map<String, Schema> properties) {
        if (codec.parentCodec != null) {
            BuilderCodec.createSchemaFields(context, def, codec.parentCodec, properties);
        }
        for (Map.Entry<String, List<BuilderField<T, ?>>> entry : codec.getEntries().entrySet()) {
            Object defC;
            String key = entry.getKey();
            List<BuilderField<T, ?>> fields = entry.getValue();
            BuilderField<T, ?> field = fields.getLast();
            Codec<?> c = field.getCodec().getChildCodec();
            try {
                defC = field.getter.apply(def, EmptyExtraInfo.EMPTY);
            }
            catch (UnsupportedOperationException e) {
                continue;
            }
            Schema fieldSchema = context.refDefinition(c, defC);
            field.updateSchema(context, fieldSchema);
            Schema finalSchema = fieldSchema;
            String type = (String)Schema.CODEC.getIdFor(fieldSchema.getClass());
            if (!type.isEmpty()) {
                if (!field.hasNonNullValidator() && !field.isPrimitive) {
                    fieldSchema.setTypes(new String[]{type, "null"});
                }
                properties.put(key, fieldSchema);
            } else if (field.hasNonNullValidator()) {
                properties.put(key, fieldSchema);
            } else {
                finalSchema = Schema.anyOf(fieldSchema, NullSchema.INSTANCE);
                properties.put(key, finalSchema);
            }
            finalSchema.setMarkdownDescription(field.getDocumentation());
        }
    }

    @Nonnull
    public String toString() {
        return "BuilderCodec{supplier=" + String.valueOf(this.supplier) + ", parentCodec=" + String.valueOf(this.parentCodec) + ", entries=" + String.valueOf(this.entries) + ", afterDecodeAndValidate=" + String.valueOf(this.afterDecode) + "}";
    }

    @Nullable
    protected static <T> BuilderField<? super T, ?> findEntry(@Nonnull BuilderCodec<? super T> current, String key, @Nonnull ExtraInfo extraInfo) {
        BuilderField<T, ?> field;
        List<BuilderField<T, ?>> fields = current.entries.get(key);
        if (fields != null && fields.size() == 1 && (field = fields.getFirst()).supportsVersion(extraInfo.getVersion())) {
            return field;
        }
        BuilderField<T, ?> entry = null;
        while (current != null) {
            entry = BuilderCodec.findField(current.entries.get(key), extraInfo);
            if (entry != null) {
                return entry;
            }
            current = current.parentCodec;
        }
        return entry;
    }

    @Nullable
    protected static <T, F extends BuilderField<T, ?>> F findField(@Nullable List<F> entry, @Nonnull ExtraInfo extraInfo) {
        if (entry == null) {
            return null;
        }
        int size = entry.size();
        for (int i = 0; i < size; ++i) {
            BuilderField field = (BuilderField)entry.get(i);
            if (!field.supportsVersion(extraInfo.getVersion())) continue;
            return (F)field;
        }
        return null;
    }

    @Nonnull
    public static <T> Builder<T> builder(Class<T> tClass, Supplier<T> supplier) {
        return new Builder<T>(tClass, supplier);
    }

    @Nonnull
    public static <T> Builder<T> builder(Class<T> tClass, Supplier<T> supplier, BuilderCodec<? super T> parentCodec) {
        return new Builder<T>(tClass, supplier, parentCodec);
    }

    @Nonnull
    public static <T> Builder<T> abstractBuilder(Class<T> tClass) {
        return new Builder<T>(tClass, null);
    }

    @Nonnull
    public static <T> Builder<T> abstractBuilder(Class<T> tClass, BuilderCodec<? super T> parentCodec) {
        return new Builder<T>(tClass, null, parentCodec);
    }

    public static abstract class BuilderBase<T, S extends BuilderBase<T, S>> {
        protected final Class<T> tClass;
        protected final Supplier<T> supplier;
        @Nullable
        protected final BuilderCodec<? super T> parentCodec;
        protected final Map<String, List<BuilderField<T, ?>>> entries = new Object2ObjectLinkedOpenHashMap();
        @Nonnull
        protected final StringTreeMap<KeyEntry<T>> stringTreeMap;
        protected BiConsumer<T, ValidationResults> validator;
        protected BiConsumer<T, ExtraInfo> afterDecode;
        protected String documentation;
        protected List<Metadata> metadata;
        protected int codecVersion = Integer.MIN_VALUE;
        protected int minCodecVersion = Integer.MAX_VALUE;
        protected boolean versioned = false;
        protected boolean useLegacyVersion = false;

        protected BuilderBase(Class<T> tClass, Supplier<T> supplier) {
            this(tClass, supplier, null);
        }

        protected BuilderBase(Class<T> tClass, Supplier<T> supplier, @Nullable BuilderCodec<? super T> parentCodec) {
            this.tClass = tClass;
            this.supplier = supplier;
            this.parentCodec = parentCodec;
            this.stringTreeMap = parentCodec != null ? new StringTreeMap(parentCodec.stringTreeMap) : new StringTreeMap(Map.ofEntries(Map.entry("$Title", new KeyEntry(EntryType.IGNORE)), Map.entry("$Comment", new KeyEntry(EntryType.IGNORE)), Map.entry("$TODO", new KeyEntry(EntryType.IGNORE)), Map.entry("$Author", new KeyEntry(EntryType.IGNORE)), Map.entry("$Position", new KeyEntry(EntryType.IGNORE)), Map.entry("$FloatingFunctionNodes", new KeyEntry(EntryType.IGNORE)), Map.entry("$Groups", new KeyEntry(EntryType.IGNORE)), Map.entry("$WorkspaceID", new KeyEntry(EntryType.IGNORE)), Map.entry("$NodeEditorMetadata", new KeyEntry(EntryType.IGNORE)), Map.entry("$NodeId", new KeyEntry(EntryType.IGNORE)), Map.entry("Parent", new KeyEntry(EntryType.IGNORE_IN_BASE_OBJECT))));
        }

        private S self() {
            return (S)this;
        }

        @Nonnull
        public S documentation(String doc) {
            this.documentation = doc;
            return this.self();
        }

        @Nonnull
        public S versioned() {
            this.versioned = true;
            return this.self();
        }

        @Nonnull
        @Deprecated
        public S legacyVersioned() {
            this.versioned = true;
            this.useLegacyVersion = true;
            return this.self();
        }

        @Nonnull
        @Deprecated
        public <FieldType> S addField(@Nonnull KeyedCodec<FieldType> codec, @Nonnull BiConsumer<T, FieldType> setter, @Nonnull Function<T, FieldType> getter) {
            return this.addField(new BuilderField<Object, Object>(codec, (t, fieldType, extraInfo) -> setter.accept(t, fieldType), (t1, extraInfo1) -> getter.apply(t1), null));
        }

        @Nonnull
        public <FieldType> BuilderField.FieldBuilder<T, FieldType, S> append(KeyedCodec<FieldType> codec, @Nonnull BiConsumer<T, FieldType> setter, @Nonnull Function<T, FieldType> getter) {
            return this.append(codec, (T t, FieldType fieldType, ExtraInfo extraInfo) -> setter.accept(t, fieldType), (T t, ExtraInfo extraInfo) -> getter.apply(t));
        }

        @Nonnull
        public <FieldType> BuilderField.FieldBuilder<T, FieldType, S> append(KeyedCodec<FieldType> codec, TriConsumer<T, FieldType, ExtraInfo> setter, BiFunction<T, ExtraInfo, FieldType> getter) {
            return new BuilderField.FieldBuilder<T, FieldType, S>(this.self(), codec, setter, getter, null);
        }

        @Nonnull
        public <FieldType> BuilderField.FieldBuilder<T, FieldType, S> appendInherited(KeyedCodec<FieldType> codec, @Nonnull BiConsumer<T, FieldType> setter, @Nonnull Function<T, FieldType> getter, @Nonnull BiConsumer<T, T> inherit) {
            return this.appendInherited(codec, (T t, FieldType fieldType, ExtraInfo extraInfo) -> setter.accept(t, fieldType), (T t, ExtraInfo extraInfo) -> getter.apply(t), (T t, T parent, ExtraInfo extraInfo) -> inherit.accept(t, parent));
        }

        @Nonnull
        public <FieldType> BuilderField.FieldBuilder<T, FieldType, S> appendInherited(KeyedCodec<FieldType> codec, TriConsumer<T, FieldType, ExtraInfo> setter, BiFunction<T, ExtraInfo, FieldType> getter, TriConsumer<T, T, ExtraInfo> inherit) {
            return new BuilderField.FieldBuilder<T, FieldType, S>(this.self(), codec, setter, getter, inherit);
        }

        @Nonnull
        public <FieldType> S addField(@Nonnull BuilderField<T, FieldType> entry) {
            if (entry.getMinVersion() > entry.getMaxVersion()) {
                throw new IllegalArgumentException("Min version must be less than the max version: " + String.valueOf(entry));
            }
            List fields = this.entries.computeIfAbsent(entry.getCodec().getKey(), k -> new ObjectArrayList());
            for (BuilderField field : fields) {
                if (entry.getMaxVersion() < field.getMinVersion() || entry.getMinVersion() > field.getMaxVersion()) continue;
                throw new IllegalArgumentException("Field already defined for this version range!");
            }
            fields.add(entry);
            this.stringTreeMap.put(entry.getCodec().getKey(), new KeyEntry(fields));
            return this.self();
        }

        @Nonnull
        public S afterDecode(@Nonnull Consumer<T> afterDecode) {
            Objects.requireNonNull(afterDecode, "afterDecodeAndValidate can't be null!");
            return this.afterDecode((T t, ExtraInfo extraInfo) -> afterDecode.accept(t));
        }

        @Nonnull
        public S afterDecode(BiConsumer<T, ExtraInfo> afterDecode) {
            this.afterDecode = Objects.requireNonNull(afterDecode, "afterDecodeAndValidate can't be null!");
            return this.self();
        }

        @Nonnull
        @Deprecated
        public S validator(BiConsumer<T, ValidationResults> validator) {
            this.validator = Objects.requireNonNull(validator, "validator can't be null!");
            return this.self();
        }

        @Nonnull
        public S metadata(Metadata metadata) {
            if (this.metadata == null) {
                this.metadata = new ObjectArrayList<Metadata>();
            }
            this.metadata.add(metadata);
            return this.self();
        }

        @Nonnull
        public S codecVersion(int minCodecVersion, int codecVersion) {
            this.minCodecVersion = minCodecVersion;
            this.codecVersion = codecVersion;
            return this.self();
        }

        @Nonnull
        public S codecVersion(int codecVersion) {
            this.minCodecVersion = 0;
            this.codecVersion = codecVersion;
            return this.self();
        }

        @Nonnull
        public BuilderCodec<T> build() {
            return new BuilderCodec(this);
        }
    }

    protected static class KeyEntry<T> {
        private final EntryType type;
        @Nullable
        private final List<BuilderField<T, ?>> fields;

        public KeyEntry(EntryType type) {
            this.type = type;
            this.fields = null;
        }

        public KeyEntry(List<BuilderField<T, ?>> fields) {
            this.type = EntryType.FIELD;
            this.fields = fields;
        }

        public EntryType getType() {
            return this.type;
        }

        @Nullable
        public List<BuilderField<T, ?>> getFields() {
            return this.fields;
        }
    }

    protected static enum EntryType {
        FIELD,
        IGNORE,
        IGNORE_IN_BASE_OBJECT;

    }

    public static class Builder<T>
    extends BuilderBase<T, Builder<T>> {
        protected Builder(Class<T> tClass, Supplier<T> supplier) {
            super(tClass, supplier);
        }

        protected Builder(Class<T> tClass, Supplier<T> supplier, @Nullable BuilderCodec<? super T> parentCodec) {
            super(tClass, supplier, parentCodec);
        }
    }
}

