/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MapFieldLite;
import com.google.protobuf.Message;
import com.google.protobuf.MessageReflection;
import com.google.protobuf.TextFormat;
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessage
extends AbstractMessageLite
implements Message {
    protected int memoizedSize = -1;

    @Override
    public boolean isInitialized() {
        return MessageReflection.isInitialized(this);
    }

    protected Message.Builder newBuilderForType(BuilderParent parent) {
        throw new UnsupportedOperationException("Nested builder is not supported for this type.");
    }

    @Override
    public List<String> findInitializationErrors() {
        return MessageReflection.findMissingFields(this);
    }

    @Override
    public String getInitializationErrorString() {
        return MessageReflection.delimitWithCommas(this.findInitializationErrors());
    }

    @Override
    public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
        throw new UnsupportedOperationException("hasOneof() is not implemented.");
    }

    @Override
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
        throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
    }

    @Override
    public final String toString() {
        return TextFormat.Printer.getOutputModePrinter().printToString(this, TextFormat.Printer.FieldReporterLevel.ABSTRACT_TO_STRING);
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        MessageReflection.writeMessageTo(this, this.getAllFields(), output, false);
    }

    @Override
    int getMemoizedSerializedSize() {
        return this.memoizedSize;
    }

    @Override
    void setMemoizedSerializedSize(int size) {
        this.memoizedSize = size;
    }

    @Override
    public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1) {
            return size;
        }
        this.memoizedSize = MessageReflection.getSerializedSize(this, this.getAllFields());
        return this.memoizedSize;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Message)) {
            return false;
        }
        Message otherMessage = (Message)other;
        if (this.getDescriptorForType() != otherMessage.getDescriptorForType()) {
            return false;
        }
        return AbstractMessage.compareFields(this.getAllFields(), otherMessage.getAllFields()) && this.getUnknownFields().equals(otherMessage.getUnknownFields());
    }

    @Override
    public int hashCode() {
        int hash = this.memoizedHashCode;
        if (hash == 0) {
            hash = 41;
            hash = 19 * hash + this.getDescriptorForType().hashCode();
            hash = AbstractMessage.hashFields(hash, this.getAllFields());
            this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        }
        return hash;
    }

    private static ByteString toByteString(Object value) {
        if (value instanceof byte[]) {
            return ByteString.copyFrom((byte[])value);
        }
        return (ByteString)value;
    }

    private static boolean compareBytes(Object a, Object b) {
        if (a instanceof byte[] && b instanceof byte[]) {
            return Arrays.equals((byte[])a, (byte[])b);
        }
        return AbstractMessage.toByteString(a).equals(AbstractMessage.toByteString(b));
    }

    private static Map convertMapEntryListToMap(List list) {
        if (list.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        Iterator iterator = list.iterator();
        Message entry = (Message)iterator.next();
        Descriptors.Descriptor descriptor = entry.getDescriptorForType();
        Descriptors.FieldDescriptor key = descriptor.findFieldByName("key");
        Descriptors.FieldDescriptor value = descriptor.findFieldByName("value");
        Object fieldValue = entry.getField(value);
        if (fieldValue instanceof Descriptors.EnumValueDescriptor) {
            fieldValue = ((Descriptors.EnumValueDescriptor)fieldValue).getNumber();
        }
        result.put(entry.getField(key), fieldValue);
        while (iterator.hasNext()) {
            entry = (Message)iterator.next();
            fieldValue = entry.getField(value);
            if (fieldValue instanceof Descriptors.EnumValueDescriptor) {
                fieldValue = ((Descriptors.EnumValueDescriptor)fieldValue).getNumber();
            }
            result.put(entry.getField(key), fieldValue);
        }
        return result;
    }

    private static boolean compareMapField(Object a, Object b) {
        Map ma = AbstractMessage.convertMapEntryListToMap((List)a);
        Map mb = AbstractMessage.convertMapEntryListToMap((List)b);
        return MapFieldLite.equals(ma, mb);
    }

    static boolean compareFields(Map<Descriptors.FieldDescriptor, Object> a, Map<Descriptors.FieldDescriptor, Object> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (Descriptors.FieldDescriptor descriptor : a.keySet()) {
            if (!b.containsKey(descriptor)) {
                return false;
            }
            Object value1 = a.get(descriptor);
            Object value2 = b.get(descriptor);
            if (descriptor.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
                if (descriptor.isRepeated()) {
                    List list1 = (List)value1;
                    List list2 = (List)value2;
                    if (list1.size() != list2.size()) {
                        return false;
                    }
                    for (int i = 0; i < list1.size(); ++i) {
                        if (AbstractMessage.compareBytes(list1.get(i), list2.get(i))) continue;
                        return false;
                    }
                    continue;
                }
                if (AbstractMessage.compareBytes(value1, value2)) continue;
                return false;
            }
            if (!(descriptor.isMapField() ? !AbstractMessage.compareMapField(value1, value2) : !value1.equals(value2))) continue;
            return false;
        }
        return true;
    }

    private static int hashMapField(Object value) {
        return MapFieldLite.calculateHashCodeForMap(AbstractMessage.convertMapEntryListToMap((List)value));
    }

    protected static int hashFields(int hash, Map<Descriptors.FieldDescriptor, Object> map) {
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : map.entrySet()) {
            Descriptors.FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();
            hash = 37 * hash + field.getNumber();
            if (field.isMapField()) {
                hash = 53 * hash + AbstractMessage.hashMapField(value);
                continue;
            }
            if (field.getType() != Descriptors.FieldDescriptor.Type.ENUM) {
                hash = 53 * hash + value.hashCode();
                continue;
            }
            if (field.isRepeated()) {
                List list = (List)value;
                hash = 53 * hash + Internal.hashEnumList(list);
                continue;
            }
            hash = 53 * hash + Internal.hashEnum((Internal.EnumLite)value);
        }
        return hash;
    }

    @Override
    UninitializedMessageException newUninitializedMessageException() {
        return Builder.newUninitializedMessageException(this);
    }

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
    extends AbstractMessageLite.Builder
    implements Message.Builder {
        @Override
        public BuilderType clone() {
            throw new UnsupportedOperationException("clone() should be implemented in subclasses.");
        }

        @Override
        public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
            throw new UnsupportedOperationException("hasOneof() is not implemented.");
        }

        @Override
        public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
            throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
        }

        public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
            throw new UnsupportedOperationException("clearOneof() is not implemented.");
        }

        public BuilderType clear() {
            for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : this.getAllFields().entrySet()) {
                this.clearField(entry.getKey());
            }
            return (BuilderType)this;
        }

        @Override
        public List<String> findInitializationErrors() {
            return MessageReflection.findMissingFields(this);
        }

        @Override
        public String getInitializationErrorString() {
            return MessageReflection.delimitWithCommas(this.findInitializationErrors());
        }

        protected BuilderType internalMergeFrom(AbstractMessageLite other) {
            return (BuilderType)this.mergeFrom((Message)((Object)other));
        }

        public BuilderType mergeFrom(Message other) {
            return this.mergeFrom(other, other.getAllFields());
        }

        BuilderType mergeFrom(Message other, Map<Descriptors.FieldDescriptor, Object> allFields) {
            if (other.getDescriptorForType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
            }
            for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : allFields.entrySet()) {
                Descriptors.FieldDescriptor field = entry.getKey();
                if (field.isRepeated()) {
                    for (Object element : (List)entry.getValue()) {
                        this.addRepeatedField(field, element);
                    }
                    continue;
                }
                if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    Message existingValue = (Message)this.getField(field);
                    if (existingValue == existingValue.getDefaultInstanceForType()) {
                        this.setField(field, entry.getValue());
                        continue;
                    }
                    this.setField(field, existingValue.newBuilderForType().mergeFrom(existingValue).mergeFrom((Message)entry.getValue()).build());
                    continue;
                }
                this.setField(field, entry.getValue());
            }
            this.mergeUnknownFields(other.getUnknownFields());
            return (BuilderType)this;
        }

        @Override
        public BuilderType mergeFrom(CodedInputStream input) throws IOException {
            return (BuilderType)this.mergeFrom(input, (ExtensionRegistryLite)ExtensionRegistry.getEmptyRegistry());
        }

        @Override
        public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            boolean discardUnknown = input.shouldDiscardUnknownFields();
            UnknownFieldSet.Builder unknownFields = discardUnknown ? null : this.getUnknownFieldSetBuilder();
            MessageReflection.mergeMessageFrom(this, unknownFields, input, extensionRegistry);
            if (unknownFields != null) {
                this.setUnknownFieldSetBuilder(unknownFields);
            }
            return (BuilderType)this;
        }

        protected UnknownFieldSet.Builder getUnknownFieldSetBuilder() {
            return UnknownFieldSet.newBuilder(this.getUnknownFields());
        }

        protected void setUnknownFieldSetBuilder(UnknownFieldSet.Builder builder) {
            this.setUnknownFields(builder.build());
        }

        public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
            this.setUnknownFields(UnknownFieldSet.newBuilder(this.getUnknownFields()).mergeFrom(unknownFields).build());
            return (BuilderType)this;
        }

        @Override
        public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
            throw new UnsupportedOperationException("getFieldBuilder() called on an unsupported message type.");
        }

        @Override
        public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
            throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on an unsupported message type.");
        }

        public String toString() {
            return TextFormat.Printer.getOutputModePrinter().printToString(this, TextFormat.Printer.FieldReporterLevel.ABSTRACT_BUILDER_TO_STRING);
        }

        protected static UninitializedMessageException newUninitializedMessageException(Message message) {
            return new UninitializedMessageException(MessageReflection.findMissingFields(message));
        }

        void markClean() {
            throw new IllegalStateException("Should be overridden by subclasses.");
        }

        void dispose() {
            throw new IllegalStateException("Should be overridden by subclasses.");
        }

        @Override
        public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
            return (BuilderType)((Builder)super.mergeFrom(data));
        }

        @Override
        public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (BuilderType)((Builder)super.mergeFrom(data, extensionRegistry));
        }

        @Override
        public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
            return (BuilderType)((Builder)super.mergeFrom(data));
        }

        @Override
        public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
            return (BuilderType)((Builder)super.mergeFrom(data, off, len));
        }

        @Override
        public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (BuilderType)((Builder)super.mergeFrom(data, extensionRegistry));
        }

        @Override
        public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (BuilderType)((Builder)super.mergeFrom(data, off, len, extensionRegistry));
        }

        @Override
        public BuilderType mergeFrom(InputStream input) throws IOException {
            return (BuilderType)((Builder)super.mergeFrom(input));
        }

        @Override
        public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (BuilderType)((Builder)super.mergeFrom(input, extensionRegistry));
        }
    }

    protected static interface BuilderParent {
        public void markDirty();
    }
}

