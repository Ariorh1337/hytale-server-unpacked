/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.ExtensionSchema;
import com.google.protobuf.FieldSet;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Protobuf;
import com.google.protobuf.Reader;
import com.google.protobuf.Schema;
import com.google.protobuf.SchemaUtil;
import com.google.protobuf.UnknownFieldSchema;
import com.google.protobuf.WireFormat;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.lang.constant.Constable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CheckReturnValue
final class ExtensionSchemaLite
extends ExtensionSchema<GeneratedMessageLite.ExtensionDescriptor> {
    ExtensionSchemaLite() {
    }

    @Override
    boolean hasExtensions(MessageLite prototype) {
        return prototype instanceof GeneratedMessageLite.ExtendableMessage;
    }

    @Override
    FieldSet<GeneratedMessageLite.ExtensionDescriptor> getExtensions(Object message) {
        return ((GeneratedMessageLite.ExtendableMessage)message).extensions;
    }

    @Override
    void setExtensions(Object message, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) {
        ((GeneratedMessageLite.ExtendableMessage)message).extensions = extensions;
    }

    @Override
    FieldSet<GeneratedMessageLite.ExtensionDescriptor> getMutableExtensions(Object message) {
        return ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
    }

    @Override
    void makeImmutable(Object message) {
        this.getExtensions(message).makeImmutable();
    }

    @Override
    <UT, UB> UB parseExtension(Object containerMessage, Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) throws IOException {
        GeneratedMessageLite.GeneratedExtension extension = (GeneratedMessageLite.GeneratedExtension)extensionObject;
        int fieldNumber = extension.getNumber();
        if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
            ArrayList<Constable> value = null;
            switch (extension.getLiteType()) {
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
                    unknownFields = SchemaUtil.filterUnknownEnumList(containerMessage, fieldNumber, list, extension.descriptor.getEnumType(), unknownFields, unknownFieldSchema);
                    value = list;
                    break;
                }
                default: {
                    throw new IllegalStateException("Type cannot be packed: " + (Object)((Object)extension.descriptor.getLiteType()));
                }
            }
            extensions.setField(extension.descriptor, value);
        } else {
            Object value = null;
            if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
                int number = reader.readInt32();
                Object enumValue = extension.descriptor.getEnumType().findValueByNumber(number);
                if (enumValue == null) {
                    return SchemaUtil.storeUnknownEnum(containerMessage, fieldNumber, number, unknownFields, unknownFieldSchema);
                }
                value = number;
            } else {
                switch (extension.getLiteType()) {
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
                        Object oldValue;
                        if (!extension.isRepeated() && (oldValue = extensions.getField(extension.descriptor)) instanceof GeneratedMessageLite) {
                            Schema<Object> extSchema = Protobuf.getInstance().schemaFor(oldValue);
                            if (!((GeneratedMessageLite)oldValue).isMutable()) {
                                Object newValue = extSchema.newInstance();
                                extSchema.mergeFrom(newValue, oldValue);
                                extensions.setField(extension.descriptor, newValue);
                                oldValue = newValue;
                            }
                            reader.mergeGroupField(oldValue, extSchema, extensionRegistry);
                            return unknownFields;
                        }
                        value = reader.readGroup(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
                        break;
                    }
                    case MESSAGE: {
                        Object oldValue;
                        if (!extension.isRepeated() && (oldValue = extensions.getField(extension.descriptor)) instanceof GeneratedMessageLite) {
                            Schema<Object> extSchema = Protobuf.getInstance().schemaFor(oldValue);
                            if (!((GeneratedMessageLite)oldValue).isMutable()) {
                                Object newValue = extSchema.newInstance();
                                extSchema.mergeFrom(newValue, oldValue);
                                extensions.setField(extension.descriptor, newValue);
                                oldValue = newValue;
                            }
                            reader.mergeMessageField(oldValue, extSchema, extensionRegistry);
                            return unknownFields;
                        }
                        value = reader.readMessage(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
                        break;
                    }
                    case ENUM: {
                        throw new IllegalStateException("Shouldn't reach here.");
                    }
                }
            }
            if (extension.isRepeated()) {
                extensions.addRepeatedField(extension.descriptor, value);
            } else {
                switch (extension.getLiteType()) {
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
        GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor)extension.getKey();
        return descriptor.getNumber();
    }

    @Override
    void serializeExtension(Writer writer, Map.Entry<?, ?> extension) throws IOException {
        block42: {
            GeneratedMessageLite.ExtensionDescriptor descriptor;
            block41: {
                descriptor = (GeneratedMessageLite.ExtensionDescriptor)extension.getKey();
                if (!descriptor.isRepeated()) break block41;
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
                        SchemaUtil.writeInt32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
                        break;
                    }
                    case STRING: {
                        SchemaUtil.writeStringList(descriptor.getNumber(), (List)extension.getValue(), writer);
                        break;
                    }
                    case GROUP: {
                        List data = (List)extension.getValue();
                        if (data != null && !data.isEmpty()) {
                            SchemaUtil.writeGroupList(descriptor.getNumber(), (List)extension.getValue(), writer, Protobuf.getInstance().schemaFor(data.get(0).getClass()));
                            break;
                        }
                        break block42;
                    }
                    case MESSAGE: {
                        List data = (List)extension.getValue();
                        if (data == null || data.isEmpty()) break;
                        SchemaUtil.writeMessageList(descriptor.getNumber(), (List)extension.getValue(), writer, Protobuf.getInstance().schemaFor(data.get(0).getClass()));
                    }
                }
                break block42;
            }
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
                    writer.writeInt32(descriptor.getNumber(), (Integer)extension.getValue());
                    break;
                }
                case STRING: {
                    writer.writeString(descriptor.getNumber(), (String)extension.getValue());
                    break;
                }
                case GROUP: {
                    writer.writeGroup(descriptor.getNumber(), extension.getValue(), Protobuf.getInstance().schemaFor(extension.getValue().getClass()));
                    break;
                }
                case MESSAGE: {
                    writer.writeMessage(descriptor.getNumber(), extension.getValue(), Protobuf.getInstance().schemaFor(extension.getValue().getClass()));
                }
            }
        }
    }

    @Override
    Object findExtensionByNumber(ExtensionRegistryLite extensionRegistry, MessageLite defaultInstance, int number) {
        return extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
    }

    @Override
    void parseLengthPrefixedMessageSetItem(Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
        GeneratedMessageLite.GeneratedExtension extension = (GeneratedMessageLite.GeneratedExtension)extensionObject;
        Object value = reader.readMessage(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
        extensions.setField(extension.descriptor, value);
    }

    @Override
    void parseMessageSetItem(ByteString data, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
        GeneratedMessageLite.GeneratedExtension extension = (GeneratedMessageLite.GeneratedExtension)extensionObject;
        MessageLite.Builder builder = extension.getMessageDefaultInstance().newBuilderForType();
        CodedInputStream input = data.newCodedInput();
        builder.mergeFrom(input, extensionRegistry);
        extensions.setField(extension.descriptor, builder.buildPartial());
        input.checkLastTagWas(0);
    }
}

