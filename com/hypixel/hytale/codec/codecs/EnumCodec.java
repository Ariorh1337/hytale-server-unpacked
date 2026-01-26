/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.codecs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonValue;

public class EnumCodec<T extends Enum<T>>
implements Codec<T> {
    @Nonnull
    private final Class<T> clazz;
    @Nonnull
    private final T[] enumConstants;
    @Nonnull
    private final String[] enumKeys;
    private final EnumStyle enumStyle;
    @Nonnull
    private final EnumMap<T, String> documentation;

    public EnumCodec(@Nonnull Class<T> clazz) {
        this(clazz, EnumStyle.CAMEL_CASE);
    }

    public EnumCodec(@Nonnull Class<T> clazz, EnumStyle enumStyle) {
        this.clazz = clazz;
        this.enumConstants = (Enum[])clazz.getEnumConstants();
        this.enumStyle = enumStyle;
        this.documentation = new EnumMap(clazz);
        EnumStyle currentStyle = EnumStyle.detect(this.enumConstants);
        this.enumKeys = new String[this.enumConstants.length];
        for (int i = 0; i < this.enumConstants.length; ++i) {
            T e = this.enumConstants[i];
            this.enumKeys[i] = currentStyle.formatCamelCase(((Enum)e).name());
        }
    }

    @Nonnull
    public EnumCodec<T> documentKey(T key, String doc) {
        this.documentation.put(key, doc);
        return this;
    }

    @Override
    @Nonnull
    public T decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
        String decode = STRING.decode(bsonValue, extraInfo);
        T value = this.getEnum(decode);
        if (value == null) {
            throw new IllegalArgumentException("Failed to apply function to '" + decode + "' decoded from '" + String.valueOf(bsonValue) + "'!");
        }
        return value;
    }

    @Override
    @Nonnull
    public BsonValue encode(@Nonnull T r, ExtraInfo extraInfo) {
        return switch (this.enumStyle.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> STRING.encode(this.enumKeys[((Enum)r).ordinal()], extraInfo);
            case 0 -> STRING.encode(((Enum)r).name(), extraInfo);
        };
    }

    @Override
    @Nonnull
    public T decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        String decode = STRING.decodeJson(reader, extraInfo);
        T value = this.getEnum(decode);
        if (value == null) {
            throw new IllegalArgumentException("Failed to apply function to '" + decode + "'!");
        }
        return value;
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        return this.toSchema(context, (T)null);
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context, @Nullable T def) {
        StringSchema enumSchema = new StringSchema();
        enumSchema.setTitle(this.clazz.getSimpleName());
        enumSchema.setEnum(this.enumKeys);
        enumSchema.getHytale().setType("Enum");
        String[] documentation = new String[this.enumKeys.length];
        for (int i = 0; i < this.enumKeys.length; ++i) {
            String desc = this.documentation.get(this.enumConstants[i]);
            documentation[i] = Objects.requireNonNullElse(desc, "");
        }
        enumSchema.setMarkdownEnumDescriptions(documentation);
        if (def != null) {
            enumSchema.setDefault(this.enumKeys[((Enum)def).ordinal()]);
        }
        return enumSchema;
    }

    @Nullable
    private T getEnum(String value) {
        return (T)this.enumStyle.match((Enum[])this.enumConstants, this.enumKeys, value);
    }

    public static enum EnumStyle {
        LEGACY,
        CAMEL_CASE;


        @Nullable
        public <T extends Enum<T>> T match(@Nonnull T[] enumConstants, @Nonnull String[] enumKeys, String value) {
            return (T)this.match((Enum[])enumConstants, enumKeys, value, false);
        }

        @Nullable
        public <T extends Enum<T>> T match(@Nonnull T[] enumConstants, @Nonnull String[] enumKeys, String value, boolean allowInvalid) {
            switch (this.ordinal()) {
                case 0: {
                    int i;
                    for (i = 0; i < enumConstants.length; ++i) {
                        T e = enumConstants[i];
                        if (!((Enum)e).name().equalsIgnoreCase(value)) continue;
                        return e;
                    }
                }
                case 1: {
                    int i;
                    for (i = 0; i < enumKeys.length; ++i) {
                        String key = enumKeys[i];
                        if (!key.equals(value)) continue;
                        return enumConstants[i];
                    }
                    break;
                }
            }
            if (allowInvalid) {
                return null;
            }
            throw new CodecException("Failed to find enum value for " + value);
        }

        @Nonnull
        public String formatCamelCase(@Nonnull String name) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    StringBuilder nameParts = new StringBuilder();
                    for (String part : name.split("_")) {
                        nameParts.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase());
                    }
                    yield nameParts.toString();
                }
                case 1 -> name;
            };
        }

        @Nonnull
        public static <T extends Enum<T>> EnumStyle detect(@Nonnull T[] enumConstants) {
            for (T e : enumConstants) {
                String name = ((Enum)e).name();
                if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
                    for (int i = 1; i < name.length(); ++i) {
                        char c = name.charAt(i);
                        if (!Character.isLetter(c) || !Character.isLowerCase(c)) continue;
                        return CAMEL_CASE;
                    }
                    continue;
                }
                return CAMEL_CASE;
            }
            return LEGACY;
        }
    }
}

