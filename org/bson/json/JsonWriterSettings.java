/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonBinary;
import org.bson.BsonMaxKey;
import org.bson.BsonMinKey;
import org.bson.BsonNull;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonUndefined;
import org.bson.BsonWriterSettings;
import org.bson.assertions.Assertions;
import org.bson.json.Converter;
import org.bson.json.ExtendedJsonBinaryConverter;
import org.bson.json.ExtendedJsonDateTimeConverter;
import org.bson.json.ExtendedJsonDecimal128Converter;
import org.bson.json.ExtendedJsonDoubleConverter;
import org.bson.json.ExtendedJsonInt32Converter;
import org.bson.json.ExtendedJsonInt64Converter;
import org.bson.json.ExtendedJsonMaxKeyConverter;
import org.bson.json.ExtendedJsonMinKeyConverter;
import org.bson.json.ExtendedJsonObjectIdConverter;
import org.bson.json.ExtendedJsonRegularExpressionConverter;
import org.bson.json.ExtendedJsonTimestampConverter;
import org.bson.json.ExtendedJsonUndefinedConverter;
import org.bson.json.JsonBooleanConverter;
import org.bson.json.JsonDoubleConverter;
import org.bson.json.JsonInt32Converter;
import org.bson.json.JsonJavaScriptConverter;
import org.bson.json.JsonMode;
import org.bson.json.JsonNullConverter;
import org.bson.json.JsonStringConverter;
import org.bson.json.JsonSymbolConverter;
import org.bson.json.LegacyExtendedJsonBinaryConverter;
import org.bson.json.LegacyExtendedJsonDateTimeConverter;
import org.bson.json.LegacyExtendedJsonRegularExpressionConverter;
import org.bson.json.RelaxedExtendedJsonDateTimeConverter;
import org.bson.json.RelaxedExtendedJsonDoubleConverter;
import org.bson.json.RelaxedExtendedJsonInt64Converter;
import org.bson.json.ShellBinaryConverter;
import org.bson.json.ShellDateTimeConverter;
import org.bson.json.ShellDecimal128Converter;
import org.bson.json.ShellInt64Converter;
import org.bson.json.ShellMaxKeyConverter;
import org.bson.json.ShellMinKeyConverter;
import org.bson.json.ShellObjectIdConverter;
import org.bson.json.ShellRegularExpressionConverter;
import org.bson.json.ShellTimestampConverter;
import org.bson.json.ShellUndefinedConverter;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public final class JsonWriterSettings
extends BsonWriterSettings {
    private static final JsonNullConverter JSON_NULL_CONVERTER = new JsonNullConverter();
    private static final JsonStringConverter JSON_STRING_CONVERTER = new JsonStringConverter();
    private static final JsonBooleanConverter JSON_BOOLEAN_CONVERTER = new JsonBooleanConverter();
    private static final JsonDoubleConverter JSON_DOUBLE_CONVERTER = new JsonDoubleConverter();
    private static final ExtendedJsonDoubleConverter EXTENDED_JSON_DOUBLE_CONVERTER = new ExtendedJsonDoubleConverter();
    private static final RelaxedExtendedJsonDoubleConverter RELAXED_EXTENDED_JSON_DOUBLE_CONVERTER = new RelaxedExtendedJsonDoubleConverter();
    private static final JsonInt32Converter JSON_INT_32_CONVERTER = new JsonInt32Converter();
    private static final ExtendedJsonInt32Converter EXTENDED_JSON_INT_32_CONVERTER = new ExtendedJsonInt32Converter();
    private static final JsonSymbolConverter JSON_SYMBOL_CONVERTER = new JsonSymbolConverter();
    private static final ExtendedJsonMinKeyConverter EXTENDED_JSON_MIN_KEY_CONVERTER = new ExtendedJsonMinKeyConverter();
    private static final ShellMinKeyConverter SHELL_MIN_KEY_CONVERTER = new ShellMinKeyConverter();
    private static final ExtendedJsonMaxKeyConverter EXTENDED_JSON_MAX_KEY_CONVERTER = new ExtendedJsonMaxKeyConverter();
    private static final ShellMaxKeyConverter SHELL_MAX_KEY_CONVERTER = new ShellMaxKeyConverter();
    private static final ExtendedJsonUndefinedConverter EXTENDED_JSON_UNDEFINED_CONVERTER = new ExtendedJsonUndefinedConverter();
    private static final ShellUndefinedConverter SHELL_UNDEFINED_CONVERTER = new ShellUndefinedConverter();
    private static final LegacyExtendedJsonDateTimeConverter LEGACY_EXTENDED_JSON_DATE_TIME_CONVERTER = new LegacyExtendedJsonDateTimeConverter();
    private static final ExtendedJsonDateTimeConverter EXTENDED_JSON_DATE_TIME_CONVERTER = new ExtendedJsonDateTimeConverter();
    private static final RelaxedExtendedJsonDateTimeConverter RELAXED_EXTENDED_JSON_DATE_TIME_CONVERTER = new RelaxedExtendedJsonDateTimeConverter();
    private static final ShellDateTimeConverter SHELL_DATE_TIME_CONVERTER = new ShellDateTimeConverter();
    private static final ExtendedJsonBinaryConverter EXTENDED_JSON_BINARY_CONVERTER = new ExtendedJsonBinaryConverter();
    private static final LegacyExtendedJsonBinaryConverter LEGACY_EXTENDED_JSON_BINARY_CONVERTER = new LegacyExtendedJsonBinaryConverter();
    private static final ShellBinaryConverter SHELL_BINARY_CONVERTER = new ShellBinaryConverter();
    private static final ExtendedJsonInt64Converter EXTENDED_JSON_INT_64_CONVERTER = new ExtendedJsonInt64Converter();
    private static final RelaxedExtendedJsonInt64Converter RELAXED_JSON_INT_64_CONVERTER = new RelaxedExtendedJsonInt64Converter();
    private static final ShellInt64Converter SHELL_INT_64_CONVERTER = new ShellInt64Converter();
    private static final ExtendedJsonDecimal128Converter EXTENDED_JSON_DECIMAL_128_CONVERTER = new ExtendedJsonDecimal128Converter();
    private static final ShellDecimal128Converter SHELL_DECIMAL_128_CONVERTER = new ShellDecimal128Converter();
    private static final ExtendedJsonObjectIdConverter EXTENDED_JSON_OBJECT_ID_CONVERTER = new ExtendedJsonObjectIdConverter();
    private static final ShellObjectIdConverter SHELL_OBJECT_ID_CONVERTER = new ShellObjectIdConverter();
    private static final ExtendedJsonTimestampConverter EXTENDED_JSON_TIMESTAMP_CONVERTER = new ExtendedJsonTimestampConverter();
    private static final ShellTimestampConverter SHELL_TIMESTAMP_CONVERTER = new ShellTimestampConverter();
    private static final ExtendedJsonRegularExpressionConverter EXTENDED_JSON_REGULAR_EXPRESSION_CONVERTER = new ExtendedJsonRegularExpressionConverter();
    private static final LegacyExtendedJsonRegularExpressionConverter LEGACY_EXTENDED_JSON_REGULAR_EXPRESSION_CONVERTER = new LegacyExtendedJsonRegularExpressionConverter();
    private static final ShellRegularExpressionConverter SHELL_REGULAR_EXPRESSION_CONVERTER = new ShellRegularExpressionConverter();
    private final boolean indent;
    private final String newLineCharacters;
    private final String indentCharacters;
    private final int maxLength;
    private final JsonMode outputMode;
    private final Converter<BsonNull> nullConverter;
    private final Converter<String> stringConverter;
    private final Converter<Long> dateTimeConverter;
    private final Converter<BsonBinary> binaryConverter;
    private final Converter<Boolean> booleanConverter;
    private final Converter<Double> doubleConverter;
    private final Converter<Integer> int32Converter;
    private final Converter<Long> int64Converter;
    private final Converter<Decimal128> decimal128Converter;
    private final Converter<ObjectId> objectIdConverter;
    private final Converter<BsonTimestamp> timestampConverter;
    private final Converter<BsonRegularExpression> regularExpressionConverter;
    private final Converter<String> symbolConverter;
    private final Converter<BsonUndefined> undefinedConverter;
    private final Converter<BsonMinKey> minKeyConverter;
    private final Converter<BsonMaxKey> maxKeyConverter;
    private final Converter<String> javaScriptConverter;

    public static Builder builder() {
        return new Builder();
    }

    private JsonWriterSettings(Builder builder) {
        this.indent = builder.indent;
        this.newLineCharacters = builder.newLineCharacters != null ? builder.newLineCharacters : System.getProperty("line.separator");
        this.indentCharacters = builder.indentCharacters;
        this.outputMode = builder.outputMode;
        this.maxLength = builder.maxLength;
        this.nullConverter = builder.nullConverter != null ? builder.nullConverter : JSON_NULL_CONVERTER;
        this.stringConverter = builder.stringConverter != null ? builder.stringConverter : JSON_STRING_CONVERTER;
        this.booleanConverter = builder.booleanConverter != null ? builder.booleanConverter : JSON_BOOLEAN_CONVERTER;
        this.doubleConverter = builder.doubleConverter != null ? builder.doubleConverter : (this.outputMode == JsonMode.EXTENDED ? EXTENDED_JSON_DOUBLE_CONVERTER : (this.outputMode == JsonMode.RELAXED ? RELAXED_EXTENDED_JSON_DOUBLE_CONVERTER : JSON_DOUBLE_CONVERTER));
        this.int32Converter = builder.int32Converter != null ? builder.int32Converter : (this.outputMode == JsonMode.EXTENDED ? EXTENDED_JSON_INT_32_CONVERTER : JSON_INT_32_CONVERTER);
        this.symbolConverter = builder.symbolConverter != null ? builder.symbolConverter : JSON_SYMBOL_CONVERTER;
        this.javaScriptConverter = builder.javaScriptConverter != null ? builder.javaScriptConverter : new JsonJavaScriptConverter();
        this.minKeyConverter = builder.minKeyConverter != null ? builder.minKeyConverter : (this.outputMode == JsonMode.STRICT || this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED ? EXTENDED_JSON_MIN_KEY_CONVERTER : SHELL_MIN_KEY_CONVERTER);
        this.maxKeyConverter = builder.maxKeyConverter != null ? builder.maxKeyConverter : (this.outputMode == JsonMode.STRICT || this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED ? EXTENDED_JSON_MAX_KEY_CONVERTER : SHELL_MAX_KEY_CONVERTER);
        this.undefinedConverter = builder.undefinedConverter != null ? builder.undefinedConverter : (this.outputMode == JsonMode.STRICT || this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED ? EXTENDED_JSON_UNDEFINED_CONVERTER : SHELL_UNDEFINED_CONVERTER);
        this.dateTimeConverter = builder.dateTimeConverter != null ? builder.dateTimeConverter : (this.outputMode == JsonMode.STRICT ? LEGACY_EXTENDED_JSON_DATE_TIME_CONVERTER : (this.outputMode == JsonMode.EXTENDED ? EXTENDED_JSON_DATE_TIME_CONVERTER : (this.outputMode == JsonMode.RELAXED ? RELAXED_EXTENDED_JSON_DATE_TIME_CONVERTER : SHELL_DATE_TIME_CONVERTER)));
        this.binaryConverter = builder.binaryConverter != null ? builder.binaryConverter : (this.outputMode == JsonMode.STRICT ? LEGACY_EXTENDED_JSON_BINARY_CONVERTER : (this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED ? EXTENDED_JSON_BINARY_CONVERTER : SHELL_BINARY_CONVERTER));
        this.int64Converter = builder.int64Converter != null ? builder.int64Converter : (this.outputMode == JsonMode.STRICT || this.outputMode == JsonMode.EXTENDED ? EXTENDED_JSON_INT_64_CONVERTER : (this.outputMode == JsonMode.RELAXED ? RELAXED_JSON_INT_64_CONVERTER : SHELL_INT_64_CONVERTER));
        this.decimal128Converter = builder.decimal128Converter != null ? builder.decimal128Converter : (this.outputMode == JsonMode.STRICT || this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED ? EXTENDED_JSON_DECIMAL_128_CONVERTER : SHELL_DECIMAL_128_CONVERTER);
        this.objectIdConverter = builder.objectIdConverter != null ? builder.objectIdConverter : (this.outputMode == JsonMode.STRICT || this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED ? EXTENDED_JSON_OBJECT_ID_CONVERTER : SHELL_OBJECT_ID_CONVERTER);
        this.timestampConverter = builder.timestampConverter != null ? builder.timestampConverter : (this.outputMode == JsonMode.STRICT || this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED ? EXTENDED_JSON_TIMESTAMP_CONVERTER : SHELL_TIMESTAMP_CONVERTER);
        this.regularExpressionConverter = builder.regularExpressionConverter != null ? builder.regularExpressionConverter : (this.outputMode == JsonMode.EXTENDED || this.outputMode == JsonMode.RELAXED ? EXTENDED_JSON_REGULAR_EXPRESSION_CONVERTER : (this.outputMode == JsonMode.STRICT ? LEGACY_EXTENDED_JSON_REGULAR_EXPRESSION_CONVERTER : SHELL_REGULAR_EXPRESSION_CONVERTER));
    }

    public boolean isIndent() {
        return this.indent;
    }

    public String getNewLineCharacters() {
        return this.newLineCharacters;
    }

    public String getIndentCharacters() {
        return this.indentCharacters;
    }

    public JsonMode getOutputMode() {
        return this.outputMode;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public Converter<BsonNull> getNullConverter() {
        return this.nullConverter;
    }

    public Converter<String> getStringConverter() {
        return this.stringConverter;
    }

    public Converter<BsonBinary> getBinaryConverter() {
        return this.binaryConverter;
    }

    public Converter<Boolean> getBooleanConverter() {
        return this.booleanConverter;
    }

    public Converter<Long> getDateTimeConverter() {
        return this.dateTimeConverter;
    }

    public Converter<Double> getDoubleConverter() {
        return this.doubleConverter;
    }

    public Converter<Integer> getInt32Converter() {
        return this.int32Converter;
    }

    public Converter<Long> getInt64Converter() {
        return this.int64Converter;
    }

    public Converter<Decimal128> getDecimal128Converter() {
        return this.decimal128Converter;
    }

    public Converter<ObjectId> getObjectIdConverter() {
        return this.objectIdConverter;
    }

    public Converter<BsonRegularExpression> getRegularExpressionConverter() {
        return this.regularExpressionConverter;
    }

    public Converter<BsonTimestamp> getTimestampConverter() {
        return this.timestampConverter;
    }

    public Converter<String> getSymbolConverter() {
        return this.symbolConverter;
    }

    public Converter<BsonMinKey> getMinKeyConverter() {
        return this.minKeyConverter;
    }

    public Converter<BsonMaxKey> getMaxKeyConverter() {
        return this.maxKeyConverter;
    }

    public Converter<BsonUndefined> getUndefinedConverter() {
        return this.undefinedConverter;
    }

    public Converter<String> getJavaScriptConverter() {
        return this.javaScriptConverter;
    }

    public static final class Builder {
        private boolean indent;
        private String newLineCharacters = System.getProperty("line.separator");
        private String indentCharacters = "  ";
        private JsonMode outputMode = JsonMode.RELAXED;
        private int maxLength;
        private Converter<BsonNull> nullConverter;
        private Converter<String> stringConverter;
        private Converter<Long> dateTimeConverter;
        private Converter<BsonBinary> binaryConverter;
        private Converter<Boolean> booleanConverter;
        private Converter<Double> doubleConverter;
        private Converter<Integer> int32Converter;
        private Converter<Long> int64Converter;
        private Converter<Decimal128> decimal128Converter;
        private Converter<ObjectId> objectIdConverter;
        private Converter<BsonTimestamp> timestampConverter;
        private Converter<BsonRegularExpression> regularExpressionConverter;
        private Converter<String> symbolConverter;
        private Converter<BsonUndefined> undefinedConverter;
        private Converter<BsonMinKey> minKeyConverter;
        private Converter<BsonMaxKey> maxKeyConverter;
        private Converter<String> javaScriptConverter;

        public JsonWriterSettings build() {
            return new JsonWriterSettings(this);
        }

        public Builder indent(boolean indent) {
            this.indent = indent;
            return this;
        }

        public Builder newLineCharacters(String newLineCharacters) {
            Assertions.notNull("newLineCharacters", newLineCharacters);
            this.newLineCharacters = newLineCharacters;
            return this;
        }

        public Builder indentCharacters(String indentCharacters) {
            Assertions.notNull("indentCharacters", indentCharacters);
            this.indentCharacters = indentCharacters;
            return this;
        }

        public Builder outputMode(JsonMode outputMode) {
            Assertions.notNull("outputMode", outputMode);
            this.outputMode = outputMode;
            return this;
        }

        public Builder maxLength(int maxLength) {
            Assertions.isTrueArgument("maxLength >= 0", maxLength >= 0);
            this.maxLength = maxLength;
            return this;
        }

        public Builder nullConverter(Converter<BsonNull> nullConverter) {
            this.nullConverter = nullConverter;
            return this;
        }

        public Builder stringConverter(Converter<String> stringConverter) {
            this.stringConverter = stringConverter;
            return this;
        }

        public Builder dateTimeConverter(Converter<Long> dateTimeConverter) {
            this.dateTimeConverter = dateTimeConverter;
            return this;
        }

        public Builder binaryConverter(Converter<BsonBinary> binaryConverter) {
            this.binaryConverter = binaryConverter;
            return this;
        }

        public Builder booleanConverter(Converter<Boolean> booleanConverter) {
            this.booleanConverter = booleanConverter;
            return this;
        }

        public Builder doubleConverter(Converter<Double> doubleConverter) {
            this.doubleConverter = doubleConverter;
            return this;
        }

        public Builder int32Converter(Converter<Integer> int32Converter) {
            this.int32Converter = int32Converter;
            return this;
        }

        public Builder int64Converter(Converter<Long> int64Converter) {
            this.int64Converter = int64Converter;
            return this;
        }

        public Builder decimal128Converter(Converter<Decimal128> decimal128Converter) {
            this.decimal128Converter = decimal128Converter;
            return this;
        }

        public Builder objectIdConverter(Converter<ObjectId> objectIdConverter) {
            this.objectIdConverter = objectIdConverter;
            return this;
        }

        public Builder timestampConverter(Converter<BsonTimestamp> timestampConverter) {
            this.timestampConverter = timestampConverter;
            return this;
        }

        public Builder regularExpressionConverter(Converter<BsonRegularExpression> regularExpressionConverter) {
            this.regularExpressionConverter = regularExpressionConverter;
            return this;
        }

        public Builder symbolConverter(Converter<String> symbolConverter) {
            this.symbolConverter = symbolConverter;
            return this;
        }

        public Builder minKeyConverter(Converter<BsonMinKey> minKeyConverter) {
            this.minKeyConverter = minKeyConverter;
            return this;
        }

        public Builder maxKeyConverter(Converter<BsonMaxKey> maxKeyConverter) {
            this.maxKeyConverter = maxKeyConverter;
            return this;
        }

        public Builder undefinedConverter(Converter<BsonUndefined> undefinedConverter) {
            this.undefinedConverter = undefinedConverter;
            return this;
        }

        public Builder javaScriptConverter(Converter<String> javaScriptConverter) {
            this.javaScriptConverter = javaScriptConverter;
            return this;
        }

        private Builder() {
        }
    }
}

