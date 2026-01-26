/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.LazyField;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class MessageReflection {
    MessageReflection() {
    }

    static void writeMessageTo(Message message, Map<Descriptors.FieldDescriptor, Object> fields, CodedOutputStream output, boolean alwaysWriteRequiredFields) throws IOException {
        boolean isMessageSet = message.getDescriptorForType().getOptions().getMessageSetWireFormat();
        if (alwaysWriteRequiredFields) {
            fields = new TreeMap<Descriptors.FieldDescriptor, Object>(fields);
            for (Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
                if (!field.isRequired() || fields.containsKey(field)) continue;
                fields.put(field, message.getField(field));
            }
        }
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
            Descriptors.FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();
            if (isMessageSet && field.isExtension() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && !field.isRepeated()) {
                output.writeMessageSetExtension(field.getNumber(), (Message)value);
                continue;
            }
            FieldSet.writeField(field, value, output);
        }
        UnknownFieldSet unknownFields = message.getUnknownFields();
        if (isMessageSet) {
            unknownFields.writeAsMessageSetTo(output);
        } else {
            unknownFields.writeTo(output);
        }
    }

    static int getSerializedSize(Message message, Map<Descriptors.FieldDescriptor, Object> fields) {
        int size = 0;
        boolean isMessageSet = message.getDescriptorForType().getOptions().getMessageSetWireFormat();
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
            Descriptors.FieldDescriptor field = entry.getKey();
            Object value = entry.getValue();
            if (isMessageSet && field.isExtension() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && !field.isRepeated()) {
                size += CodedOutputStream.computeMessageSetExtensionSize(field.getNumber(), (Message)value);
                continue;
            }
            size += FieldSet.computeFieldSize(field, value);
        }
        UnknownFieldSet unknownFields = message.getUnknownFields();
        size = isMessageSet ? (size += unknownFields.getSerializedSizeAsMessageSet()) : (size += unknownFields.getSerializedSize());
        return size;
    }

    static String delimitWithCommas(List<String> parts) {
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(part);
        }
        return result.toString();
    }

    static boolean isInitialized(MessageOrBuilder message) {
        for (Descriptors.FieldDescriptor fieldDescriptor : message.getDescriptorForType().getFields()) {
            if (!fieldDescriptor.isRequired() || message.hasField(fieldDescriptor)) continue;
            return false;
        }
        for (Map.Entry entry : message.getAllFields().entrySet()) {
            Descriptors.FieldDescriptor field = (Descriptors.FieldDescriptor)entry.getKey();
            if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) continue;
            if (field.isRepeated()) {
                for (Message element : (List)entry.getValue()) {
                    if (element.isInitialized()) continue;
                    return false;
                }
                continue;
            }
            if (((Message)entry.getValue()).isInitialized()) continue;
            return false;
        }
        return true;
    }

    private static String subMessagePrefix(String prefix, Descriptors.FieldDescriptor field, int index) {
        StringBuilder result = new StringBuilder(prefix);
        if (field.isExtension()) {
            result.append('(').append(field.getFullName()).append(')');
        } else {
            result.append(field.getName());
        }
        if (index != -1) {
            result.append('[').append(index).append(']');
        }
        result.append('.');
        return result.toString();
    }

    private static void findMissingFields(MessageOrBuilder message, String prefix, List<String> results) {
        for (Descriptors.FieldDescriptor fieldDescriptor : message.getDescriptorForType().getFields()) {
            if (!fieldDescriptor.isRequired() || message.hasField(fieldDescriptor)) continue;
            results.add(prefix + fieldDescriptor.getName());
        }
        for (Map.Entry entry : message.getAllFields().entrySet()) {
            Descriptors.FieldDescriptor field = (Descriptors.FieldDescriptor)entry.getKey();
            Object value = entry.getValue();
            if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) continue;
            if (field.isRepeated()) {
                int i = 0;
                for (Object element : (List)value) {
                    MessageReflection.findMissingFields((MessageOrBuilder)element, MessageReflection.subMessagePrefix(prefix, field, i++), results);
                }
                continue;
            }
            if (!message.hasField(field)) continue;
            MessageReflection.findMissingFields((MessageOrBuilder)value, MessageReflection.subMessagePrefix(prefix, field, -1), results);
        }
    }

    static List<String> findMissingFields(MessageOrBuilder message) {
        ArrayList<String> results = new ArrayList<String>();
        MessageReflection.findMissingFields(message, "", results);
        return results;
    }

    static boolean mergeFieldFrom(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, Descriptors.Descriptor type, MergeTarget target, int tag) throws IOException {
        Descriptors.FieldDescriptor field;
        if (type.getOptions().getMessageSetWireFormat() && tag == WireFormat.MESSAGE_SET_ITEM_TAG) {
            MessageReflection.mergeMessageSetExtensionFromCodedStream(input, unknownFields, extensionRegistry, type, target);
            return true;
        }
        int wireType = WireFormat.getTagWireType(tag);
        int fieldNumber = WireFormat.getTagFieldNumber(tag);
        Message defaultInstance = null;
        if (type.isExtensionNumber(fieldNumber)) {
            if (extensionRegistry instanceof ExtensionRegistry) {
                ExtensionRegistry.ExtensionInfo extension = target.findExtensionByNumber((ExtensionRegistry)extensionRegistry, type, fieldNumber);
                if (extension == null) {
                    field = null;
                } else {
                    field = extension.descriptor;
                    defaultInstance = extension.defaultInstance;
                    if (defaultInstance == null && field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        throw new IllegalStateException("Message-typed extension lacked default instance: " + field.getFullName());
                    }
                }
            } else {
                field = null;
            }
        } else {
            field = target.getContainerType() == MergeTarget.ContainerType.MESSAGE ? type.findFieldByNumber(fieldNumber) : null;
        }
        boolean unknown = false;
        boolean packed = false;
        if (field == null) {
            unknown = true;
        } else if (wireType == FieldSet.getWireFormatForFieldType(field.getLiteType(), false)) {
            packed = false;
        } else if (field.isPackable() && wireType == FieldSet.getWireFormatForFieldType(field.getLiteType(), true)) {
            packed = true;
        } else {
            unknown = true;
        }
        if (unknown) {
            if (unknownFields != null) {
                return unknownFields.mergeFieldFrom(tag, input);
            }
            return input.skipField(tag);
        }
        if (packed) {
            int length = input.readRawVarint32();
            int limit = input.pushLimit(length);
            if (field.getLiteType() == WireFormat.FieldType.ENUM) {
                while (input.getBytesUntilLimit() > 0) {
                    int rawValue = input.readEnum();
                    if (field.legacyEnumFieldTreatedAsClosed()) {
                        Descriptors.EnumValueDescriptor value = field.getEnumType().findValueByNumber(rawValue);
                        if (value == null) {
                            if (unknownFields == null) continue;
                            unknownFields.mergeVarintField(fieldNumber, rawValue);
                            continue;
                        }
                        target.addRepeatedField(field, value);
                        continue;
                    }
                    target.addRepeatedField(field, field.getEnumType().findValueByNumberCreatingIfUnknown(rawValue));
                }
            } else {
                while (input.getBytesUntilLimit() > 0) {
                    Object value = input.readPrimitiveField(field.getLiteType(), target.getUtf8Validation(field));
                    target.addRepeatedField(field, value);
                }
            }
            input.popLimit(limit);
        } else {
            Object value;
            switch (field.getType()) {
                case GROUP: {
                    target.mergeGroup(input, extensionRegistry, field, defaultInstance);
                    return true;
                }
                case MESSAGE: {
                    target.mergeMessage(input, extensionRegistry, field, defaultInstance);
                    return true;
                }
                case ENUM: {
                    int rawValue = input.readEnum();
                    if (field.legacyEnumFieldTreatedAsClosed()) {
                        value = field.getEnumType().findValueByNumber(rawValue);
                        if (value != null) break;
                        if (unknownFields != null) {
                            unknownFields.mergeVarintField(fieldNumber, rawValue);
                        }
                        return true;
                    }
                    value = field.getEnumType().findValueByNumberCreatingIfUnknown(rawValue);
                    break;
                }
                default: {
                    value = input.readPrimitiveField(field.getLiteType(), target.getUtf8Validation(field));
                }
            }
            if (field.isRepeated()) {
                target.addRepeatedField(field, value);
            } else {
                target.setField(field, value);
            }
        }
        return true;
    }

    static void mergeMessageFrom(Message.Builder target, UnknownFieldSet.Builder unknownFields, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        int tag;
        BuilderAdapter builderAdapter = new BuilderAdapter(target);
        Descriptors.Descriptor descriptorForType = target.getDescriptorForType();
        while ((tag = input.readTag()) != 0 && MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, descriptorForType, builderAdapter, tag)) {
        }
    }

    private static void mergeMessageSetExtensionFromCodedStream(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, Descriptors.Descriptor type, MergeTarget target) throws IOException {
        int tag;
        int typeId = 0;
        ByteString rawBytes = null;
        ExtensionRegistry.ExtensionInfo extension = null;
        while ((tag = input.readTag()) != 0) {
            if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
                typeId = input.readUInt32();
                if (typeId == 0 || !(extensionRegistry instanceof ExtensionRegistry)) continue;
                extension = target.findExtensionByNumber((ExtensionRegistry)extensionRegistry, type, typeId);
                continue;
            }
            if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
                if (typeId != 0 && extension != null && ExtensionRegistryLite.isEagerlyParseMessageSets()) {
                    MessageReflection.eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, target);
                    rawBytes = null;
                    continue;
                }
                rawBytes = input.readBytes();
                continue;
            }
            if (tag != WireFormat.MESSAGE_SET_ITEM_END_TAG && input.skipField(tag)) continue;
            break;
        }
        input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
        if (rawBytes != null && typeId != 0) {
            if (extension != null) {
                MessageReflection.mergeMessageSetExtensionFromBytes(rawBytes, extension, extensionRegistry, target);
            } else if (rawBytes != null && unknownFields != null) {
                unknownFields.mergeField(typeId, UnknownFieldSet.Field.newBuilder().addLengthDelimited(rawBytes).build());
            }
        }
    }

    private static void mergeMessageSetExtensionFromBytes(ByteString rawBytes, ExtensionRegistry.ExtensionInfo extension, ExtensionRegistryLite extensionRegistry, MergeTarget target) throws IOException {
        Descriptors.FieldDescriptor field = extension.descriptor;
        boolean hasOriginalValue = target.hasField(field);
        if (hasOriginalValue || ExtensionRegistryLite.isEagerlyParseMessageSets()) {
            Object value = target.parseMessageFromBytes(rawBytes, extensionRegistry, field, extension.defaultInstance);
            target.setField(field, value);
        } else {
            LazyField lazyField = new LazyField(extension.defaultInstance, extensionRegistry, rawBytes);
            target.setField(field, lazyField);
        }
    }

    private static void eagerlyMergeMessageSetExtension(CodedInputStream input, ExtensionRegistry.ExtensionInfo extension, ExtensionRegistryLite extensionRegistry, MergeTarget target) throws IOException {
        Descriptors.FieldDescriptor field = extension.descriptor;
        Object value = target.parseMessage(input, extensionRegistry, field, extension.defaultInstance);
        target.setField(field, value);
    }

    static interface MergeTarget {
        public Descriptors.Descriptor getDescriptorForType();

        public ContainerType getContainerType();

        public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry var1, String var2);

        public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry var1, Descriptors.Descriptor var2, int var3);

        public Object getField(Descriptors.FieldDescriptor var1);

        public boolean hasField(Descriptors.FieldDescriptor var1);

        public MergeTarget setField(Descriptors.FieldDescriptor var1, Object var2);

        public MergeTarget clearField(Descriptors.FieldDescriptor var1);

        public MergeTarget setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3);

        public MergeTarget addRepeatedField(Descriptors.FieldDescriptor var1, Object var2);

        public boolean hasOneof(Descriptors.OneofDescriptor var1);

        public MergeTarget clearOneof(Descriptors.OneofDescriptor var1);

        public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor var1);

        public Object parseGroup(CodedInputStream var1, ExtensionRegistryLite var2, Descriptors.FieldDescriptor var3, Message var4) throws IOException;

        public Object parseMessage(CodedInputStream var1, ExtensionRegistryLite var2, Descriptors.FieldDescriptor var3, Message var4) throws IOException;

        public Object parseMessageFromBytes(ByteString var1, ExtensionRegistryLite var2, Descriptors.FieldDescriptor var3, Message var4) throws IOException;

        public void mergeGroup(CodedInputStream var1, ExtensionRegistryLite var2, Descriptors.FieldDescriptor var3, Message var4) throws IOException;

        public void mergeMessage(CodedInputStream var1, ExtensionRegistryLite var2, Descriptors.FieldDescriptor var3, Message var4) throws IOException;

        public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor var1);

        public MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor var1, Message var2);

        public MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor var1, Message var2);

        public Object finish();

        public static enum ContainerType {
            MESSAGE,
            EXTENSION_SET;

        }
    }

    static class BuilderAdapter
    implements MergeTarget {
        private final Message.Builder builder;
        private boolean hasNestedBuilders = true;

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return this.builder.getDescriptorForType();
        }

        public BuilderAdapter(Message.Builder builder) {
            this.builder = builder;
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            return this.builder.getField(field);
        }

        private Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
            if (this.hasNestedBuilders) {
                try {
                    return this.builder.getFieldBuilder(field);
                }
                catch (UnsupportedOperationException e) {
                    this.hasNestedBuilders = false;
                }
            }
            return null;
        }

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            return this.builder.hasField(field);
        }

        @Override
        public MergeTarget setField(Descriptors.FieldDescriptor field, Object value) {
            if (!field.isRepeated() && value instanceof MessageLite.Builder) {
                if (value != this.getFieldBuilder(field)) {
                    this.builder.setField(field, ((MessageLite.Builder)value).buildPartial());
                }
                return this;
            }
            this.builder.setField(field, value);
            return this;
        }

        @Override
        public MergeTarget clearField(Descriptors.FieldDescriptor field) {
            this.builder.clearField(field);
            return this;
        }

        @Override
        public MergeTarget setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            if (value instanceof MessageLite.Builder) {
                value = ((MessageLite.Builder)value).buildPartial();
            }
            this.builder.setRepeatedField(field, index, value);
            return this;
        }

        @Override
        public MergeTarget addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            if (value instanceof MessageLite.Builder) {
                value = ((MessageLite.Builder)value).buildPartial();
            }
            this.builder.addRepeatedField(field, value);
            return this;
        }

        @Override
        public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
            return this.builder.hasOneof(oneof);
        }

        @Override
        public MergeTarget clearOneof(Descriptors.OneofDescriptor oneof) {
            this.builder.clearOneof(oneof);
            return this;
        }

        @Override
        public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
            return this.builder.getOneofFieldDescriptor(oneof);
        }

        @Override
        public MergeTarget.ContainerType getContainerType() {
            return MergeTarget.ContainerType.MESSAGE;
        }

        @Override
        public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry registry, String name) {
            return registry.findImmutableExtensionByName(name);
        }

        @Override
        public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry registry, Descriptors.Descriptor containingType, int fieldNumber) {
            return registry.findImmutableExtensionByNumber(containingType, fieldNumber);
        }

        @Override
        public Object parseGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance != null ? defaultInstance.newBuilderForType() : this.builder.newBuilderForField(field);
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
            return subBuilder.buildPartial();
        }

        @Override
        public Object parseMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance != null ? defaultInstance.newBuilderForType() : this.builder.newBuilderForField(field);
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            input.readMessage(subBuilder, extensionRegistry);
            return subBuilder.buildPartial();
        }

        @Override
        public Object parseMessageFromBytes(ByteString bytes, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance != null ? defaultInstance.newBuilderForType() : this.builder.newBuilderForField(field);
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            subBuilder.mergeFrom(bytes, extensionRegistry);
            return subBuilder.buildPartial();
        }

        @Override
        public void mergeGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            if (!field.isRepeated()) {
                Message.Builder subBuilder;
                if (this.hasField(field)) {
                    subBuilder = this.getFieldBuilder(field);
                    if (subBuilder != null) {
                        input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                        return;
                    }
                    subBuilder = this.newMessageFieldInstance(field, defaultInstance);
                    subBuilder.mergeFrom((Message)this.getField(field));
                } else {
                    subBuilder = this.newMessageFieldInstance(field, defaultInstance);
                }
                input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.setField(field, subBuilder.buildPartial());
            } else {
                Message.Builder subBuilder = this.newMessageFieldInstance(field, defaultInstance);
                input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.addRepeatedField(field, subBuilder.buildPartial());
            }
        }

        @Override
        public void mergeMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            if (!field.isRepeated()) {
                Message.Builder subBuilder;
                if (this.hasField(field)) {
                    subBuilder = this.getFieldBuilder(field);
                    if (subBuilder != null) {
                        input.readMessage(subBuilder, extensionRegistry);
                        return;
                    }
                    subBuilder = this.newMessageFieldInstance(field, defaultInstance);
                    subBuilder.mergeFrom((Message)this.getField(field));
                } else {
                    subBuilder = this.newMessageFieldInstance(field, defaultInstance);
                }
                input.readMessage(subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.setField(field, subBuilder.buildPartial());
            } else {
                Message.Builder subBuilder = this.newMessageFieldInstance(field, defaultInstance);
                input.readMessage(subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.addRepeatedField(field, subBuilder.buildPartial());
            }
        }

        private Message.Builder newMessageFieldInstance(Descriptors.FieldDescriptor field, Message defaultInstance) {
            if (defaultInstance != null) {
                return defaultInstance.newBuilderForType();
            }
            return this.builder.newBuilderForField(field);
        }

        @Override
        public MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor field, Message defaultInstance) {
            Message originalMessage;
            Message.Builder subBuilder;
            if (!field.isRepeated() && this.hasField(field) && (subBuilder = this.getFieldBuilder(field)) != null) {
                return new BuilderAdapter(subBuilder);
            }
            subBuilder = this.newMessageFieldInstance(field, defaultInstance);
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            return new BuilderAdapter(subBuilder);
        }

        @Override
        public MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor field, Message defaultInstance) {
            Message.Builder subBuilder = defaultInstance != null ? defaultInstance.newBuilderForType() : this.builder.newBuilderForField(field);
            return new BuilderAdapter(subBuilder);
        }

        @Override
        public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor descriptor) {
            if (descriptor.needsUtf8Check()) {
                return WireFormat.Utf8Validation.STRICT;
            }
            if (!descriptor.isRepeated() && this.builder instanceof GeneratedMessage.Builder) {
                return WireFormat.Utf8Validation.LAZY;
            }
            return WireFormat.Utf8Validation.LOOSE;
        }

        @Override
        public Object finish() {
            return this.builder;
        }
    }

    static class ExtensionBuilderAdapter
    implements MergeTarget {
        private final FieldSet.Builder<Descriptors.FieldDescriptor> extensions;

        ExtensionBuilderAdapter(FieldSet.Builder<Descriptors.FieldDescriptor> extensions) {
            this.extensions = extensions;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            throw new UnsupportedOperationException("getDescriptorForType() called on FieldSet object");
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            return this.extensions.getField(field);
        }

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            return this.extensions.hasField(field);
        }

        @Override
        @CanIgnoreReturnValue
        public MergeTarget setField(Descriptors.FieldDescriptor field, Object value) {
            this.extensions.setField(field, value);
            return this;
        }

        @Override
        @CanIgnoreReturnValue
        public MergeTarget clearField(Descriptors.FieldDescriptor field) {
            this.extensions.clearField(field);
            return this;
        }

        @Override
        @CanIgnoreReturnValue
        public MergeTarget setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            this.extensions.setRepeatedField(field, index, value);
            return this;
        }

        @Override
        @CanIgnoreReturnValue
        public MergeTarget addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            this.extensions.addRepeatedField(field, value);
            return this;
        }

        @Override
        public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
            return false;
        }

        @Override
        @CanIgnoreReturnValue
        public MergeTarget clearOneof(Descriptors.OneofDescriptor oneof) {
            return this;
        }

        @Override
        public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
            return null;
        }

        @Override
        public MergeTarget.ContainerType getContainerType() {
            return MergeTarget.ContainerType.EXTENSION_SET;
        }

        @Override
        public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry registry, String name) {
            return registry.findImmutableExtensionByName(name);
        }

        @Override
        public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry registry, Descriptors.Descriptor containingType, int fieldNumber) {
            return registry.findImmutableExtensionByNumber(containingType, fieldNumber);
        }

        @Override
        public Object parseGroup(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance.newBuilderForType();
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            input.readGroup(field.getNumber(), subBuilder, registry);
            return subBuilder.buildPartial();
        }

        @Override
        public Object parseMessage(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance.newBuilderForType();
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            input.readMessage(subBuilder, registry);
            return subBuilder.buildPartial();
        }

        @Override
        public void mergeGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            if (!field.isRepeated()) {
                if (this.hasField(field)) {
                    MessageLite.Builder subBuilder;
                    Object fieldOrBuilder = this.extensions.getFieldAllowBuilders(field);
                    if (fieldOrBuilder instanceof MessageLite.Builder) {
                        subBuilder = (MessageLite.Builder)fieldOrBuilder;
                    } else {
                        subBuilder = ((MessageLite)fieldOrBuilder).toBuilder();
                        this.extensions.setField(field, subBuilder);
                    }
                    input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                    return;
                }
                Message.Builder subBuilder = defaultInstance.newBuilderForType();
                input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.setField(field, subBuilder);
            } else {
                Message.Builder subBuilder = defaultInstance.newBuilderForType();
                input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.addRepeatedField(field, subBuilder.buildPartial());
            }
        }

        @Override
        public void mergeMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            if (!field.isRepeated()) {
                if (this.hasField(field)) {
                    MessageLite.Builder subBuilder;
                    Object fieldOrBuilder = this.extensions.getFieldAllowBuilders(field);
                    if (fieldOrBuilder instanceof MessageLite.Builder) {
                        subBuilder = (MessageLite.Builder)fieldOrBuilder;
                    } else {
                        subBuilder = ((MessageLite)fieldOrBuilder).toBuilder();
                        this.extensions.setField(field, subBuilder);
                    }
                    input.readMessage(subBuilder, extensionRegistry);
                    return;
                }
                Message.Builder subBuilder = defaultInstance.newBuilderForType();
                input.readMessage(subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.setField(field, subBuilder);
            } else {
                Message.Builder subBuilder = defaultInstance.newBuilderForType();
                input.readMessage(subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.addRepeatedField(field, subBuilder.buildPartial());
            }
        }

        @Override
        public Object parseMessageFromBytes(ByteString bytes, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance.newBuilderForType();
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            subBuilder.mergeFrom(bytes, registry);
            return subBuilder.buildPartial();
        }

        @Override
        public MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
            throw new UnsupportedOperationException("newMergeTargetForField() called on FieldSet object");
        }

        @Override
        public MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
            throw new UnsupportedOperationException("newEmptyTargetForField() called on FieldSet object");
        }

        @Override
        public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor descriptor) {
            if (descriptor.needsUtf8Check()) {
                return WireFormat.Utf8Validation.STRICT;
            }
            return WireFormat.Utf8Validation.LOOSE;
        }

        @Override
        public Object finish() {
            throw new UnsupportedOperationException("finish() called on FieldSet object");
        }
    }

    static class ExtensionAdapter
    implements MergeTarget {
        private final FieldSet<Descriptors.FieldDescriptor> extensions;

        ExtensionAdapter(FieldSet<Descriptors.FieldDescriptor> extensions) {
            this.extensions = extensions;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            throw new UnsupportedOperationException("getDescriptorForType() called on FieldSet object");
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            return this.extensions.getField(field);
        }

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            return this.extensions.hasField(field);
        }

        @Override
        public MergeTarget setField(Descriptors.FieldDescriptor field, Object value) {
            this.extensions.setField(field, value);
            return this;
        }

        @Override
        public MergeTarget clearField(Descriptors.FieldDescriptor field) {
            this.extensions.clearField(field);
            return this;
        }

        @Override
        public MergeTarget setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            this.extensions.setRepeatedField(field, index, value);
            return this;
        }

        @Override
        public MergeTarget addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            this.extensions.addRepeatedField(field, value);
            return this;
        }

        @Override
        public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
            return false;
        }

        @Override
        public MergeTarget clearOneof(Descriptors.OneofDescriptor oneof) {
            return this;
        }

        @Override
        public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
            return null;
        }

        @Override
        public MergeTarget.ContainerType getContainerType() {
            return MergeTarget.ContainerType.EXTENSION_SET;
        }

        @Override
        public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry registry, String name) {
            return registry.findImmutableExtensionByName(name);
        }

        @Override
        public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry registry, Descriptors.Descriptor containingType, int fieldNumber) {
            return registry.findImmutableExtensionByNumber(containingType, fieldNumber);
        }

        @Override
        public Object parseGroup(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance.newBuilderForType();
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            input.readGroup(field.getNumber(), subBuilder, registry);
            return subBuilder.buildPartial();
        }

        @Override
        public Object parseMessage(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance.newBuilderForType();
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            input.readMessage(subBuilder, registry);
            return subBuilder.buildPartial();
        }

        @Override
        public void mergeGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            if (!field.isRepeated()) {
                if (this.hasField(field)) {
                    MessageLite.Builder current = ((MessageLite)this.getField(field)).toBuilder();
                    input.readGroup(field.getNumber(), current, extensionRegistry);
                    MergeTarget unused = this.setField(field, current.buildPartial());
                    return;
                }
                Message.Builder subBuilder = defaultInstance.newBuilderForType();
                input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.setField(field, subBuilder.buildPartial());
            } else {
                Message.Builder subBuilder = defaultInstance.newBuilderForType();
                input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.addRepeatedField(field, subBuilder.buildPartial());
            }
        }

        @Override
        public void mergeMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            if (!field.isRepeated()) {
                if (this.hasField(field)) {
                    MessageLite.Builder current = ((MessageLite)this.getField(field)).toBuilder();
                    input.readMessage(current, extensionRegistry);
                    MergeTarget unused = this.setField(field, current.buildPartial());
                    return;
                }
                Message.Builder subBuilder = defaultInstance.newBuilderForType();
                input.readMessage(subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.setField(field, subBuilder.buildPartial());
            } else {
                Message.Builder subBuilder = defaultInstance.newBuilderForType();
                input.readMessage(subBuilder, extensionRegistry);
                MergeTarget mergeTarget = this.addRepeatedField(field, subBuilder.buildPartial());
            }
        }

        @Override
        public Object parseMessageFromBytes(ByteString bytes, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
            Message originalMessage;
            Message.Builder subBuilder = defaultInstance.newBuilderForType();
            if (!field.isRepeated() && (originalMessage = (Message)this.getField(field)) != null) {
                subBuilder.mergeFrom(originalMessage);
            }
            subBuilder.mergeFrom(bytes, registry);
            return subBuilder.buildPartial();
        }

        @Override
        public MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
            throw new UnsupportedOperationException("newMergeTargetForField() called on FieldSet object");
        }

        @Override
        public MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
            throw new UnsupportedOperationException("newEmptyTargetForField() called on FieldSet object");
        }

        @Override
        public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor descriptor) {
            if (descriptor.needsUtf8Check()) {
                return WireFormat.Utf8Validation.STRICT;
            }
            return WireFormat.Utf8Validation.LOOSE;
        }

        @Override
        public Object finish() {
            throw new UnsupportedOperationException("finish() called on FieldSet object");
        }
    }
}

