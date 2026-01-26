/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.LazyField;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.SmallSortedMap;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class FieldSet<T extends FieldDescriptorLite<T>> {
    private final SmallSortedMap<T, Object> fields;
    private boolean isImmutable;
    private boolean hasLazyField;
    private static final FieldSet<?> DEFAULT_INSTANCE = new FieldSet(true);

    private FieldSet() {
        this.fields = SmallSortedMap.newFieldMap();
    }

    private FieldSet(boolean dummy) {
        this(SmallSortedMap.newFieldMap());
        this.makeImmutable();
    }

    private FieldSet(SmallSortedMap<T, Object> fields) {
        this.fields = fields;
        this.makeImmutable();
    }

    public static <T extends FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
        return new FieldSet<T>();
    }

    public static <T extends FieldDescriptorLite<T>> FieldSet<T> emptySet() {
        return DEFAULT_INSTANCE;
    }

    public static <T extends FieldDescriptorLite<T>> Builder<T> newBuilder() {
        return new Builder();
    }

    boolean isEmpty() {
        return this.fields.isEmpty();
    }

    public void makeImmutable() {
        Object value;
        if (this.isImmutable) {
            return;
        }
        int n = this.fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            value = entry.getValue();
            if (!(value instanceof GeneratedMessageLite)) continue;
            ((GeneratedMessageLite)value).makeImmutable();
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            value = entry.getValue();
            if (!(value instanceof GeneratedMessageLite)) continue;
            ((GeneratedMessageLite)value).makeImmutable();
        }
        this.fields.makeImmutable();
        this.isImmutable = true;
    }

    public boolean isImmutable() {
        return this.isImmutable;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldSet)) {
            return false;
        }
        FieldSet other = (FieldSet)o;
        return this.fields.equals(other.fields);
    }

    public int hashCode() {
        return this.fields.hashCode();
    }

    public FieldSet<T> clone() {
        FieldSet<FieldDescriptorLite> clone = FieldSet.newFieldSet();
        int n = this.fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            clone.setField((FieldDescriptorLite)entry.getKey(), entry.getValue());
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            clone.setField((FieldDescriptorLite)entry.getKey(), entry.getValue());
        }
        clone.hasLazyField = this.hasLazyField;
        return clone;
    }

    public void clear() {
        this.fields.clear();
        this.hasLazyField = false;
    }

    public Map<T, Object> getAllFields() {
        if (this.hasLazyField) {
            SmallSortedMap<T, Object> result = FieldSet.cloneAllFieldsMap(this.fields, false, true);
            if (this.fields.isImmutable()) {
                result.makeImmutable();
            }
            return result;
        }
        return this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields);
    }

    private static <T extends FieldDescriptorLite<T>> SmallSortedMap<T, Object> cloneAllFieldsMap(SmallSortedMap<T, Object> fields, boolean copyList, boolean resolveLazyFields) {
        SmallSortedMap result = SmallSortedMap.newFieldMap();
        int n = fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            FieldSet.cloneFieldEntry(result, fields.getArrayEntryAt(i), copyList, resolveLazyFields);
        }
        for (Map.Entry<T, Object> entry : fields.getOverflowEntries()) {
            FieldSet.cloneFieldEntry(result, entry, copyList, resolveLazyFields);
        }
        return result;
    }

    private static <T extends FieldDescriptorLite<T>> void cloneFieldEntry(Map<T, Object> map, Map.Entry<T, Object> entry, boolean copyList, boolean resolveLazyFields) {
        FieldDescriptorLite key = (FieldDescriptorLite)entry.getKey();
        Object value = entry.getValue();
        if (resolveLazyFields && value instanceof LazyField) {
            map.put(key, ((LazyField)value).getValue());
        } else if (copyList && value instanceof List) {
            map.put(key, new ArrayList((List)value));
        } else {
            map.put(key, value);
        }
    }

    public Iterator<Map.Entry<T, Object>> iterator() {
        if (this.isEmpty()) {
            return Collections.emptyIterator();
        }
        if (this.hasLazyField) {
            return new LazyField.LazyIterator(this.fields.entrySet().iterator());
        }
        return this.fields.entrySet().iterator();
    }

    Iterator<Map.Entry<T, Object>> descendingIterator() {
        if (this.isEmpty()) {
            return Collections.emptyIterator();
        }
        if (this.hasLazyField) {
            return new LazyField.LazyIterator(this.fields.descendingEntrySet().iterator());
        }
        return this.fields.descendingEntrySet().iterator();
    }

    public boolean hasField(T descriptor) {
        if (descriptor.isRepeated()) {
            throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
        }
        return this.fields.get(descriptor) != null;
    }

    public Object getField(T descriptor) {
        Object o = this.fields.get(descriptor);
        if (o instanceof LazyField) {
            return ((LazyField)o).getValue();
        }
        return o;
    }

    boolean lazyFieldCorrupted(T descriptor) {
        Object o = this.fields.get(descriptor);
        return o instanceof LazyField && ((LazyField)o).isCorrupted();
    }

    public void setField(T descriptor, Object value) {
        if (descriptor.isRepeated()) {
            if (!(value instanceof List)) {
                throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
            List list = value;
            int listSize = list.size();
            ArrayList newList = new ArrayList(listSize);
            for (int i = 0; i < listSize; ++i) {
                Object element = list.get(i);
                this.verifyType(descriptor, element);
                newList.add(element);
            }
            value = newList;
        } else {
            this.verifyType(descriptor, value);
        }
        if (value instanceof LazyField) {
            this.hasLazyField = true;
        }
        this.fields.put(descriptor, (Object)value);
    }

    public void clearField(T descriptor) {
        this.fields.remove(descriptor);
        if (this.fields.isEmpty()) {
            this.hasLazyField = false;
        }
    }

    public int getRepeatedFieldCount(T descriptor) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object value = this.getField(descriptor);
        if (value == null) {
            return 0;
        }
        return ((List)value).size();
    }

    public Object getRepeatedField(T descriptor, int index) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object value = this.getField(descriptor);
        if (value == null) {
            throw new IndexOutOfBoundsException();
        }
        return ((List)value).get(index);
    }

    public void setRepeatedField(T descriptor, int index, Object value) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object list = this.getField(descriptor);
        if (list == null) {
            throw new IndexOutOfBoundsException();
        }
        this.verifyType(descriptor, value);
        ((List)list).set(index, value);
    }

    public void addRepeatedField(T descriptor, Object value) {
        ArrayList<Object> list;
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
        }
        this.verifyType(descriptor, value);
        Object existingValue = this.getField(descriptor);
        if (existingValue == null) {
            list = new ArrayList<Object>();
            this.fields.put(descriptor, (Object)list);
        } else {
            list = (ArrayList<Object>)existingValue;
        }
        list.add(value);
    }

    private void verifyType(T descriptor, Object value) {
        if (!FieldSet.isValidType(descriptor.getLiteType(), value)) {
            throw new IllegalArgumentException(String.format("Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n", new Object[]{descriptor.getNumber(), descriptor.getLiteType().getJavaType(), value.getClass().getName()}));
        }
    }

    private static boolean isValidType(WireFormat.FieldType type, Object value) {
        Internal.checkNotNull(value);
        switch (type.getJavaType()) {
            case INT: {
                return value instanceof Integer;
            }
            case LONG: {
                return value instanceof Long;
            }
            case FLOAT: {
                return value instanceof Float;
            }
            case DOUBLE: {
                return value instanceof Double;
            }
            case BOOLEAN: {
                return value instanceof Boolean;
            }
            case STRING: {
                return value instanceof String;
            }
            case BYTE_STRING: {
                return value instanceof ByteString || value instanceof byte[];
            }
            case ENUM: {
                return value instanceof Integer || value instanceof Internal.EnumLite;
            }
            case MESSAGE: {
                return value instanceof MessageLite || value instanceof LazyField;
            }
        }
        return false;
    }

    public boolean isInitialized() {
        int n = this.fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            if (FieldSet.isInitialized(this.fields.getArrayEntryAt(i))) continue;
            return false;
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            if (FieldSet.isInitialized(entry)) continue;
            return false;
        }
        return true;
    }

    private static <T extends FieldDescriptorLite<T>> boolean isInitialized(Map.Entry<T, Object> entry) {
        FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            if (descriptor.isRepeated()) {
                List list = (List)entry.getValue();
                int listSize = list.size();
                for (int i = 0; i < listSize; ++i) {
                    Object element = list.get(i);
                    if (FieldSet.isMessageFieldValueInitialized(element)) continue;
                    return false;
                }
            } else {
                return FieldSet.isMessageFieldValueInitialized(entry.getValue());
            }
        }
        return true;
    }

    private static boolean isMessageFieldValueInitialized(Object value) {
        if (value instanceof MessageLiteOrBuilder) {
            return ((MessageLiteOrBuilder)value).isInitialized();
        }
        if (value instanceof LazyField) {
            return true;
        }
        throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
    }

    static int getWireFormatForFieldType(WireFormat.FieldType type, boolean isPacked) {
        if (isPacked) {
            return 2;
        }
        return type.getWireType();
    }

    public void mergeFrom(FieldSet<T> other) {
        int n = other.fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            this.mergeFromField(other.fields.getArrayEntryAt(i));
        }
        for (Map.Entry<T, Object> entry : other.fields.getOverflowEntries()) {
            this.mergeFromField(entry);
        }
    }

    private static Object cloneIfMutable(Object value) {
        if (value instanceof byte[]) {
            byte[] bytes = (byte[])value;
            byte[] copy = new byte[bytes.length];
            System.arraycopy(bytes, 0, copy, 0, bytes.length);
            return copy;
        }
        return value;
    }

    private void mergeFromField(Map.Entry<T, Object> entry) {
        FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
        Object otherValue = entry.getValue();
        boolean isLazyField = otherValue instanceof LazyField;
        if (descriptor.isRepeated()) {
            if (isLazyField) {
                throw new IllegalStateException("Lazy fields can not be repeated");
            }
            ArrayList value = this.getField(descriptor);
            List otherList = (List)otherValue;
            int otherListSize = otherList.size();
            if (value == null) {
                value = new ArrayList(otherListSize);
            }
            List list = value;
            for (int i = 0; i < otherListSize; ++i) {
                Object element = otherList.get(i);
                list.add(FieldSet.cloneIfMutable(element));
            }
            this.fields.put((T)descriptor, (Object)value);
        } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            Object value = this.getField(descriptor);
            if (value == null) {
                this.fields.put((T)descriptor, FieldSet.cloneIfMutable(otherValue));
                if (isLazyField) {
                    this.hasLazyField = true;
                }
            } else {
                if (otherValue instanceof LazyField) {
                    otherValue = ((LazyField)otherValue).getValue();
                }
                if (descriptor.internalMessageIsImmutable(value)) {
                    MessageLite.Builder builder = ((MessageLite)value).toBuilder();
                    descriptor.internalMergeFrom(builder, otherValue);
                    this.fields.put((T)descriptor, (Object)builder.build());
                } else {
                    descriptor.internalMergeFrom(value, otherValue);
                }
            }
        } else {
            if (isLazyField) {
                throw new IllegalStateException("Lazy fields must be message-valued");
            }
            this.fields.put((T)descriptor, FieldSet.cloneIfMutable(otherValue));
        }
    }

    public static Object readPrimitiveField(CodedInputStream input, WireFormat.FieldType type, boolean checkUtf8) throws IOException {
        if (checkUtf8) {
            return input.readPrimitiveField(type, WireFormat.Utf8Validation.STRICT);
        }
        return input.readPrimitiveField(type, WireFormat.Utf8Validation.LOOSE);
    }

    public void writeTo(CodedOutputStream output) throws IOException {
        int n = this.fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            FieldSet.writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            FieldSet.writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
        }
    }

    public void writeMessageSetTo(CodedOutputStream output) throws IOException {
        int n = this.fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            this.writeMessageSetTo(this.fields.getArrayEntryAt(i), output);
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            this.writeMessageSetTo(entry, output);
        }
    }

    private void writeMessageSetTo(Map.Entry<T, Object> entry, CodedOutputStream output) throws IOException {
        FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
            Object value = entry.getValue();
            if (value instanceof LazyField) {
                ByteString valueBytes = ((LazyField)value).toByteString();
                output.writeRawMessageSetExtension(((FieldDescriptorLite)entry.getKey()).getNumber(), valueBytes);
            } else {
                output.writeMessageSetExtension(((FieldDescriptorLite)entry.getKey()).getNumber(), (MessageLite)value);
            }
        } else {
            FieldSet.writeField(descriptor, entry.getValue(), output);
        }
    }

    static void writeElement(CodedOutputStream output, WireFormat.FieldType type, int number, Object value) throws IOException {
        if (type == WireFormat.FieldType.GROUP) {
            output.writeGroup(number, (MessageLite)value);
        } else {
            output.writeTag(number, FieldSet.getWireFormatForFieldType(type, false));
            FieldSet.writeElementNoTag(output, type, value);
        }
    }

    static void writeElementNoTag(CodedOutputStream output, WireFormat.FieldType type, Object value) throws IOException {
        switch (type) {
            case DOUBLE: {
                output.writeDoubleNoTag((Double)value);
                break;
            }
            case FLOAT: {
                output.writeFloatNoTag(((Float)value).floatValue());
                break;
            }
            case INT64: {
                output.writeInt64NoTag((Long)value);
                break;
            }
            case UINT64: {
                output.writeUInt64NoTag((Long)value);
                break;
            }
            case INT32: {
                output.writeInt32NoTag((Integer)value);
                break;
            }
            case FIXED64: {
                output.writeFixed64NoTag((Long)value);
                break;
            }
            case FIXED32: {
                output.writeFixed32NoTag((Integer)value);
                break;
            }
            case BOOL: {
                output.writeBoolNoTag((Boolean)value);
                break;
            }
            case GROUP: {
                output.writeGroupNoTag((MessageLite)value);
                break;
            }
            case MESSAGE: {
                output.writeMessageNoTag((MessageLite)value);
                break;
            }
            case STRING: {
                if (value instanceof ByteString) {
                    output.writeBytesNoTag((ByteString)value);
                    break;
                }
                output.writeStringNoTag((String)value);
                break;
            }
            case BYTES: {
                if (value instanceof ByteString) {
                    output.writeBytesNoTag((ByteString)value);
                    break;
                }
                output.writeByteArrayNoTag((byte[])value);
                break;
            }
            case UINT32: {
                output.writeUInt32NoTag((Integer)value);
                break;
            }
            case SFIXED32: {
                output.writeSFixed32NoTag((Integer)value);
                break;
            }
            case SFIXED64: {
                output.writeSFixed64NoTag((Long)value);
                break;
            }
            case SINT32: {
                output.writeSInt32NoTag((Integer)value);
                break;
            }
            case SINT64: {
                output.writeSInt64NoTag((Long)value);
                break;
            }
            case ENUM: {
                if (value instanceof Internal.EnumLite) {
                    output.writeEnumNoTag(((Internal.EnumLite)value).getNumber());
                    break;
                }
                output.writeEnumNoTag((Integer)value);
            }
        }
    }

    public static void writeField(FieldDescriptorLite<?> descriptor, Object value, CodedOutputStream output) throws IOException {
        WireFormat.FieldType type = descriptor.getLiteType();
        int number = descriptor.getNumber();
        if (descriptor.isRepeated()) {
            List valueList = (List)value;
            int valueListSize = valueList.size();
            if (descriptor.isPacked()) {
                Object element;
                int i;
                if (valueList.isEmpty()) {
                    return;
                }
                output.writeTag(number, 2);
                int dataSize = 0;
                for (i = 0; i < valueListSize; ++i) {
                    element = valueList.get(i);
                    dataSize += FieldSet.computeElementSizeNoTag(type, element);
                }
                output.writeUInt32NoTag(dataSize);
                for (i = 0; i < valueListSize; ++i) {
                    element = valueList.get(i);
                    FieldSet.writeElementNoTag(output, type, element);
                }
            } else {
                for (int i = 0; i < valueListSize; ++i) {
                    Object element = valueList.get(i);
                    FieldSet.writeElement(output, type, number, element);
                }
            }
        } else if (value instanceof LazyField) {
            FieldSet.writeElement(output, type, number, ((LazyField)value).getValue());
        } else {
            FieldSet.writeElement(output, type, number, value);
        }
    }

    public int getSerializedSize() {
        int size = 0;
        int n = this.fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            size += FieldSet.computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue());
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            size += FieldSet.computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue());
        }
        return size;
    }

    public int getMessageSetSerializedSize() {
        int size = 0;
        int n = this.fields.getNumArrayEntries();
        for (int i = 0; i < n; ++i) {
            size += this.getMessageSetSerializedSize(this.fields.getArrayEntryAt(i));
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            size += this.getMessageSetSerializedSize(entry);
        }
        return size;
    }

    private int getMessageSetSerializedSize(Map.Entry<T, Object> entry) {
        FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
        Object value = entry.getValue();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
            if (value instanceof LazyField) {
                return ((LazyField)value).computeMessageSetExtensionSize(((FieldDescriptorLite)entry.getKey()).getNumber());
            }
            return CodedOutputStream.computeMessageSetExtensionSize(((FieldDescriptorLite)entry.getKey()).getNumber(), (MessageLite)value);
        }
        return FieldSet.computeFieldSize(descriptor, value);
    }

    static int computeElementSize(WireFormat.FieldType type, int number, Object value) {
        int tagSize = CodedOutputStream.computeTagSize(number);
        if (type == WireFormat.FieldType.GROUP) {
            tagSize *= 2;
        }
        return tagSize + FieldSet.computeElementSizeNoTag(type, value);
    }

    static int computeElementSizeNoTag(WireFormat.FieldType type, Object value) {
        switch (type) {
            case DOUBLE: {
                return CodedOutputStream.computeDoubleSizeNoTag((Double)value);
            }
            case FLOAT: {
                return CodedOutputStream.computeFloatSizeNoTag(((Float)value).floatValue());
            }
            case INT64: {
                return CodedOutputStream.computeInt64SizeNoTag((Long)value);
            }
            case UINT64: {
                return CodedOutputStream.computeUInt64SizeNoTag((Long)value);
            }
            case INT32: {
                return CodedOutputStream.computeInt32SizeNoTag((Integer)value);
            }
            case FIXED64: {
                return CodedOutputStream.computeFixed64SizeNoTag((Long)value);
            }
            case FIXED32: {
                return CodedOutputStream.computeFixed32SizeNoTag((Integer)value);
            }
            case BOOL: {
                return CodedOutputStream.computeBoolSizeNoTag((Boolean)value);
            }
            case GROUP: {
                return ((MessageLite)value).getSerializedSize();
            }
            case BYTES: {
                if (value instanceof ByteString) {
                    return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
                }
                return CodedOutputStream.computeByteArraySizeNoTag((byte[])value);
            }
            case STRING: {
                if (value instanceof ByteString) {
                    return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
                }
                return CodedOutputStream.computeStringSizeNoTag((String)value);
            }
            case UINT32: {
                return CodedOutputStream.computeUInt32SizeNoTag((Integer)value);
            }
            case SFIXED32: {
                return CodedOutputStream.computeSFixed32SizeNoTag((Integer)value);
            }
            case SFIXED64: {
                return CodedOutputStream.computeSFixed64SizeNoTag((Long)value);
            }
            case SINT32: {
                return CodedOutputStream.computeSInt32SizeNoTag((Integer)value);
            }
            case SINT64: {
                return CodedOutputStream.computeSInt64SizeNoTag((Long)value);
            }
            case MESSAGE: {
                if (value instanceof LazyField) {
                    return ((LazyField)value).computeSizeNoTag();
                }
                return CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
            }
            case ENUM: {
                if (value instanceof Internal.EnumLite) {
                    return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite)value).getNumber());
                }
                return CodedOutputStream.computeEnumSizeNoTag((Integer)value);
            }
        }
        throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
    }

    public static int computeFieldSize(FieldDescriptorLite<?> descriptor, Object value) {
        WireFormat.FieldType type = descriptor.getLiteType();
        int number = descriptor.getNumber();
        if (descriptor.isRepeated()) {
            List valueList = (List)value;
            int valueListSize = valueList.size();
            if (descriptor.isPacked()) {
                if (valueList.isEmpty()) {
                    return 0;
                }
                int dataSize = 0;
                for (int i = 0; i < valueListSize; ++i) {
                    Object element = valueList.get(i);
                    dataSize += FieldSet.computeElementSizeNoTag(type, element);
                }
                return dataSize + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(dataSize);
            }
            int size = 0;
            for (int i = 0; i < valueListSize; ++i) {
                Object element = valueList.get(i);
                size += FieldSet.computeElementSize(type, number, element);
            }
            return size;
        }
        return FieldSet.computeElementSize(type, number, value);
    }

    static final class Builder<T extends FieldDescriptorLite<T>> {
        private SmallSortedMap<T, Object> fields;
        private boolean hasLazyField;
        private boolean isMutable;
        private boolean hasNestedBuilders;

        private Builder() {
            this(SmallSortedMap.newFieldMap());
        }

        private Builder(SmallSortedMap<T, Object> fields) {
            this.fields = fields;
            this.isMutable = true;
        }

        public FieldSet<T> build() {
            return this.buildImpl(false);
        }

        public FieldSet<T> buildPartial() {
            return this.buildImpl(true);
        }

        private FieldSet<T> buildImpl(boolean partial) {
            if (this.fields.isEmpty()) {
                return FieldSet.emptySet();
            }
            this.isMutable = false;
            SmallSortedMap fieldsForBuild = this.fields;
            if (this.hasNestedBuilders) {
                fieldsForBuild = FieldSet.cloneAllFieldsMap(this.fields, false, false);
                Builder.replaceBuilders(fieldsForBuild, partial);
            }
            FieldSet fieldSet = new FieldSet(fieldsForBuild);
            fieldSet.hasLazyField = this.hasLazyField;
            return fieldSet;
        }

        private static <T extends FieldDescriptorLite<T>> void replaceBuilders(SmallSortedMap<T, Object> fieldMap, boolean partial) {
            int n = fieldMap.getNumArrayEntries();
            for (int i = 0; i < n; ++i) {
                Builder.replaceBuilders(fieldMap.getArrayEntryAt(i), partial);
            }
            for (Map.Entry<T, Object> entry : fieldMap.getOverflowEntries()) {
                Builder.replaceBuilders(entry, partial);
            }
        }

        private static <T extends FieldDescriptorLite<T>> void replaceBuilders(Map.Entry<T, Object> entry, boolean partial) {
            entry.setValue(Builder.replaceBuilders((FieldDescriptorLite)entry.getKey(), entry.getValue(), partial));
        }

        private static <T extends FieldDescriptorLite<T>> Object replaceBuilders(T descriptor, Object value, boolean partial) {
            if (value == null) {
                return value;
            }
            if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
                if (descriptor.isRepeated()) {
                    if (!(value instanceof List)) {
                        throw new IllegalStateException("Repeated field should contains a List but actually contains type: " + value.getClass());
                    }
                    ArrayList<Object> list = (ArrayList<Object>)value;
                    for (int i = 0; i < list.size(); ++i) {
                        Object oldElement = list.get(i);
                        Object newElement = Builder.replaceBuilder(oldElement, partial);
                        if (newElement == oldElement) continue;
                        if (list == value) {
                            list = new ArrayList<Object>(list);
                        }
                        list.set(i, newElement);
                    }
                    return list;
                }
                return Builder.replaceBuilder(value, partial);
            }
            return value;
        }

        private static Object replaceBuilder(Object value, boolean partial) {
            if (!(value instanceof MessageLite.Builder)) {
                return value;
            }
            MessageLite.Builder builder = (MessageLite.Builder)value;
            if (partial) {
                return builder.buildPartial();
            }
            return builder.build();
        }

        public static <T extends FieldDescriptorLite<T>> Builder<T> fromFieldSet(FieldSet<T> fieldSet) {
            Builder<T> builder = new Builder<T>(FieldSet.cloneAllFieldsMap(((FieldSet)fieldSet).fields, true, false));
            builder.hasLazyField = ((FieldSet)fieldSet).hasLazyField;
            return builder;
        }

        public Map<T, Object> getAllFields() {
            if (this.hasLazyField) {
                SmallSortedMap result = FieldSet.cloneAllFieldsMap(this.fields, false, true);
                if (this.fields.isImmutable()) {
                    result.makeImmutable();
                } else {
                    Builder.replaceBuilders(result, true);
                }
                return result;
            }
            return this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields);
        }

        public boolean hasField(T descriptor) {
            if (descriptor.isRepeated()) {
                throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
            }
            return this.fields.get(descriptor) != null;
        }

        public Object getField(T descriptor) {
            Object value = this.getFieldAllowBuilders(descriptor);
            return Builder.replaceBuilders(descriptor, value, true);
        }

        Object getFieldAllowBuilders(T descriptor) {
            Object o = this.fields.get(descriptor);
            if (o instanceof LazyField) {
                return ((LazyField)o).getValue();
            }
            return o;
        }

        private void ensureIsMutable() {
            if (!this.isMutable) {
                this.fields = FieldSet.cloneAllFieldsMap(this.fields, true, false);
                this.isMutable = true;
            }
        }

        public void setField(T descriptor, Object value) {
            this.ensureIsMutable();
            if (descriptor.isRepeated()) {
                if (!(value instanceof List)) {
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                }
                ArrayList newList = new ArrayList(value);
                int newListSize = newList.size();
                for (int i = 0; i < newListSize; ++i) {
                    Object element = newList.get(i);
                    this.verifyType(descriptor, element);
                    this.hasNestedBuilders = this.hasNestedBuilders || element instanceof MessageLite.Builder;
                }
                value = newList;
            } else {
                this.verifyType(descriptor, value);
            }
            if (value instanceof LazyField) {
                this.hasLazyField = true;
            }
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            this.fields.put(descriptor, (Object)value);
        }

        public void clearField(T descriptor) {
            this.ensureIsMutable();
            this.fields.remove(descriptor);
            if (this.fields.isEmpty()) {
                this.hasLazyField = false;
            }
        }

        public int getRepeatedFieldCount(T descriptor) {
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedFieldCount() can only be called on repeated fields.");
            }
            Object value = this.getFieldAllowBuilders(descriptor);
            if (value == null) {
                return 0;
            }
            return ((List)value).size();
        }

        public Object getRepeatedField(T descriptor, int index) {
            if (this.hasNestedBuilders) {
                this.ensureIsMutable();
            }
            Object value = this.getRepeatedFieldAllowBuilders(descriptor, index);
            return Builder.replaceBuilder(value, true);
        }

        Object getRepeatedFieldAllowBuilders(T descriptor, int index) {
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
            }
            Object value = this.getFieldAllowBuilders(descriptor);
            if (value == null) {
                throw new IndexOutOfBoundsException();
            }
            return ((List)value).get(index);
        }

        public void setRepeatedField(T descriptor, int index, Object value) {
            this.ensureIsMutable();
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
            }
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            Object list = this.getFieldAllowBuilders(descriptor);
            if (list == null) {
                throw new IndexOutOfBoundsException();
            }
            this.verifyType(descriptor, value);
            ((List)list).set(index, value);
        }

        public void addRepeatedField(T descriptor, Object value) {
            ArrayList<Object> list;
            this.ensureIsMutable();
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
            }
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            this.verifyType(descriptor, value);
            Object existingValue = this.getFieldAllowBuilders(descriptor);
            if (existingValue == null) {
                list = new ArrayList<Object>();
                this.fields.put(descriptor, (Object)list);
            } else {
                list = (ArrayList<Object>)existingValue;
            }
            list.add(value);
        }

        private void verifyType(T descriptor, Object value) {
            if (!FieldSet.isValidType(descriptor.getLiteType(), value)) {
                if (descriptor.getLiteType().getJavaType() == WireFormat.JavaType.MESSAGE && value instanceof MessageLite.Builder) {
                    return;
                }
                throw new IllegalArgumentException(String.format("Wrong object type used with protocol message reflection.\nField number: %d, field java type: %s, value type: %s\n", new Object[]{descriptor.getNumber(), descriptor.getLiteType().getJavaType(), value.getClass().getName()}));
            }
        }

        public boolean isInitialized() {
            int n = this.fields.getNumArrayEntries();
            for (int i = 0; i < n; ++i) {
                if (FieldSet.isInitialized(this.fields.getArrayEntryAt(i))) continue;
                return false;
            }
            for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
                if (FieldSet.isInitialized(entry)) continue;
                return false;
            }
            return true;
        }

        public void mergeFrom(FieldSet<T> other) {
            this.ensureIsMutable();
            int n = ((FieldSet)other).fields.getNumArrayEntries();
            for (int i = 0; i < n; ++i) {
                this.mergeFromField(((FieldSet)other).fields.getArrayEntryAt(i));
            }
            for (Map.Entry entry : ((FieldSet)other).fields.getOverflowEntries()) {
                this.mergeFromField(entry);
            }
        }

        private void mergeFromField(Map.Entry<T, Object> entry) {
            FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
            Object otherValue = entry.getValue();
            boolean isLazyField = otherValue instanceof LazyField;
            if (descriptor.isRepeated()) {
                if (isLazyField) {
                    throw new IllegalStateException("Lazy fields can not be repeated");
                }
                ArrayList<Object> value = (ArrayList<Object>)this.getFieldAllowBuilders(descriptor);
                List otherList = (List)otherValue;
                int otherListSize = otherList.size();
                if (value == null) {
                    value = new ArrayList<Object>(otherListSize);
                    this.fields.put((T)descriptor, (Object)value);
                }
                for (int i = 0; i < otherListSize; ++i) {
                    Object element = otherList.get(i);
                    value.add(FieldSet.cloneIfMutable(element));
                }
            } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
                Object value = this.getFieldAllowBuilders(descriptor);
                if (value == null) {
                    this.fields.put((T)descriptor, FieldSet.cloneIfMutable(otherValue));
                    if (isLazyField) {
                        this.hasLazyField = true;
                    }
                } else {
                    if (otherValue instanceof LazyField) {
                        otherValue = ((LazyField)otherValue).getValue();
                    }
                    if (descriptor.internalMessageIsImmutable(value)) {
                        MessageLite.Builder builder = ((MessageLite)value).toBuilder();
                        descriptor.internalMergeFrom(builder, otherValue);
                        value = builder.build();
                        this.fields.put((T)descriptor, value);
                    } else {
                        descriptor.internalMergeFrom(value, otherValue);
                    }
                }
            } else {
                if (isLazyField) {
                    throw new IllegalStateException("Lazy fields must be message-valued");
                }
                this.fields.put((T)descriptor, FieldSet.cloneIfMutable(otherValue));
            }
        }
    }

    public static interface FieldDescriptorLite<T extends FieldDescriptorLite<T>>
    extends Comparable<T> {
        public int getNumber();

        public WireFormat.FieldType getLiteType();

        public WireFormat.JavaType getLiteJavaType();

        public boolean isRepeated();

        public boolean isPacked();

        public Internal.EnumLiteMap<?> getEnumType();

        public boolean internalMessageIsImmutable(Object var1);

        public void internalMergeFrom(Object var1, Object var2);
    }
}

