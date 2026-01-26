/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.lookup.ObjectCodecMapCodec;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.BooleanSchema;
import com.hypixel.hytale.codec.schema.config.IntegerSchema;
import com.hypixel.hytale.codec.schema.config.NullSchema;
import com.hypixel.hytale.codec.schema.config.NumberSchema;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.codec.schema.metadata.ui.UIButton;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditorFeatures;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditorPreview;
import com.hypixel.hytale.codec.schema.metadata.ui.UIRebuildCaches;
import com.hypixel.hytale.codec.util.Documentation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonValue;

public class Schema {
    public static final ObjectCodecMapCodec<String, Schema> CODEC = new ObjectCodecMapCodec("type", new StringOrBlank(), true, false);
    public static final ArrayCodec<Schema> ARRAY_CODEC = new ArrayCodec(CODEC, Schema[]::new);
    public static final BuilderCodec<Schema> BASE_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Schema.class, Schema::new).addField(new KeyedCodec<String>("$id", Codec.STRING, false, true), (o, i) -> {
        o.id = i;
    }, o -> o.id)).addField(new KeyedCodec<String[]>("type", new ArrayOrNull(), false, true), (o, i) -> {
        o.types = i;
    }, o -> o.types)).addField(new KeyedCodec<String>("title", Codec.STRING, false, true), (o, i) -> {
        o.title = i;
    }, o -> o.title)).addField(new KeyedCodec<String>("description", Codec.STRING, false, true), (o, i) -> {
        o.description = i;
    }, o -> {
        if (o.description == null && o.markdownDescription != null) {
            return Documentation.stripMarkdown(o.markdownDescription);
        }
        return o.description;
    })).addField(new KeyedCodec<String>("markdownDescription", Codec.STRING, false, true), (o, i) -> {
        o.markdownDescription = i;
    }, o -> o.markdownDescription)).addField(new KeyedCodec<T[]>("enumDescriptions", Codec.STRING_ARRAY, false, true), (o, i) -> {
        o.enumDescriptions = i;
    }, o -> {
        if (o.enumDescriptions == null && o.markdownEnumDescriptions != null) {
            String[] enumDescriptions = new String[o.markdownEnumDescriptions.length];
            for (int i = 0; i < enumDescriptions.length; ++i) {
                enumDescriptions[i] = Documentation.stripMarkdown(o.markdownEnumDescriptions[i]);
            }
            return enumDescriptions;
        }
        return o.enumDescriptions;
    })).addField(new KeyedCodec<T[]>("markdownEnumDescriptions", Codec.STRING_ARRAY, false, true), (o, i) -> {
        o.markdownEnumDescriptions = i;
    }, o -> o.markdownEnumDescriptions)).addField(new KeyedCodec<T[]>("anyOf", ARRAY_CODEC, false, true), (o, i) -> {
        o.anyOf = i;
    }, o -> o.anyOf)).addField(new KeyedCodec<T[]>("oneOf", ARRAY_CODEC, false, true), (o, i) -> {
        o.oneOf = i;
    }, o -> o.oneOf)).addField(new KeyedCodec<T[]>("allOf", ARRAY_CODEC, false, true), (o, i) -> {
        o.allOf = i;
    }, o -> o.allOf)).addField(new KeyedCodec("not", CODEC, false, true), (o, i) -> {
        o.not = i;
    }, o -> o.not)).addField(new KeyedCodec("if", CODEC, false, true), (o, i) -> {
        o.if_ = i;
    }, o -> o.if_)).addField(new KeyedCodec("then", CODEC, false, true), (o, i) -> {
        o.then = i;
    }, o -> o.then)).addField(new KeyedCodec<Object>("else", new BooleanOrSchema(), false, true), (o, i) -> {
        o.else_ = i;
    }, o -> o.else_)).addField(new KeyedCodec<T[]>("required", Codec.STRING_ARRAY, false, true), (o, i) -> {
        o.required = i;
    }, o -> o.required)).addField(new KeyedCodec<BsonDocument>("default", Codec.BSON_DOCUMENT, false, true), (o, i) -> {
        o.default_ = i;
    }, o -> o.default_)).addField(new KeyedCodec("definitions", new MapCodec(CODEC, HashMap::new), false, true), (o, i) -> {
        o.definitions = i;
    }, o -> o.definitions)).addField(new KeyedCodec<String>("$ref", Codec.STRING, false, true), (o, i) -> {
        o.ref = i;
    }, o -> o.ref)).addField(new KeyedCodec<String>("$data", Codec.STRING, false, true), (o, i) -> {
        o.data = i;
    }, o -> o.data)).addField(new KeyedCodec<Boolean>("doNotSuggest", Codec.BOOLEAN, false, true), (o, i) -> {
        o.doNotSuggest = i;
    }, o -> o.doNotSuggest)).addField(new KeyedCodec<String>("hytaleAssetRef", Codec.STRING, false, true), (o, i) -> {
        o.hytaleAssetRef = i;
    }, o -> o.hytaleAssetRef)).addField(new KeyedCodec<String>("hytaleCustomAssetRef", Codec.STRING, false, true), (o, i) -> {
        o.hytaleCustomAssetRef = i;
    }, o -> o.hytaleCustomAssetRef)).addField(new KeyedCodec<InheritSettings>("hytaleParent", InheritSettings.CODEC, false, true), (o, i) -> {
        o.hytaleParent = i;
    }, o -> o.hytaleParent)).addField(new KeyedCodec<SchemaTypeField>("hytaleSchemaTypeField", SchemaTypeField.CODEC, false, true), (o, i) -> {
        o.hytaleSchemaTypeField = i;
    }, o -> o.hytaleSchemaTypeField)).addField(new KeyedCodec<HytaleMetadata>("hytale", HytaleMetadata.CODEC, false, true), (o, i) -> {
        if (i.type == null) {
            i.type = (String)CODEC.getIdFor(o.getClass());
        }
        o.hytale = i;
    }, o -> o.hytale)).build();
    private String id;
    private String[] types;
    private String title;
    private String description;
    private String markdownDescription;
    private Schema[] anyOf;
    private Schema[] oneOf;
    private Schema[] allOf;
    private Schema not;
    private String[] required;
    private String[] enumDescriptions;
    private String[] markdownEnumDescriptions;
    private Map<String, Schema> definitions;
    private String ref;
    private String data;
    private BsonDocument default_;
    private Schema if_;
    private Schema then;
    private Object else_;
    private HytaleMetadata hytale;
    private InheritSettings hytaleParent;
    private SchemaTypeField hytaleSchemaTypeField;
    private String hytaleAssetRef;
    private String hytaleCustomAssetRef;
    private Boolean doNotSuggest;

    public Schema() {
        String id = (String)CODEC.getIdFor(this.getClass());
        if (id != null && !id.isBlank()) {
            this.hytale = new HytaleMetadata(id);
        }
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getTypes() {
        return this.types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMarkdownDescription() {
        return this.markdownDescription;
    }

    public void setMarkdownDescription(String markdownDescription) {
        this.markdownDescription = markdownDescription;
    }

    public String[] getEnumDescriptions() {
        return this.enumDescriptions;
    }

    public void setEnumDescriptions(String[] enumDescriptions) {
        this.enumDescriptions = enumDescriptions;
    }

    public String[] getMarkdownEnumDescriptions() {
        return this.markdownEnumDescriptions;
    }

    public void setMarkdownEnumDescriptions(String[] markdownEnumDescriptions) {
        this.markdownEnumDescriptions = markdownEnumDescriptions;
    }

    public Schema[] getAnyOf() {
        return this.anyOf;
    }

    public void setAnyOf(Schema ... anyOf) {
        this.anyOf = anyOf;
    }

    public Schema[] getOneOf() {
        return this.oneOf;
    }

    public void setOneOf(Schema ... oneOf) {
        this.oneOf = oneOf;
    }

    public Schema[] getAllOf() {
        return this.allOf;
    }

    public void setAllOf(Schema ... allOf) {
        this.allOf = allOf;
    }

    public String[] getRequired() {
        return this.required;
    }

    public void setRequired(String ... required) {
        this.required = required;
    }

    public BsonDocument getDefaultRaw() {
        return this.default_;
    }

    public void setDefaultRaw(BsonDocument default_) {
        this.default_ = default_;
    }

    public Map<String, Schema> getDefinitions() {
        return this.definitions;
    }

    public void setDefinitions(Map<String, Schema> definitions) {
        this.definitions = definitions;
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Schema getIf() {
        return this.if_;
    }

    public void setIf(Schema if_) {
        this.if_ = if_;
    }

    public Schema getThen() {
        return this.then;
    }

    public void setThen(Schema then) {
        this.then = then;
    }

    public Schema getElse() {
        return (Schema)this.else_;
    }

    public void setElse(Schema else_) {
        this.else_ = else_;
    }

    public void setElse(boolean else_) {
        this.else_ = else_;
    }

    public Boolean isDoNotSuggest() {
        return this.doNotSuggest;
    }

    public void setDoNotSuggest(boolean doNotSuggest) {
        this.doNotSuggest = doNotSuggest;
    }

    @Nullable
    public HytaleMetadata getHytale() {
        return this.getHytale(true);
    }

    @Nullable
    public HytaleMetadata getHytale(boolean createInstance) {
        if (createInstance && this.hytale == null) {
            this.hytale = new HytaleMetadata();
            this.hytale.type = (String)CODEC.getIdFor(this.getClass());
        }
        return this.hytale;
    }

    public String getHytaleAssetRef() {
        return this.hytaleAssetRef;
    }

    public void setHytaleAssetRef(String hytaleAssetRef) {
        this.hytaleAssetRef = hytaleAssetRef;
    }

    public InheritSettings getHytaleParent() {
        return this.hytaleParent;
    }

    public void setHytaleParent(InheritSettings hytaleParent) {
        this.hytaleParent = hytaleParent;
    }

    public SchemaTypeField getHytaleSchemaTypeField() {
        return this.hytaleSchemaTypeField;
    }

    public void setHytaleSchemaTypeField(SchemaTypeField hytaleSchemaTypeField) {
        this.hytaleSchemaTypeField = hytaleSchemaTypeField;
    }

    public String getHytaleCustomAssetRef() {
        return this.hytaleCustomAssetRef;
    }

    public void setHytaleCustomAssetRef(String hytaleCustomAssetRef) {
        this.hytaleCustomAssetRef = hytaleCustomAssetRef;
    }

    @Nonnull
    public static Schema ref(String file) {
        Schema s = new Schema();
        s.setRef(file);
        return s;
    }

    @Nonnull
    public static Schema data(String file) {
        Schema s = new Schema();
        s.setData(file);
        return s;
    }

    @Nonnull
    public static Schema anyOf(Schema ... anyOf) {
        Schema s = new Schema();
        s.anyOf = anyOf;
        return s;
    }

    @Nonnull
    public static Schema not(Schema not) {
        Schema s = new Schema();
        s.not = not;
        return s;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Schema schema = (Schema)o;
        if (this.id != null ? !this.id.equals(schema.id) : schema.id != null) {
            return false;
        }
        if (!Arrays.equals(this.types, schema.types)) {
            return false;
        }
        if (this.title != null ? !this.title.equals(schema.title) : schema.title != null) {
            return false;
        }
        if (this.description != null ? !this.description.equals(schema.description) : schema.description != null) {
            return false;
        }
        if (this.markdownDescription != null ? !this.markdownDescription.equals(schema.markdownDescription) : schema.markdownDescription != null) {
            return false;
        }
        if (!Arrays.equals(this.anyOf, schema.anyOf)) {
            return false;
        }
        if (!Arrays.equals(this.oneOf, schema.oneOf)) {
            return false;
        }
        if (!Arrays.equals(this.allOf, schema.allOf)) {
            return false;
        }
        if (this.not != null ? !this.not.equals(schema.not) : schema.not != null) {
            return false;
        }
        if (!Arrays.equals(this.required, schema.required)) {
            return false;
        }
        if (!Arrays.equals(this.enumDescriptions, schema.enumDescriptions)) {
            return false;
        }
        if (!Arrays.equals(this.markdownEnumDescriptions, schema.markdownEnumDescriptions)) {
            return false;
        }
        if (this.definitions != null ? !this.definitions.equals(schema.definitions) : schema.definitions != null) {
            return false;
        }
        if (this.ref != null ? !this.ref.equals(schema.ref) : schema.ref != null) {
            return false;
        }
        if (this.data != null ? !this.data.equals(schema.data) : schema.data != null) {
            return false;
        }
        if (this.default_ != null ? !this.default_.equals(schema.default_) : schema.default_ != null) {
            return false;
        }
        if (this.hytale != null ? !this.hytale.equals(schema.hytale) : schema.hytale != null) {
            return false;
        }
        if (this.hytaleParent != null ? !this.hytaleParent.equals(schema.hytaleParent) : schema.hytaleParent != null) {
            return false;
        }
        if (this.hytaleSchemaTypeField != null ? !this.hytaleSchemaTypeField.equals(schema.hytaleSchemaTypeField) : schema.hytaleSchemaTypeField != null) {
            return false;
        }
        if (this.hytaleAssetRef != null ? !this.hytaleAssetRef.equals(schema.hytaleAssetRef) : schema.hytaleAssetRef != null) {
            return false;
        }
        return this.hytaleCustomAssetRef != null ? this.hytaleCustomAssetRef.equals(schema.hytaleCustomAssetRef) : schema.hytaleCustomAssetRef == null;
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(this.types);
        result = 31 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 31 * result + (this.description != null ? this.description.hashCode() : 0);
        result = 31 * result + (this.markdownDescription != null ? this.markdownDescription.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.anyOf);
        result = 31 * result + Arrays.hashCode(this.oneOf);
        result = 31 * result + Arrays.hashCode(this.allOf);
        result = 31 * result + (this.not != null ? this.not.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.required);
        result = 31 * result + Arrays.hashCode(this.enumDescriptions);
        result = 31 * result + Arrays.hashCode(this.markdownEnumDescriptions);
        result = 31 * result + (this.definitions != null ? this.definitions.hashCode() : 0);
        result = 31 * result + (this.ref != null ? this.ref.hashCode() : 0);
        result = 31 * result + (this.data != null ? this.data.hashCode() : 0);
        result = 31 * result + (this.default_ != null ? this.default_.hashCode() : 0);
        result = 31 * result + (this.hytale != null ? this.hytale.hashCode() : 0);
        result = 31 * result + (this.hytaleParent != null ? this.hytaleParent.hashCode() : 0);
        result = 31 * result + (this.hytaleSchemaTypeField != null ? this.hytaleSchemaTypeField.hashCode() : 0);
        result = 31 * result + (this.hytaleAssetRef != null ? this.hytaleAssetRef.hashCode() : 0);
        result = 31 * result + (this.hytaleCustomAssetRef != null ? this.hytaleCustomAssetRef.hashCode() : 0);
        return result;
    }

    public static void init() {
        CODEC.register(Priority.DEFAULT, (Object)"", Schema.class, BASE_CODEC);
        CODEC.register((Object)"null", NullSchema.class, NullSchema.CODEC);
        CODEC.register((Object)"string", StringSchema.class, StringSchema.CODEC);
        CODEC.register((Object)"number", NumberSchema.class, NumberSchema.CODEC);
        CODEC.register((Object)"integer", IntegerSchema.class, IntegerSchema.CODEC);
        CODEC.register((Object)"array", ArraySchema.class, ArraySchema.CODEC);
        CODEC.register((Object)"boolean", BooleanSchema.class, BooleanSchema.CODEC);
        CODEC.register((Object)"object", ObjectSchema.class, ObjectSchema.CODEC);
        UIEditor.init();
    }

    public static class HytaleMetadata {
        public static final BuilderCodec<HytaleMetadata> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HytaleMetadata.class, HytaleMetadata::new).addField(new KeyedCodec<String>("type", Codec.STRING, false, true), (o, i) -> {
            o.type = i;
        }, o -> {
            if (o.type != null && o.type.isEmpty()) {
                return null;
            }
            return o.type;
        })).addField(new KeyedCodec<T[]>("internalKeys", Codec.STRING_ARRAY, false, true), (o, i) -> {
            o.internalKeys = i;
        }, o -> o.internalKeys)).addField(new KeyedCodec<String>("path", Codec.STRING, false, true), (o, i) -> {
            o.path = i;
        }, o -> o.path)).addField(new KeyedCodec<String>("virtualPath", Codec.STRING, false, true), (o, i) -> {
            o.virtualPath = i;
        }, o -> o.virtualPath)).addField(new KeyedCodec<String>("extension", Codec.STRING, false, true), (o, i) -> {
            o.extension = i;
        }, o -> o.extension)).addField(new KeyedCodec<String>("idProvider", Codec.STRING, false, true), (o, i) -> {
            o.idProvider = i;
        }, o -> o.idProvider)).addField(new KeyedCodec<Boolean>("inheritsProperty", Codec.BOOLEAN, false, true), (o, i) -> {
            o.inheritsProperty = i;
        }, o -> o.inheritsProperty)).addField(new KeyedCodec<Boolean>("mergesProperties", Codec.BOOLEAN, false, true), (o, i) -> {
            o.mergesProperties = i;
        }, o -> o.mergesProperties)).addField(new KeyedCodec<UIDisplayMode.DisplayMode>("uiDisplayMode", new EnumCodec<UIDisplayMode.DisplayMode>(UIDisplayMode.DisplayMode.class), false, true), (o, i) -> {
            o.uiDisplayMode = i;
        }, o -> o.uiDisplayMode)).addField(new KeyedCodec<UIEditor.EditorComponent>("uiEditorComponent", UIEditor.CODEC, false, true), (o, i) -> {
            o.uiEditorComponent = i;
        }, o -> o.uiEditorComponent)).addField(new KeyedCodec<Boolean>("allowEmptyObject", Codec.BOOLEAN, false, true), (o, i) -> {
            o.allowEmptyObject = i;
        }, o -> o.allowEmptyObject)).addField(new KeyedCodec<Boolean>("uiEditorIgnore", Codec.BOOLEAN, false, true), (o, i) -> {
            o.uiEditorIgnore = i;
        }, o -> o.uiEditorIgnore)).addField(new KeyedCodec<T[]>("uiEditorFeatures", new ArrayCodec<UIEditorFeatures.EditorFeature>(new EnumCodec<UIEditorFeatures.EditorFeature>(UIEditorFeatures.EditorFeature.class), UIEditorFeatures.EditorFeature[]::new), false, true), (o, i) -> {
            o.uiEditorFeatures = i;
        }, o -> o.uiEditorFeatures)).addField(new KeyedCodec<UIEditorPreview.PreviewType>("uiEditorPreview", new EnumCodec<UIEditorPreview.PreviewType>(UIEditorPreview.PreviewType.class), false, true), (o, i) -> {
            o.uiEditorPreview = i;
        }, o -> o.uiEditorPreview)).addField(new KeyedCodec<String>("uiTypeIcon", Codec.STRING, false, true), (o, i) -> {
            o.uiTypeIcon = i;
        }, o -> o.uiTypeIcon)).addField(new KeyedCodec<String>("uiPropertyTitle", Codec.STRING, false, true), (o, i) -> {
            o.uiPropertyTitle = i;
        }, o -> o.uiPropertyTitle)).addField(new KeyedCodec<String>("uiSectionStart", Codec.STRING, false, true), (o, i) -> {
            o.uiSectionStart = i;
        }, o -> o.uiSectionStart)).addField(new KeyedCodec<T[]>("uiRebuildCaches", new ArrayCodec<UIRebuildCaches.ClientCache>(new EnumCodec<UIRebuildCaches.ClientCache>(UIRebuildCaches.ClientCache.class), UIRebuildCaches.ClientCache[]::new), false, true), (o, i) -> {
            o.uiRebuildCaches = i;
        }, o -> o.uiRebuildCaches)).addField(new KeyedCodec<T[]>("uiSidebarButtons", new ArrayCodec<UIButton>(UIButton.CODEC, UIButton[]::new), false, true), (o, i) -> {
            o.uiSidebarButtons = i;
        }, o -> o.uiSidebarButtons)).addField(new KeyedCodec<Boolean>("uiRebuildCachesForChildProperties", Codec.BOOLEAN, false, true), (o, i) -> {
            o.uiRebuildCachesForChildProperties = i;
        }, o -> o.uiRebuildCachesForChildProperties)).addField(new KeyedCodec<Boolean>("uiCollapsedByDefault", Codec.BOOLEAN, false, true), (o, i) -> {
            o.uiCollapsedByDefault = i;
        }, o -> o.uiCollapsedByDefault)).addField(new KeyedCodec<T[]>("uiCreateButtons", new ArrayCodec<UIButton>(UIButton.CODEC, UIButton[]::new), false, true), (o, i) -> {
            o.uiCreateButtons = i;
        }, o -> o.uiCreateButtons)).build();
        private String type;
        private String path;
        private String virtualPath;
        private String extension;
        private String idProvider;
        private String[] internalKeys;
        private Boolean inheritsProperty;
        private Boolean mergesProperties;
        private UIEditorFeatures.EditorFeature[] uiEditorFeatures;
        private UIEditorPreview.PreviewType uiEditorPreview;
        private String uiTypeIcon;
        private Boolean uiEditorIgnore;
        private Boolean allowEmptyObject;
        private UIDisplayMode.DisplayMode uiDisplayMode;
        private UIEditor.EditorComponent uiEditorComponent;
        private String uiPropertyTitle;
        private String uiSectionStart;
        private UIRebuildCaches.ClientCache[] uiRebuildCaches;
        private Boolean uiRebuildCachesForChildProperties;
        private UIButton[] uiSidebarButtons;
        private Boolean uiCollapsedByDefault;
        private UIButton[] uiCreateButtons;

        public HytaleMetadata(String type) {
            this.type = type;
        }

        public HytaleMetadata() {
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getVirtualPath() {
            return this.virtualPath;
        }

        public void setVirtualPath(String virtualPath) {
            this.virtualPath = virtualPath;
        }

        public String getExtension() {
            return this.extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String getIdProvider() {
            return this.idProvider;
        }

        public void setIdProvider(String idProvider) {
            this.idProvider = idProvider;
        }

        public String[] getInternalKeys() {
            return this.internalKeys;
        }

        public void setInternalKeys(String[] internalKeys) {
            this.internalKeys = internalKeys;
        }

        public UIDisplayMode.DisplayMode getUiDisplayMode() {
            return this.uiDisplayMode;
        }

        public void setUiDisplayMode(UIDisplayMode.DisplayMode uiDisplayMode) {
            this.uiDisplayMode = uiDisplayMode;
        }

        public UIEditor.EditorComponent getUiEditorComponent() {
            return this.uiEditorComponent;
        }

        public void setUiEditorComponent(UIEditor.EditorComponent uiEditorComponent) {
            this.uiEditorComponent = uiEditorComponent;
        }

        public UIEditorFeatures.EditorFeature[] getUiEditorFeatures() {
            return this.uiEditorFeatures;
        }

        public void setUiEditorFeatures(UIEditorFeatures.EditorFeature[] uiEditorFeatures) {
            this.uiEditorFeatures = uiEditorFeatures;
        }

        public UIEditorPreview.PreviewType getUiEditorPreview() {
            return this.uiEditorPreview;
        }

        public void setUiEditorPreview(UIEditorPreview.PreviewType uiEditorPreview) {
            this.uiEditorPreview = uiEditorPreview;
        }

        public String getUiTypeIcon() {
            return this.uiTypeIcon;
        }

        public void setUiTypeIcon(String uiTypeIcon) {
            this.uiTypeIcon = uiTypeIcon;
        }

        public Boolean getUiEditorIgnore() {
            return this.uiEditorIgnore;
        }

        public void setUiEditorIgnore(Boolean uiEditorIgnore) {
            this.uiEditorIgnore = uiEditorIgnore;
        }

        public Boolean getAllowEmptyObject() {
            return this.allowEmptyObject;
        }

        public void setAllowEmptyObject(Boolean allowEmptyObject) {
            this.allowEmptyObject = allowEmptyObject;
        }

        public String getUiPropertyTitle() {
            return this.uiPropertyTitle;
        }

        public void setUiPropertyTitle(String uiPropertyTitle) {
            this.uiPropertyTitle = uiPropertyTitle;
        }

        public String getUiSectionStart() {
            return this.uiSectionStart;
        }

        public void setUiSectionStart(String uiSectionStart) {
            this.uiSectionStart = uiSectionStart;
        }

        public boolean isInheritsProperty() {
            return this.inheritsProperty;
        }

        public void setInheritsProperty(boolean inheritsProperty) {
            this.inheritsProperty = inheritsProperty;
        }

        public boolean getMergesProperties() {
            return this.mergesProperties;
        }

        public void setMergesProperties(boolean mergesProperties) {
            this.mergesProperties = mergesProperties;
        }

        public UIRebuildCaches.ClientCache[] getUiRebuildCaches() {
            return this.uiRebuildCaches;
        }

        public void setUiRebuildCaches(UIRebuildCaches.ClientCache[] uiRebuildCaches) {
            this.uiRebuildCaches = uiRebuildCaches;
        }

        public Boolean getUiRebuildCachesForChildProperties() {
            return this.uiRebuildCachesForChildProperties;
        }

        public void setUiRebuildCachesForChildProperties(Boolean uiRebuildCachesForChildProperties) {
            this.uiRebuildCachesForChildProperties = uiRebuildCachesForChildProperties;
        }

        public UIButton[] getUiSidebarButtons() {
            return this.uiSidebarButtons;
        }

        public void setUiSidebarButtons(UIButton[] uiSidebarButtons) {
            this.uiSidebarButtons = uiSidebarButtons;
        }

        public Boolean getUiCollapsedByDefault() {
            return this.uiCollapsedByDefault;
        }

        public void setUiCollapsedByDefault(Boolean uiCollapsedByDefault) {
            this.uiCollapsedByDefault = uiCollapsedByDefault;
        }

        public UIButton[] getUiCreateButtons() {
            return this.uiCreateButtons;
        }

        public void setUiCreateButtons(UIButton[] uiCreateButtons) {
            this.uiCreateButtons = uiCreateButtons;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            HytaleMetadata that = (HytaleMetadata)o;
            if (this.type != null ? !this.type.equals(that.type) : that.type != null) {
                return false;
            }
            if (this.path != null ? !this.path.equals(that.path) : that.path != null) {
                return false;
            }
            if (this.virtualPath != null ? !this.virtualPath.equals(that.virtualPath) : that.virtualPath != null) {
                return false;
            }
            if (this.extension != null ? !this.extension.equals(that.extension) : that.extension != null) {
                return false;
            }
            if (this.idProvider != null ? !this.idProvider.equals(that.idProvider) : that.idProvider != null) {
                return false;
            }
            if (!Arrays.equals(this.internalKeys, that.internalKeys)) {
                return false;
            }
            if (this.inheritsProperty != null ? !this.inheritsProperty.equals(that.inheritsProperty) : that.inheritsProperty != null) {
                return false;
            }
            if (this.mergesProperties != null ? !this.mergesProperties.equals(that.mergesProperties) : that.mergesProperties != null) {
                return false;
            }
            if (!Arrays.equals((Object[])this.uiEditorFeatures, (Object[])that.uiEditorFeatures)) {
                return false;
            }
            if (this.uiEditorPreview != that.uiEditorPreview) {
                return false;
            }
            if (this.uiTypeIcon != null ? !this.uiTypeIcon.equals(that.uiTypeIcon) : that.uiTypeIcon != null) {
                return false;
            }
            if (this.uiEditorIgnore != null ? !this.uiEditorIgnore.equals(that.uiEditorIgnore) : that.uiEditorIgnore != null) {
                return false;
            }
            if (this.allowEmptyObject != null ? !this.allowEmptyObject.equals(that.allowEmptyObject) : that.allowEmptyObject != null) {
                return false;
            }
            if (this.uiDisplayMode != that.uiDisplayMode) {
                return false;
            }
            if (this.uiEditorComponent != null ? !this.uiEditorComponent.equals(that.uiEditorComponent) : that.uiEditorComponent != null) {
                return false;
            }
            if (this.uiPropertyTitle != null ? !this.uiPropertyTitle.equals(that.uiPropertyTitle) : that.uiPropertyTitle != null) {
                return false;
            }
            if (this.uiSectionStart != null ? !this.uiSectionStart.equals(that.uiSectionStart) : that.uiSectionStart != null) {
                return false;
            }
            if (!Arrays.equals((Object[])this.uiRebuildCaches, (Object[])that.uiRebuildCaches)) {
                return false;
            }
            if (this.uiRebuildCachesForChildProperties != null ? !this.uiRebuildCachesForChildProperties.equals(that.uiRebuildCachesForChildProperties) : that.uiRebuildCachesForChildProperties != null) {
                return false;
            }
            if (!Arrays.equals(this.uiSidebarButtons, that.uiSidebarButtons)) {
                return false;
            }
            if (this.uiCollapsedByDefault != null ? !this.uiCollapsedByDefault.equals(that.uiCollapsedByDefault) : that.uiCollapsedByDefault != null) {
                return false;
            }
            return Arrays.equals(this.uiCreateButtons, that.uiCreateButtons);
        }

        public int hashCode() {
            int result = this.type != null ? this.type.hashCode() : 0;
            result = 31 * result + (this.path != null ? this.path.hashCode() : 0);
            result = 31 * result + (this.virtualPath != null ? this.virtualPath.hashCode() : 0);
            result = 31 * result + (this.extension != null ? this.extension.hashCode() : 0);
            result = 31 * result + (this.idProvider != null ? this.idProvider.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(this.internalKeys);
            result = 31 * result + (this.inheritsProperty != null ? this.inheritsProperty.hashCode() : 0);
            result = 31 * result + (this.mergesProperties != null ? this.mergesProperties.hashCode() : 0);
            result = 31 * result + Arrays.hashCode((Object[])this.uiEditorFeatures);
            result = 31 * result + (this.uiEditorPreview != null ? this.uiEditorPreview.hashCode() : 0);
            result = 31 * result + (this.uiTypeIcon != null ? this.uiTypeIcon.hashCode() : 0);
            result = 31 * result + (this.uiEditorIgnore != null ? this.uiEditorIgnore.hashCode() : 0);
            result = 31 * result + (this.allowEmptyObject != null ? this.allowEmptyObject.hashCode() : 0);
            result = 31 * result + (this.uiDisplayMode != null ? this.uiDisplayMode.hashCode() : 0);
            result = 31 * result + (this.uiEditorComponent != null ? this.uiEditorComponent.hashCode() : 0);
            result = 31 * result + (this.uiPropertyTitle != null ? this.uiPropertyTitle.hashCode() : 0);
            result = 31 * result + (this.uiSectionStart != null ? this.uiSectionStart.hashCode() : 0);
            result = 31 * result + Arrays.hashCode((Object[])this.uiRebuildCaches);
            result = 31 * result + (this.uiRebuildCachesForChildProperties != null ? this.uiRebuildCachesForChildProperties.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(this.uiSidebarButtons);
            result = 31 * result + (this.uiCollapsedByDefault != null ? this.uiCollapsedByDefault.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(this.uiCreateButtons);
            return result;
        }
    }

    public static class InheritSettings {
        public static final BuilderCodec<InheritSettings> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(InheritSettings.class, InheritSettings::new).addField(new KeyedCodec<String>("type", Codec.STRING, false, true), (o, i) -> {
            o.type = i;
        }, o -> o.type)).addField(new KeyedCodec<String>("mapKey", Codec.STRING, false, true), (o, i) -> {
            o.mapKey = i;
        }, o -> o.mapKey)).addField(new KeyedCodec<String>("mapKeyValue", Codec.STRING, false, true), (o, i) -> {
            o.mapKeyValue = i;
        }, o -> o.mapKeyValue)).build();
        private String type;
        private String mapKey;
        private String mapKeyValue;

        public InheritSettings(String type) {
            this.type = type;
        }

        protected InheritSettings() {
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMapKey() {
            return this.mapKey;
        }

        public void setMapKey(String mapKey) {
            this.mapKey = mapKey;
        }

        public String getMapKeyValue() {
            return this.mapKeyValue;
        }

        public void setMapKeyValue(String mapKeyValue) {
            this.mapKeyValue = mapKeyValue;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            InheritSettings that = (InheritSettings)o;
            if (this.type != null ? !this.type.equals(that.type) : that.type != null) {
                return false;
            }
            if (this.mapKey != null ? !this.mapKey.equals(that.mapKey) : that.mapKey != null) {
                return false;
            }
            return this.mapKeyValue != null ? this.mapKeyValue.equals(that.mapKeyValue) : that.mapKeyValue == null;
        }

        public int hashCode() {
            int result = this.type != null ? this.type.hashCode() : 0;
            result = 31 * result + (this.mapKey != null ? this.mapKey.hashCode() : 0);
            result = 31 * result + (this.mapKeyValue != null ? this.mapKeyValue.hashCode() : 0);
            return result;
        }
    }

    public static class SchemaTypeField {
        public static final BuilderCodec<SchemaTypeField> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SchemaTypeField.class, SchemaTypeField::new).addField(new KeyedCodec<String>("property", Codec.STRING, false, true), (o, i) -> {
            o.property = i;
        }, o -> o.property)).addField(new KeyedCodec<String>("defaultValue", Codec.STRING, false, true), (o, i) -> {
            o.defaultValue = i;
        }, o -> o.defaultValue)).addField(new KeyedCodec<T[]>("values", Codec.STRING_ARRAY, false, true), (o, i) -> {
            o.values = i;
        }, o -> o.values)).addField(new KeyedCodec<String>("parentPropertyKey", Codec.STRING, false, true), (o, i) -> {
            o.parentPropertyKey = i;
        }, o -> o.parentPropertyKey)).build();
        private String property;
        private String defaultValue;
        private String[] values;
        private String parentPropertyKey;

        public SchemaTypeField(String property, String defaultValue, String ... values) {
            this.property = property;
            this.defaultValue = defaultValue;
            this.values = values;
        }

        protected SchemaTypeField() {
        }

        public String getProperty() {
            return this.property;
        }

        public String getDefaultValue() {
            return this.defaultValue;
        }

        public String[] getValues() {
            return this.values;
        }

        public String getParentPropertyKey() {
            return this.parentPropertyKey;
        }

        public void setParentPropertyKey(String parentPropertyKey) {
            this.parentPropertyKey = parentPropertyKey;
        }

        public boolean equals(Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            SchemaTypeField that = (SchemaTypeField)o;
            return Objects.equals(this.property, that.property) && Objects.equals(this.defaultValue, that.defaultValue) && Arrays.deepEquals(this.values, that.values) && Objects.equals(this.parentPropertyKey, that.parentPropertyKey);
        }

        public int hashCode() {
            int result = Objects.hashCode(this.property);
            result = 31 * result + Objects.hashCode(this.defaultValue);
            result = 31 * result + Arrays.hashCode(this.values);
            result = 31 * result + Objects.hashCode(this.parentPropertyKey);
            return result;
        }
    }

    @Deprecated
    private static class StringOrBlank
    implements Codec<String> {
        private StringOrBlank() {
        }

        @Override
        public String decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
            if (bsonValue.isString()) {
                return Codec.STRING.decode(bsonValue, extraInfo);
            }
            if (bsonValue.isArray()) {
                BsonArray arr = bsonValue.asArray();
                for (int i = 0; i < arr.size(); ++i) {
                    BsonValue val = arr.get(i);
                    if (val.asString().getValue().equals("null")) continue;
                    return Codec.STRING.decode(val, extraInfo);
                }
                throw new IllegalArgumentException("Unknown type (in array)");
            }
            return "";
        }

        @Override
        @Nonnull
        public BsonValue encode(@Nonnull String o, ExtraInfo extraInfo) {
            return Codec.STRING.encode(o, extraInfo);
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            return Schema.anyOf(new ArraySchema(), new StringSchema());
        }
    }

    @Deprecated
    private static class ArrayOrNull
    implements Codec<String[]> {
        private ArrayOrNull() {
        }

        @Override
        @Nullable
        public String[] decode(@Nonnull BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
            if (bsonValue.isArray()) {
                return Codec.STRING_ARRAY.decode(bsonValue, extraInfo);
            }
            return null;
        }

        @Override
        @Nonnull
        public BsonValue encode(@Nullable String[] o, ExtraInfo extraInfo) {
            if (o != null) {
                return Codec.STRING_ARRAY.encode((T[])o, extraInfo);
            }
            return new BsonNull();
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            return Schema.anyOf(new ArraySchema(), new NullSchema());
        }
    }

    @Deprecated
    protected static class BooleanOrSchema
    implements Codec<Object> {
        protected BooleanOrSchema() {
        }

        @Override
        public Object decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
            if (bsonValue.isBoolean()) {
                return Codec.BOOLEAN.decode(bsonValue, extraInfo);
            }
            return CODEC.decode(bsonValue, extraInfo);
        }

        @Override
        public BsonValue encode(Object o, ExtraInfo extraInfo) {
            if (o instanceof Boolean) {
                return Codec.BOOLEAN.encode((Boolean)o, extraInfo);
            }
            return CODEC.encode((Schema)o, extraInfo);
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            return Schema.anyOf(new BooleanSchema(), CODEC.toSchema(context));
        }
    }
}

