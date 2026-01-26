/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.Android;
import com.google.protobuf.ByteString;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionSchema;
import com.google.protobuf.FieldInfo;
import com.google.protobuf.FieldSet;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.IntArrayList;
import com.google.protobuf.Internal;
import com.google.protobuf.LazyFieldLite;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.LongArrayList;
import com.google.protobuf.MapFieldSchema;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Schema;
import com.google.protobuf.UnknownFieldSchema;
import com.google.protobuf.UnknownFieldSetLiteSchema;
import com.google.protobuf.UnsafeUtil;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

@CheckReturnValue
final class SchemaUtil {
    private static final Class<?> GENERATED_MESSAGE_CLASS = SchemaUtil.getGeneratedMessageClass();
    private static final UnknownFieldSchema<?, ?> UNKNOWN_FIELD_SET_FULL_SCHEMA = SchemaUtil.getUnknownFieldSetSchema();
    private static final UnknownFieldSchema<?, ?> UNKNOWN_FIELD_SET_LITE_SCHEMA = new UnknownFieldSetLiteSchema();
    private static final int DEFAULT_LOOK_UP_START_NUMBER = 40;

    private SchemaUtil() {
    }

    public static void requireGeneratedMessage(Class<?> messageType) {
        if (!(GeneratedMessageLite.class.isAssignableFrom(messageType) || Android.assumeLiteRuntime || GENERATED_MESSAGE_CLASS == null || GENERATED_MESSAGE_CLASS.isAssignableFrom(messageType))) {
            throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
        }
    }

    public static void writeDouble(int fieldNumber, double value, Writer writer) throws IOException {
        if (Double.doubleToRawLongBits(value) != 0L) {
            writer.writeDouble(fieldNumber, value);
        }
    }

    public static void writeFloat(int fieldNumber, float value, Writer writer) throws IOException {
        if (Float.floatToRawIntBits(value) != 0) {
            writer.writeFloat(fieldNumber, value);
        }
    }

    public static void writeInt64(int fieldNumber, long value, Writer writer) throws IOException {
        if (value != 0L) {
            writer.writeInt64(fieldNumber, value);
        }
    }

    public static void writeUInt64(int fieldNumber, long value, Writer writer) throws IOException {
        if (value != 0L) {
            writer.writeUInt64(fieldNumber, value);
        }
    }

    public static void writeSInt64(int fieldNumber, long value, Writer writer) throws IOException {
        if (value != 0L) {
            writer.writeSInt64(fieldNumber, value);
        }
    }

    public static void writeFixed64(int fieldNumber, long value, Writer writer) throws IOException {
        if (value != 0L) {
            writer.writeFixed64(fieldNumber, value);
        }
    }

    public static void writeSFixed64(int fieldNumber, long value, Writer writer) throws IOException {
        if (value != 0L) {
            writer.writeSFixed64(fieldNumber, value);
        }
    }

    public static void writeInt32(int fieldNumber, int value, Writer writer) throws IOException {
        if (value != 0) {
            writer.writeInt32(fieldNumber, value);
        }
    }

    public static void writeUInt32(int fieldNumber, int value, Writer writer) throws IOException {
        if (value != 0) {
            writer.writeUInt32(fieldNumber, value);
        }
    }

    public static void writeSInt32(int fieldNumber, int value, Writer writer) throws IOException {
        if (value != 0) {
            writer.writeSInt32(fieldNumber, value);
        }
    }

    public static void writeFixed32(int fieldNumber, int value, Writer writer) throws IOException {
        if (value != 0) {
            writer.writeFixed32(fieldNumber, value);
        }
    }

    public static void writeSFixed32(int fieldNumber, int value, Writer writer) throws IOException {
        if (value != 0) {
            writer.writeSFixed32(fieldNumber, value);
        }
    }

    public static void writeEnum(int fieldNumber, int value, Writer writer) throws IOException {
        if (value != 0) {
            writer.writeEnum(fieldNumber, value);
        }
    }

    public static void writeBool(int fieldNumber, boolean value, Writer writer) throws IOException {
        if (value) {
            writer.writeBool(fieldNumber, true);
        }
    }

    public static void writeString(int fieldNumber, Object value, Writer writer) throws IOException {
        if (value instanceof String) {
            SchemaUtil.writeStringInternal(fieldNumber, (String)value, writer);
        } else {
            SchemaUtil.writeBytes(fieldNumber, (ByteString)value, writer);
        }
    }

    private static void writeStringInternal(int fieldNumber, String value, Writer writer) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeString(fieldNumber, value);
        }
    }

    public static void writeBytes(int fieldNumber, ByteString value, Writer writer) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeBytes(fieldNumber, value);
        }
    }

    public static void writeMessage(int fieldNumber, Object value, Writer writer) throws IOException {
        if (value != null) {
            writer.writeMessage(fieldNumber, value);
        }
    }

    public static void writeDoubleList(int fieldNumber, List<Double> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeDoubleList(fieldNumber, value, packed);
        }
    }

    public static void writeFloatList(int fieldNumber, List<Float> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeFloatList(fieldNumber, value, packed);
        }
    }

    public static void writeInt64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeInt64List(fieldNumber, value, packed);
        }
    }

    public static void writeUInt64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeUInt64List(fieldNumber, value, packed);
        }
    }

    public static void writeSInt64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeSInt64List(fieldNumber, value, packed);
        }
    }

    public static void writeFixed64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeFixed64List(fieldNumber, value, packed);
        }
    }

    public static void writeSFixed64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeSFixed64List(fieldNumber, value, packed);
        }
    }

    public static void writeInt32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeInt32List(fieldNumber, value, packed);
        }
    }

    public static void writeUInt32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeUInt32List(fieldNumber, value, packed);
        }
    }

    public static void writeSInt32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeSInt32List(fieldNumber, value, packed);
        }
    }

    public static void writeFixed32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeFixed32List(fieldNumber, value, packed);
        }
    }

    public static void writeSFixed32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeSFixed32List(fieldNumber, value, packed);
        }
    }

    public static void writeEnumList(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeEnumList(fieldNumber, value, packed);
        }
    }

    public static void writeBoolList(int fieldNumber, List<Boolean> value, Writer writer, boolean packed) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeBoolList(fieldNumber, value, packed);
        }
    }

    public static void writeStringList(int fieldNumber, List<String> value, Writer writer) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeStringList(fieldNumber, value);
        }
    }

    public static void writeBytesList(int fieldNumber, List<ByteString> value, Writer writer) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeBytesList(fieldNumber, value);
        }
    }

    public static void writeMessageList(int fieldNumber, List<?> value, Writer writer) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeMessageList(fieldNumber, value);
        }
    }

    public static void writeMessageList(int fieldNumber, List<?> value, Writer writer, Schema<?> schema) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeMessageList(fieldNumber, value, schema);
        }
    }

    public static void writeLazyFieldList(int fieldNumber, List<?> value, Writer writer) throws IOException {
        if (value != null && !value.isEmpty()) {
            for (Object item : value) {
                ((LazyFieldLite)item).writeTo(writer, fieldNumber);
            }
        }
    }

    public static void writeGroupList(int fieldNumber, List<?> value, Writer writer) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeGroupList(fieldNumber, value);
        }
    }

    public static void writeGroupList(int fieldNumber, List<?> value, Writer writer, Schema<?> schema) throws IOException {
        if (value != null && !value.isEmpty()) {
            writer.writeGroupList(fieldNumber, value, schema);
        }
    }

    static int computeSizeInt64ListNoTag(List<Long> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        if (list instanceof LongArrayList) {
            LongArrayList primitiveList = (LongArrayList)list;
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeInt64SizeNoTag(primitiveList.getLong(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeInt64SizeNoTag(list.get(i));
            }
        }
        return size;
    }

    static int computeSizeInt64List(int fieldNumber, List<Long> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = SchemaUtil.computeSizeInt64ListNoTag(list);
        if (packed) {
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(size);
        }
        return size + list.size() * CodedOutputStream.computeTagSize(fieldNumber);
    }

    static int computeSizeUInt64ListNoTag(List<Long> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        if (list instanceof LongArrayList) {
            LongArrayList primitiveList = (LongArrayList)list;
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeUInt64SizeNoTag(primitiveList.getLong(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeUInt64SizeNoTag(list.get(i));
            }
        }
        return size;
    }

    static int computeSizeUInt64List(int fieldNumber, List<Long> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = SchemaUtil.computeSizeUInt64ListNoTag(list);
        if (packed) {
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(size);
        }
        return size + length * CodedOutputStream.computeTagSize(fieldNumber);
    }

    static int computeSizeSInt64ListNoTag(List<Long> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        if (list instanceof LongArrayList) {
            LongArrayList primitiveList = (LongArrayList)list;
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeSInt64SizeNoTag(primitiveList.getLong(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeSInt64SizeNoTag(list.get(i));
            }
        }
        return size;
    }

    static int computeSizeSInt64List(int fieldNumber, List<Long> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = SchemaUtil.computeSizeSInt64ListNoTag(list);
        if (packed) {
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(size);
        }
        return size + length * CodedOutputStream.computeTagSize(fieldNumber);
    }

    static int computeSizeEnumListNoTag(List<Integer> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        if (list instanceof IntArrayList) {
            IntArrayList primitiveList = (IntArrayList)list;
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeEnumSizeNoTag(primitiveList.getInt(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeEnumSizeNoTag(list.get(i));
            }
        }
        return size;
    }

    static int computeSizeEnumList(int fieldNumber, List<Integer> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = SchemaUtil.computeSizeEnumListNoTag(list);
        if (packed) {
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(size);
        }
        return size + length * CodedOutputStream.computeTagSize(fieldNumber);
    }

    static int computeSizeInt32ListNoTag(List<Integer> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        if (list instanceof IntArrayList) {
            IntArrayList primitiveList = (IntArrayList)list;
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeInt32SizeNoTag(primitiveList.getInt(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeInt32SizeNoTag(list.get(i));
            }
        }
        return size;
    }

    static int computeSizeInt32List(int fieldNumber, List<Integer> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = SchemaUtil.computeSizeInt32ListNoTag(list);
        if (packed) {
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(size);
        }
        return size + length * CodedOutputStream.computeTagSize(fieldNumber);
    }

    static int computeSizeUInt32ListNoTag(List<Integer> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        if (list instanceof IntArrayList) {
            IntArrayList primitiveList = (IntArrayList)list;
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeUInt32SizeNoTag(primitiveList.getInt(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeUInt32SizeNoTag(list.get(i));
            }
        }
        return size;
    }

    static int computeSizeUInt32List(int fieldNumber, List<Integer> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = SchemaUtil.computeSizeUInt32ListNoTag(list);
        if (packed) {
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(size);
        }
        return size + length * CodedOutputStream.computeTagSize(fieldNumber);
    }

    static int computeSizeSInt32ListNoTag(List<Integer> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        if (list instanceof IntArrayList) {
            IntArrayList primitiveList = (IntArrayList)list;
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeSInt32SizeNoTag(primitiveList.getInt(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                size += CodedOutputStream.computeSInt32SizeNoTag(list.get(i));
            }
        }
        return size;
    }

    static int computeSizeSInt32List(int fieldNumber, List<Integer> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = SchemaUtil.computeSizeSInt32ListNoTag(list);
        if (packed) {
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(size);
        }
        return size + length * CodedOutputStream.computeTagSize(fieldNumber);
    }

    static int computeSizeFixed32ListNoTag(List<?> list) {
        return list.size() * 4;
    }

    static int computeSizeFixed32List(int fieldNumber, List<?> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        if (packed) {
            int dataSize = length * 4;
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(dataSize);
        }
        return length * CodedOutputStream.computeFixed32Size(fieldNumber, 0);
    }

    static int computeSizeFixed64ListNoTag(List<?> list) {
        return list.size() * 8;
    }

    static int computeSizeFixed64List(int fieldNumber, List<?> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        if (packed) {
            int dataSize = length * 8;
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(dataSize);
        }
        return length * CodedOutputStream.computeFixed64Size(fieldNumber, 0L);
    }

    static int computeSizeBoolListNoTag(List<?> list) {
        return list.size();
    }

    static int computeSizeBoolList(int fieldNumber, List<?> list, boolean packed) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        if (packed) {
            return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(length);
        }
        return length * CodedOutputStream.computeBoolSize(fieldNumber, true);
    }

    static int computeSizeStringList(int fieldNumber, List<?> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = length * CodedOutputStream.computeTagSize(fieldNumber);
        if (list instanceof LazyStringList) {
            LazyStringList lazyList = (LazyStringList)list;
            for (int i = 0; i < length; ++i) {
                Object value = lazyList.getRaw(i);
                if (value instanceof ByteString) {
                    size += CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
                    continue;
                }
                size += CodedOutputStream.computeStringSizeNoTag((String)value);
            }
        } else {
            for (int i = 0; i < length; ++i) {
                Object value = list.get(i);
                if (value instanceof ByteString) {
                    size += CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
                    continue;
                }
                size += CodedOutputStream.computeStringSizeNoTag((String)value);
            }
        }
        return size;
    }

    static int computeSizeMessage(int fieldNumber, Object value, Schema<?> schema) {
        if (value instanceof LazyFieldLite) {
            return ((LazyFieldLite)value).computeSize(fieldNumber);
        }
        return SchemaUtil.computeMessageSize(fieldNumber, (AbstractMessageLite)value, schema);
    }

    static int computeMessageSize(int fieldNumber, AbstractMessageLite value, Schema schema) {
        return CodedOutputStream.computeTagSize(fieldNumber) + SchemaUtil.computeMessageSizeNoTag(value, schema);
    }

    static int computeMessageSizeNoTag(AbstractMessageLite value, Schema schema) {
        return CodedOutputStream.computeLengthDelimitedFieldSize(value.getSerializedSize(schema));
    }

    static int computeSizeMessageList(int fieldNumber, List<?> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = length * CodedOutputStream.computeTagSize(fieldNumber);
        for (int i = 0; i < length; ++i) {
            Object value = list.get(i);
            if (value instanceof LazyFieldLite) {
                size += ((LazyFieldLite)value).computeSizeNoTag();
                continue;
            }
            size += CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
        }
        return size;
    }

    static int computeSizeMessageList(int fieldNumber, List<?> list, Schema<?> schema) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = length * CodedOutputStream.computeTagSize(fieldNumber);
        for (int i = 0; i < length; ++i) {
            Object value = list.get(i);
            if (value instanceof LazyFieldLite) {
                size += ((LazyFieldLite)value).computeSizeNoTag();
                continue;
            }
            size += SchemaUtil.computeMessageSizeNoTag((AbstractMessageLite)value, schema);
        }
        return size;
    }

    static int computeSizeByteStringList(int fieldNumber, List<ByteString> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = length * CodedOutputStream.computeTagSize(fieldNumber);
        for (int i = 0; i < list.size(); ++i) {
            size += CodedOutputStream.computeBytesSizeNoTag(list.get(i));
        }
        return size;
    }

    @Deprecated
    static int computeGroupSizeNoTag(MessageLite value, Schema schema) {
        return ((AbstractMessageLite)value).getSerializedSize(schema);
    }

    @Deprecated
    static int computeGroupSize(int fieldNumber, MessageLite value, Schema schema) {
        return CodedOutputStream.computeTagSize(fieldNumber) * 2 + SchemaUtil.computeGroupSizeNoTag(value, schema);
    }

    static int computeSizeGroupList(int fieldNumber, List<MessageLite> list) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        for (int i = 0; i < length; ++i) {
            size += CodedOutputStream.computeGroupSize(fieldNumber, list.get(i));
        }
        return size;
    }

    static int computeSizeGroupList(int fieldNumber, List<MessageLite> list, Schema<?> schema) {
        int length = list.size();
        if (length == 0) {
            return 0;
        }
        int size = 0;
        for (int i = 0; i < length; ++i) {
            size += SchemaUtil.computeGroupSize(fieldNumber, list.get(i), schema);
        }
        return size;
    }

    public static boolean shouldUseTableSwitch(FieldInfo[] fields) {
        if (fields.length == 0) {
            return false;
        }
        int lo = fields[0].getFieldNumber();
        int hi = fields[fields.length - 1].getFieldNumber();
        return SchemaUtil.shouldUseTableSwitch(lo, hi, fields.length);
    }

    public static boolean shouldUseTableSwitch(int lo, int hi, int numFields) {
        if (hi < 40) {
            return true;
        }
        long tableSpaceCost = (long)hi - (long)lo + 1L;
        long tableTimeCost = 3L;
        long lookupSpaceCost = 3L + 2L * (long)numFields;
        long lookupTimeCost = 3L + (long)numFields;
        return tableSpaceCost + 3L * tableTimeCost <= lookupSpaceCost + 3L * lookupTimeCost;
    }

    public static UnknownFieldSchema<?, ?> unknownFieldSetFullSchema() {
        return UNKNOWN_FIELD_SET_FULL_SCHEMA;
    }

    public static UnknownFieldSchema<?, ?> unknownFieldSetLiteSchema() {
        return UNKNOWN_FIELD_SET_LITE_SCHEMA;
    }

    private static UnknownFieldSchema<?, ?> getUnknownFieldSetSchema() {
        try {
            Class<?> clz = SchemaUtil.getUnknownFieldSetSchemaClass();
            if (clz == null) {
                return null;
            }
            return (UnknownFieldSchema)clz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static Class<?> getGeneratedMessageClass() {
        if (Android.assumeLiteRuntime) {
            return null;
        }
        try {
            return Class.forName("com.google.protobuf.GeneratedMessage");
        }
        catch (Throwable e) {
            return null;
        }
    }

    private static Class<?> getUnknownFieldSetSchemaClass() {
        if (Android.assumeLiteRuntime) {
            return null;
        }
        try {
            return Class.forName("com.google.protobuf.UnknownFieldSetSchema");
        }
        catch (Throwable e) {
            return null;
        }
    }

    static Object getMapDefaultEntry(Class<?> clazz, String name) {
        try {
            Class<?> holder = Class.forName(clazz.getName() + "$" + SchemaUtil.toCamelCase(name, true) + "DefaultEntryHolder");
            Field[] fields = holder.getDeclaredFields();
            if (fields.length != 1) {
                throw new IllegalStateException("Unable to look up map field default entry holder class for " + name + " in " + clazz.getName());
            }
            return UnsafeUtil.getStaticObject(fields[0]);
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    static String toCamelCase(String name, boolean capNext) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if ('a' <= c && c <= 'z') {
                if (capNext) {
                    sb.append((char)(c + -32));
                } else {
                    sb.append(c);
                }
                capNext = false;
                continue;
            }
            if ('A' <= c && c <= 'Z') {
                if (i == 0 && !capNext) {
                    sb.append((char)(c - -32));
                } else {
                    sb.append(c);
                }
                capNext = false;
                continue;
            }
            if ('0' <= c && c <= '9') {
                sb.append(c);
                capNext = true;
                continue;
            }
            capNext = true;
        }
        return sb.toString();
    }

    static boolean safeEquals(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    static <T> void mergeMap(MapFieldSchema mapFieldSchema, T message, T o, long offset) {
        Object merged = mapFieldSchema.mergeFrom(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(o, offset));
        UnsafeUtil.putObject(message, offset, merged);
    }

    static <T, FT extends FieldSet.FieldDescriptorLite<FT>> void mergeExtensions(ExtensionSchema<FT> schema, T message, T other) {
        FieldSet<FT> otherExtensions = schema.getExtensions(other);
        if (!otherExtensions.isEmpty()) {
            FieldSet<FT> messageExtensions = schema.getMutableExtensions(message);
            messageExtensions.mergeFrom(otherExtensions);
        }
    }

    static <T, UT, UB> void mergeUnknownFields(UnknownFieldSchema<UT, UB> schema, T message, T other) {
        UT messageUnknowns = schema.getFromMessage(message);
        UT otherUnknowns = schema.getFromMessage(other);
        UT merged = schema.merge(messageUnknowns, otherUnknowns);
        schema.setToMessage(message, merged);
    }

    @CanIgnoreReturnValue
    static <UT, UB> UB filterUnknownEnumList(Object containerMessage, int number, List<Integer> enumList, Internal.EnumLiteMap<?> enumMap, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
        if (enumMap == null) {
            return unknownFields;
        }
        if (enumList instanceof RandomAccess) {
            int writePos = 0;
            int size = enumList.size();
            for (int readPos = 0; readPos < size; ++readPos) {
                int enumValue = enumList.get(readPos);
                if (enumMap.findValueByNumber(enumValue) != null) {
                    if (readPos != writePos) {
                        enumList.set(writePos, enumValue);
                    }
                    ++writePos;
                    continue;
                }
                unknownFields = SchemaUtil.storeUnknownEnum(containerMessage, number, enumValue, unknownFields, unknownFieldSchema);
            }
            if (writePos != size) {
                enumList.subList(writePos, size).clear();
            }
        } else {
            Iterator<Integer> it = enumList.iterator();
            while (it.hasNext()) {
                int enumValue = it.next();
                if (enumMap.findValueByNumber(enumValue) != null) continue;
                unknownFields = SchemaUtil.storeUnknownEnum(containerMessage, number, enumValue, unknownFields, unknownFieldSchema);
                it.remove();
            }
        }
        return unknownFields;
    }

    @CanIgnoreReturnValue
    static <UT, UB> UB filterUnknownEnumList(Object containerMessage, int number, List<Integer> enumList, Internal.EnumVerifier enumVerifier, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
        if (enumVerifier == null) {
            return unknownFields;
        }
        if (enumList instanceof RandomAccess) {
            int writePos = 0;
            int size = enumList.size();
            for (int readPos = 0; readPos < size; ++readPos) {
                int enumValue = enumList.get(readPos);
                if (enumVerifier.isInRange(enumValue)) {
                    if (readPos != writePos) {
                        enumList.set(writePos, enumValue);
                    }
                    ++writePos;
                    continue;
                }
                unknownFields = SchemaUtil.storeUnknownEnum(containerMessage, number, enumValue, unknownFields, unknownFieldSchema);
            }
            if (writePos != size) {
                enumList.subList(writePos, size).clear();
            }
        } else {
            Iterator<Integer> it = enumList.iterator();
            while (it.hasNext()) {
                int enumValue = it.next();
                if (enumVerifier.isInRange(enumValue)) continue;
                unknownFields = SchemaUtil.storeUnknownEnum(containerMessage, number, enumValue, unknownFields, unknownFieldSchema);
                it.remove();
            }
        }
        return unknownFields;
    }

    @CanIgnoreReturnValue
    static <UT, UB> UB storeUnknownEnum(Object containerMessage, int number, int enumValue, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
        if (unknownFields == null) {
            unknownFields = unknownFieldSchema.getBuilderFromMessage(containerMessage);
        }
        unknownFieldSchema.addVarint(unknownFields, number, enumValue);
        return unknownFields;
    }
}

