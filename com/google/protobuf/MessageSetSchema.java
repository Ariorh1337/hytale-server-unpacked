/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ArrayDecoders;
import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.ExtensionSchema;
import com.google.protobuf.FieldSet;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyField;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Protobuf;
import com.google.protobuf.Reader;
import com.google.protobuf.Schema;
import com.google.protobuf.SchemaUtil;
import com.google.protobuf.UnknownFieldSchema;
import com.google.protobuf.UnknownFieldSetLite;
import com.google.protobuf.WireFormat;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@CheckReturnValue
final class MessageSetSchema<T>
implements Schema<T> {
    private final MessageLite defaultInstance;
    private final UnknownFieldSchema<?, ?> unknownFieldSchema;
    private final boolean hasExtensions;
    private final ExtensionSchema<?> extensionSchema;

    private MessageSetSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
        this.unknownFieldSchema = unknownFieldSchema;
        this.hasExtensions = extensionSchema.hasExtensions(defaultInstance);
        this.extensionSchema = extensionSchema;
        this.defaultInstance = defaultInstance;
    }

    static <T> MessageSetSchema<T> newSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
        return new MessageSetSchema<T>(unknownFieldSchema, extensionSchema, defaultInstance);
    }

    @Override
    public T newInstance() {
        if (this.defaultInstance instanceof GeneratedMessageLite) {
            return (T)((GeneratedMessageLite)this.defaultInstance).newMutableInstance();
        }
        return (T)this.defaultInstance.newBuilderForType().buildPartial();
    }

    @Override
    public boolean equals(T message, T other) {
        Object otherUnknown;
        Object messageUnknown = this.unknownFieldSchema.getFromMessage(message);
        if (!messageUnknown.equals(otherUnknown = this.unknownFieldSchema.getFromMessage(other))) {
            return false;
        }
        if (this.hasExtensions) {
            FieldSet<?> messageExtensions = this.extensionSchema.getExtensions(message);
            FieldSet<?> otherExtensions = this.extensionSchema.getExtensions(other);
            return messageExtensions.equals(otherExtensions);
        }
        return true;
    }

    @Override
    public int hashCode(T message) {
        int hashCode = this.unknownFieldSchema.getFromMessage(message).hashCode();
        if (this.hasExtensions) {
            FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
            hashCode = hashCode * 53 + extensions.hashCode();
        }
        return hashCode;
    }

    @Override
    public void mergeFrom(T message, T other) {
        SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
        if (this.hasExtensions) {
            SchemaUtil.mergeExtensions(this.extensionSchema, message, other);
        }
    }

    @Override
    public void writeTo(T message, Writer writer) throws IOException {
        FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
        Iterator<Map.Entry<?, Object>> iterator = extensions.iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, Object> extension = iterator.next();
            FieldSet.FieldDescriptorLite fd = (FieldSet.FieldDescriptorLite)extension.getKey();
            if (fd.getLiteJavaType() != WireFormat.JavaType.MESSAGE || fd.isRepeated() || fd.isPacked()) {
                throw new IllegalStateException("Found invalid MessageSet item.");
            }
            if (extension instanceof LazyField.LazyEntry) {
                writer.writeMessageSetItem(fd.getNumber(), ((LazyField.LazyEntry)extension).getField().toByteString());
                continue;
            }
            writer.writeMessageSetItem(fd.getNumber(), extension.getValue());
        }
        this.writeUnknownFieldsHelper(this.unknownFieldSchema, message, writer);
    }

    private <UT, UB> void writeUnknownFieldsHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, T message, Writer writer) throws IOException {
        unknownFieldSchema.writeAsMessageSetTo(unknownFieldSchema.getFromMessage(message), writer);
    }

    @Override
    public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
        UnknownFieldSetLite unknownFields = ((GeneratedMessageLite)message).unknownFields;
        if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
            ((GeneratedMessageLite)message).unknownFields = unknownFields = UnknownFieldSetLite.newInstance();
        }
        FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
        GeneratedMessageLite.GeneratedExtension extension = null;
        while (position < limit) {
            position = ArrayDecoders.decodeVarint32(data, position, registers);
            int startTag = registers.int1;
            if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
                if (WireFormat.getTagWireType(startTag) == 2) {
                    extension = (GeneratedMessageLite.GeneratedExtension)this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, WireFormat.getTagFieldNumber(startTag));
                    if (extension != null) {
                        position = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass()), data, position, limit, registers);
                        extensions.setField(extension.descriptor, registers.object1);
                        continue;
                    }
                    position = ArrayDecoders.decodeUnknownField(startTag, data, position, limit, unknownFields, registers);
                    continue;
                }
                position = ArrayDecoders.skipField(startTag, data, position, limit, registers);
                continue;
            }
            int typeId = 0;
            ByteString rawBytes = null;
            block5: while (position < limit) {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                int tag = registers.int1;
                int number = WireFormat.getTagFieldNumber(tag);
                int wireType = WireFormat.getTagWireType(tag);
                switch (number) {
                    case 2: {
                        if (wireType != 0) break;
                        position = ArrayDecoders.decodeVarint32(data, position, registers);
                        typeId = registers.int1;
                        extension = (GeneratedMessageLite.GeneratedExtension)this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, typeId);
                        continue block5;
                    }
                    case 3: {
                        if (extension != null) {
                            position = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass()), data, position, limit, registers);
                            extensions.setField(extension.descriptor, registers.object1);
                            continue block5;
                        }
                        if (wireType != 2) break;
                        position = ArrayDecoders.decodeBytes(data, position, registers);
                        rawBytes = (ByteString)registers.object1;
                        continue block5;
                    }
                }
                if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG) break;
                position = ArrayDecoders.skipField(tag, data, position, limit, registers);
            }
            if (rawBytes == null) continue;
            unknownFields.storeField(WireFormat.makeTag(typeId, 2), rawBytes);
        }
        if (position != limit) {
            throw InvalidProtocolBufferException.parseFailure();
        }
    }

    @Override
    public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        this.mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        UB unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
        FieldSet<ET> extensions = extensionSchema.getMutableExtensions(message);
        try {
            do {
                int number;
                if ((number = reader.getFieldNumber()) != Integer.MAX_VALUE) continue;
                return;
            } while (this.parseMessageSetItemOrUnknownField(reader, extensionRegistry, extensionSchema, extensions, unknownFieldSchema, unknownFields));
            return;
        }
        finally {
            unknownFieldSchema.setBuilderToMessage(message, unknownFields);
        }
    }

    @Override
    public void makeImmutable(T message) {
        this.unknownFieldSchema.makeImmutable(message);
        this.extensionSchema.makeImmutable(message);
    }

    private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> boolean parseMessageSetItemOrUnknownField(Reader reader, ExtensionRegistryLite extensionRegistry, ExtensionSchema<ET> extensionSchema, FieldSet<ET> extensions, UnknownFieldSchema<UT, UB> unknownFieldSchema, UB unknownFields) throws IOException {
        int number;
        int startTag = reader.getTag();
        if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
            if (WireFormat.getTagWireType(startTag) == 2) {
                Object extension = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, WireFormat.getTagFieldNumber(startTag));
                if (extension != null) {
                    extensionSchema.parseLengthPrefixedMessageSetItem(reader, extension, extensionRegistry, extensions);
                    return true;
                }
                return unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader, 0);
            }
            return reader.skipField();
        }
        int typeId = 0;
        ByteString rawBytes = null;
        Object extension = null;
        while ((number = reader.getFieldNumber()) != Integer.MAX_VALUE) {
            int tag = reader.getTag();
            if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
                typeId = reader.readUInt32();
                extension = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, typeId);
                continue;
            }
            if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
                if (extension != null) {
                    extensionSchema.parseLengthPrefixedMessageSetItem(reader, extension, extensionRegistry, extensions);
                    continue;
                }
                rawBytes = reader.readBytes();
                continue;
            }
            if (tag != WireFormat.MESSAGE_SET_ITEM_END_TAG && reader.skipField()) continue;
            break;
        }
        if (reader.getTag() != WireFormat.MESSAGE_SET_ITEM_END_TAG) {
            throw InvalidProtocolBufferException.invalidEndTag();
        }
        if (rawBytes != null) {
            if (extension != null) {
                extensionSchema.parseMessageSetItem(rawBytes, extension, extensionRegistry, extensions);
            } else {
                unknownFieldSchema.addLengthDelimited(unknownFields, typeId, rawBytes);
            }
        }
        return true;
    }

    @Override
    public final boolean isInitialized(T message) {
        FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
        return extensions.isInitialized();
    }

    @Override
    public int getSerializedSize(T message) {
        int size = 0;
        size += this.getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
        if (this.hasExtensions) {
            size += this.extensionSchema.getExtensions(message).getMessageSetSerializedSize();
        }
        return size;
    }

    private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
        UT unknowns = schema.getFromMessage(message);
        return schema.getSerializedSizeAsMessageSet(unknowns);
    }
}

