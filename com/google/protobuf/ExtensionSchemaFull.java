/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.BinaryReader;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.ExtensionSchema;
import com.google.protobuf.FieldSet;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyField;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Protobuf;
import com.google.protobuf.Reader;
import com.google.protobuf.SchemaUtil;
import com.google.protobuf.UnknownFieldSchema;
import com.google.protobuf.UnsafeUtil;
import com.google.protobuf.WireFormat;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class ExtensionSchemaFull
extends ExtensionSchema<Descriptors.FieldDescriptor> {
    private static final long EXTENSION_FIELD_OFFSET = ExtensionSchemaFull.getExtensionsFieldOffset();

    ExtensionSchemaFull() {
    }

    private static <T> long getExtensionsFieldOffset() {
        try {
            Field field = GeneratedMessage.ExtendableMessage.class.getDeclaredField("extensions");
            return UnsafeUtil.objectFieldOffset(field);
        }
        catch (Throwable e) {
            throw new IllegalStateException("Unable to lookup extension field offset", e);
        }
    }

    @Override
    boolean hasExtensions(MessageLite prototype) {
        return prototype instanceof GeneratedMessage.ExtendableMessage;
    }

    @Override
    public FieldSet<Descriptors.FieldDescriptor> getExtensions(Object message) {
        return (FieldSet)UnsafeUtil.getObject(message, EXTENSION_FIELD_OFFSET);
    }

    @Override
    void setExtensions(Object message, FieldSet<Descriptors.FieldDescriptor> extensions) {
        UnsafeUtil.putObject(message, EXTENSION_FIELD_OFFSET, extensions);
    }

    @Override
    FieldSet<Descriptors.FieldDescriptor> getMutableExtensions(Object message) {
        FieldSet<Descriptors.FieldDescriptor> extensions = this.getExtensions(message);
        if (extensions.isImmutable()) {
            extensions = extensions.clone();
            this.setExtensions(message, extensions);
        }
        return extensions;
    }

    @Override
    void makeImmutable(Object message) {
        this.getExtensions(message).makeImmutable();
    }

    @Override
    <UT, UB> UB parseExtension(Object containerMessage, Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<Descriptors.FieldDescriptor> extensions, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) throws IOException {
        ExtensionRegistry.ExtensionInfo extension = (ExtensionRegistry.ExtensionInfo)extensionObject;
        int fieldNumber = extension.descriptor.getNumber();
        if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
            ArrayList<Object> value = null;
            switch (extension.descriptor.getLiteType()) {
                case DOUBLE: {
                    ArrayList<Double> list = new ArrayList<Double>();
                    reader.readDoubleList(list);
                    value = list;
                    break;
                }
                case FLOAT: {
                    ArrayList<Float> list = new ArrayList<Float>();
                    reader.readFloatList(list);
                    value = list;
                    break;
                }
                case INT64: {
                    ArrayList<Long> list = new ArrayList<Long>();
                    reader.readInt64List(list);
                    value = list;
                    break;
                }
                case UINT64: {
                    ArrayList<Long> list = new ArrayList<Long>();
                    reader.readUInt64List(list);
                    value = list;
                    break;
                }
                case INT32: {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    reader.readInt32List(list);
                    value = list;
                    break;
                }
                case FIXED64: {
                    ArrayList<Long> list = new ArrayList<Long>();
                    reader.readFixed64List(list);
                    value = list;
                    break;
                }
                case FIXED32: {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    reader.readFixed32List(list);
                    value = list;
                    break;
                }
                case BOOL: {
                    ArrayList<Boolean> list = new ArrayList<Boolean>();
                    reader.readBoolList(list);
                    value = list;
                    break;
                }
                case UINT32: {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    reader.readUInt32List(list);
                    value = list;
                    break;
                }
                case SFIXED32: {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    reader.readSFixed32List(list);
                    value = list;
                    break;
                }
                case SFIXED64: {
                    ArrayList<Long> list = new ArrayList<Long>();
                    reader.readSFixed64List(list);
                    value = list;
                    break;
                }
                case SINT32: {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    reader.readSInt32List(list);
                    value = list;
                    break;
                }
                case SINT64: {
                    ArrayList<Long> list = new ArrayList<Long>();
                    reader.readSInt64List(list);
                    value = list;
                    break;
                }
                case ENUM: {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    reader.readEnumList(list);
                    ArrayList<Descriptors.EnumValueDescriptor> enumList = new ArrayList<Descriptors.EnumValueDescriptor>();
                    Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        int number = (Integer)iterator.next();
                        Descriptors.EnumValueDescriptor enumDescriptor = extension.descriptor.getEnumType().findValueByNumber(number);
                        if (enumDescriptor != null) {
                            enumList.add(enumDescriptor);
                            continue;
                        }
                        unknownFields = SchemaUtil.storeUnknownEnum(containerMessage, fieldNumber, number, unknownFields, unknownFieldSchema);
                    }
                    value = enumList;
                    break;
                }
                default: {
                    throw new IllegalStateException("Type cannot be packed: " + (Object)((Object)extension.descriptor.getLiteType()));
                }
            }
            extensions.setField(extension.descriptor, value);
        } else {
            Object value = null;
            if (extension.descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
                int number = reader.readInt32();
                Descriptors.EnumValueDescriptor enumValue = extension.descriptor.getEnumType().findValueByNumber(number);
                if (enumValue == null) {
                    return SchemaUtil.storeUnknownEnum(containerMessage, fieldNumber, number, unknownFields, unknownFieldSchema);
                }
                value = enumValue;
            } else {
                switch (extension.descriptor.getLiteType()) {
                    case DOUBLE: {
                        value = reader.readDouble();
                        break;
                    }
                    case FLOAT: {
                        value = Float.valueOf(reader.readFloat());
                        break;
                    }
                    case INT64: {
                        value = reader.readInt64();
                        break;
                    }
                    case UINT64: {
                        value = reader.readUInt64();
                        break;
                    }
                    case INT32: {
                        value = reader.readInt32();
                        break;
                    }
                    case FIXED64: {
                        value = reader.readFixed64();
                        break;
                    }
                    case FIXED32: {
                        value = reader.readFixed32();
                        break;
                    }
                    case BOOL: {
                        value = reader.readBool();
                        break;
                    }
                    case BYTES: {
                        value = reader.readBytes();
                        break;
                    }
                    case UINT32: {
                        value = reader.readUInt32();
                        break;
                    }
                    case SFIXED32: {
                        value = reader.readSFixed32();
                        break;
                    }
                    case SFIXED64: {
                        value = reader.readSFixed64();
                        break;
                    }
                    case SINT32: {
                        value = reader.readSInt32();
                        break;
                    }
                    case SINT64: {
                        value = reader.readSInt64();
                        break;
                    }
                    case STRING: {
                        value = reader.readString();
                        break;
                    }
                    case GROUP: {
                        value = reader.readGroup(extension.defaultInstance.getClass(), extensionRegistry);
                        break;
                    }
                    case MESSAGE: {
                        value = reader.readMessage(extension.defaultInstance.getClass(), extensionRegistry);
                        break;
                    }
                    case ENUM: {
                        throw new IllegalStateException("Shouldn't reach here.");
                    }
                }
            }
            if (extension.descriptor.isRepeated()) {
                extensions.addRepeatedField(extension.descriptor, value);
            } else {
                switch (extension.descriptor.getLiteType()) {
                    case GROUP: 
                    case MESSAGE: {
                        Object oldValue = extensions.getField(extension.descriptor);
                        if (oldValue == null) break;
                        value = Internal.mergeMessage(oldValue, value);
                        break;
                    }
                }
                extensions.setField(extension.descriptor, value);
            }
        }
        return unknownFields;
    }

    @Override
    int extensionNumber(Map.Entry<?, ?> extension) {
        Descriptors.FieldDescriptor descriptor = (Descriptors.FieldDescriptor)extension.getKey();
        return descriptor.getNumber();
    }

    @Override
    void serializeExtension(Writer writer, Map.Entry<?, ?> extension) throws IOException {
        Descriptors.FieldDescriptor descriptor = (Descriptors.FieldDescriptor)extension.getKey();
        if (descriptor.isRepeated()) {
            switch (descriptor.getLiteType()) {
                case DOUBLE: {
                    SchemaUtil.writeDoubleList(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case FLOAT: {
                    SchemaUtil.writeFloatList(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case INT64: {
                    SchemaUtil.writeInt64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case UINT64: {
                    SchemaUtil.writeUInt64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case INT32: {
                    SchemaUtil.writeInt32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case FIXED64: {
                    SchemaUtil.writeFixed64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case FIXED32: {
                    SchemaUtil.writeFixed32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case BOOL: {
                    SchemaUtil.writeBoolList(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case BYTES: {
                    SchemaUtil.writeBytesList(descriptor.getNumber(), (List)extension.getValue(), writer);
                    break;
                }
                case UINT32: {
                    SchemaUtil.writeUInt32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case SFIXED32: {
                    SchemaUtil.writeSFixed32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case SFIXED64: {
                    SchemaUtil.writeSFixed64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case SINT32: {
                    SchemaUtil.writeSInt32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case SINT64: {
                    SchemaUtil.writeSInt64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                    break;
                }
                case ENUM: {
                    List enumList = (List)extension.getValue();
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    for (Descriptors.EnumValueDescriptor d : enumList) {
                        list.add(d.getNumber());
                    }
                    SchemaUtil.writeInt32List(descriptor.getNumber(), list, writer, descriptor.isPacked());
                    break;
                }
                case STRING: {
                    SchemaUtil.writeStringList(descriptor.getNumber(), (List)extension.getValue(), writer);
                    break;
                }
                case GROUP: {
                    SchemaUtil.writeGroupList(descriptor.getNumber(), (List)extension.getValue(), writer);
                    break;
                }
                case MESSAGE: {
                    SchemaUtil.writeMessageList(descriptor.getNumber(), (List)extension.getValue(), writer);
                }
            }
        } else {
            switch (descriptor.getLiteType()) {
                case DOUBLE: {
                    writer.writeDouble(descriptor.getNumber(), (Double)extension.getValue());
                    break;
                }
                case FLOAT: {
                    writer.writeFloat(descriptor.getNumber(), ((Float)extension.getValue()).floatValue());
                    break;
                }
                case INT64: {
                    writer.writeInt64(descriptor.getNumber(), (Long)extension.getValue());
                    break;
                }
                case UINT64: {
                    writer.writeUInt64(descriptor.getNumber(), (Long)extension.getValue());
                    break;
                }
                case INT32: {
                    writer.writeInt32(descriptor.getNumber(), (Integer)extension.getValue());
                    break;
                }
                case FIXED64: {
                    writer.writeFixed64(descriptor.getNumber(), (Long)extension.getValue());
                    break;
                }
                case FIXED32: {
                    writer.writeFixed32(descriptor.getNumber(), (Integer)extension.getValue());
                    break;
                }
                case BOOL: {
                    writer.writeBool(descriptor.getNumber(), (Boolean)extension.getValue());
                    break;
                }
                case BYTES: {
                    writer.writeBytes(descriptor.getNumber(), (ByteString)extension.getValue());
                    break;
                }
                case UINT32: {
                    writer.writeUInt32(descriptor.getNumber(), (Integer)extension.getValue());
                    break;
                }
                case SFIXED32: {
                    writer.writeSFixed32(descriptor.getNumber(), (Integer)extension.getValue());
                    break;
                }
                case SFIXED64: {
                    writer.writeSFixed64(descriptor.getNumber(), (Long)extension.getValue());
                    break;
                }
                case SINT32: {
                    writer.writeSInt32(descriptor.getNumber(), (Integer)extension.getValue());
                    break;
                }
                case SINT64: {
                    writer.writeSInt64(descriptor.getNumber(), (Long)extension.getValue());
                    break;
                }
                case ENUM: {
                    writer.writeInt32(descriptor.getNumber(), ((Descriptors.EnumValueDescriptor)extension.getValue()).getNumber());
                    break;
                }
                case STRING: {
                    writer.writeString(descriptor.getNumber(), (String)extension.getValue());
                    break;
                }
                case GROUP: {
                    writer.writeGroup(descriptor.getNumber(), extension.getValue());
                    break;
                }
                case MESSAGE: {
                    writer.writeMessage(descriptor.getNumber(), extension.getValue());
                }
            }
        }
    }

    @Override
    Object findExtensionByNumber(ExtensionRegistryLite extensionRegistry, MessageLite defaultInstance, int number) {
        return ((ExtensionRegistry)extensionRegistry).findImmutableExtensionByNumber(((Message)defaultInstance).getDescriptorForType(), number);
    }

    @Override
    void parseLengthPrefixedMessageSetItem(Reader reader, Object extension, ExtensionRegistryLite extensionRegistry, FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
        ExtensionRegistry.ExtensionInfo extensionInfo = (ExtensionRegistry.ExtensionInfo)extension;
        if (ExtensionRegistryLite.isEagerlyParseMessageSets()) {
            Object value = reader.readMessage(extensionInfo.defaultInstance.getClass(), extensionRegistry);
            extensions.setField(extensionInfo.descriptor, value);
        } else {
            extensions.setField(extensionInfo.descriptor, new LazyField(extensionInfo.defaultInstance, extensionRegistry, reader.readBytes()));
        }
    }

    @Override
    void parseMessageSetItem(ByteString data, Object extension, ExtensionRegistryLite extensionRegistry, FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
        ExtensionRegistry.ExtensionInfo extensionInfo = (ExtensionRegistry.ExtensionInfo)extension;
        Message value = extensionInfo.defaultInstance.newBuilderForType().buildPartial();
        if (ExtensionRegistryLite.isEagerlyParseMessageSets()) {
            BinaryReader reader = BinaryReader.newInstance(ByteBuffer.wrap(data.toByteArray()), true);
            Protobuf.getInstance().mergeFrom(value, reader, extensionRegistry);
            extensions.setField(extensionInfo.descriptor, value);
            if (reader.getFieldNumber() != Integer.MAX_VALUE) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        } else {
            extensions.setField(extensionInfo.descriptor, new LazyField(extensionInfo.defaultInstance, extensionRegistry, data));
        }
    }
}

