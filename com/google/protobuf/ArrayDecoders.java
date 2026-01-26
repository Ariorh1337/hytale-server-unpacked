/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.BooleanArrayList;
import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.DoubleArrayList;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.FloatArrayList;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.IntArrayList;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LongArrayList;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageSchema;
import com.google.protobuf.Protobuf;
import com.google.protobuf.Schema;
import com.google.protobuf.SchemaUtil;
import com.google.protobuf.UnknownFieldSchema;
import com.google.protobuf.UnknownFieldSetLite;
import com.google.protobuf.Utf8;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.List;

@CheckReturnValue
final class ArrayDecoders {
    static final int DEFAULT_RECURSION_LIMIT = 100;
    private static volatile int recursionLimit = 100;

    private ArrayDecoders() {
    }

    static int decodeVarint32(byte[] data, int position, Registers registers) {
        int value;
        if ((value = data[position++]) >= 0) {
            registers.int1 = value;
            return position;
        }
        return ArrayDecoders.decodeVarint32(value, data, position, registers);
    }

    static int decodeVarint32(int firstByte, byte[] data, int position, Registers registers) {
        byte b5;
        byte b4;
        byte b3;
        byte b2;
        int value = firstByte & 0x7F;
        if ((b2 = data[position++]) >= 0) {
            registers.int1 = value | b2 << 7;
            return position;
        }
        value |= (b2 & 0x7F) << 7;
        if ((b3 = data[position++]) >= 0) {
            registers.int1 = value | b3 << 14;
            return position;
        }
        value |= (b3 & 0x7F) << 14;
        if ((b4 = data[position++]) >= 0) {
            registers.int1 = value | b4 << 21;
            return position;
        }
        value |= (b4 & 0x7F) << 21;
        if ((b5 = data[position++]) >= 0) {
            registers.int1 = value | b5 << 28;
            return position;
        }
        value |= (b5 & 0x7F) << 28;
        while (data[position++] < 0) {
        }
        registers.int1 = value;
        return position;
    }

    static int decodeVarint64(byte[] data, int position, Registers registers) {
        long value;
        if ((value = (long)data[position++]) >= 0L) {
            registers.long1 = value;
            return position;
        }
        return ArrayDecoders.decodeVarint64(value, data, position, registers);
    }

    static int decodeVarint64(long firstByte, byte[] data, int position, Registers registers) {
        long value = firstByte & 0x7FL;
        byte next = data[position++];
        int shift = 7;
        value |= (long)(next & 0x7F) << 7;
        while (next < 0) {
            next = data[position++];
            value |= (long)(next & 0x7F) << (shift += 7);
        }
        registers.long1 = value;
        return position;
    }

    static int decodeFixed32(byte[] data, int position) {
        return data[position] & 0xFF | (data[position + 1] & 0xFF) << 8 | (data[position + 2] & 0xFF) << 16 | (data[position + 3] & 0xFF) << 24;
    }

    static long decodeFixed64(byte[] data, int position) {
        return (long)data[position] & 0xFFL | ((long)data[position + 1] & 0xFFL) << 8 | ((long)data[position + 2] & 0xFFL) << 16 | ((long)data[position + 3] & 0xFFL) << 24 | ((long)data[position + 4] & 0xFFL) << 32 | ((long)data[position + 5] & 0xFFL) << 40 | ((long)data[position + 6] & 0xFFL) << 48 | ((long)data[position + 7] & 0xFFL) << 56;
    }

    static double decodeDouble(byte[] data, int position) {
        return Double.longBitsToDouble(ArrayDecoders.decodeFixed64(data, position));
    }

    static float decodeFloat(byte[] data, int position) {
        return Float.intBitsToFloat(ArrayDecoders.decodeFixed32(data, position));
    }

    static int decodeString(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length == 0) {
            registers.object1 = "";
            return position;
        }
        registers.object1 = new String(data, position, length, Internal.UTF_8);
        return position + length;
    }

    static int decodeStringRequireUtf8(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length == 0) {
            registers.object1 = "";
            return position;
        }
        registers.object1 = Utf8.decodeUtf8(data, position, length);
        return position + length;
    }

    static int decodeBytes(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length > data.length - position) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        if (length == 0) {
            registers.object1 = ByteString.EMPTY;
            return position;
        }
        registers.object1 = ByteString.copyFrom(data, position, length);
        return position + length;
    }

    static <T> int decodeMessageField(Schema<T> schema, byte[] data, int position, int limit, Registers registers) throws IOException {
        T msg = schema.newInstance();
        int offset = ArrayDecoders.mergeMessageField(msg, schema, data, position, limit, registers);
        schema.makeImmutable(msg);
        registers.object1 = msg;
        return offset;
    }

    static <T> int decodeGroupField(Schema<T> schema, byte[] data, int position, int limit, int endGroup, Registers registers) throws IOException {
        T msg = schema.newInstance();
        int offset = ArrayDecoders.mergeGroupField(msg, schema, data, position, limit, endGroup, registers);
        schema.makeImmutable(msg);
        registers.object1 = msg;
        return offset;
    }

    static <T> int mergeMessageField(Object msg, Schema<T> schema, byte[] data, int position, int limit, Registers registers) throws IOException {
        int length;
        if ((length = data[position++]) < 0) {
            position = ArrayDecoders.decodeVarint32(length, data, position, registers);
            length = registers.int1;
        }
        if (length < 0 || length > limit - position) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        ++registers.recursionDepth;
        ArrayDecoders.checkRecursionLimit(registers.recursionDepth);
        schema.mergeFrom(msg, data, position, position + length, registers);
        --registers.recursionDepth;
        registers.object1 = msg;
        return position + length;
    }

    static <T> int mergeGroupField(Object msg, Schema<T> schema, byte[] data, int position, int limit, int endGroup, Registers registers) throws IOException {
        MessageSchema messageSchema = (MessageSchema)schema;
        ++registers.recursionDepth;
        ArrayDecoders.checkRecursionLimit(registers.recursionDepth);
        int endPosition = messageSchema.parseMessage(msg, data, position, limit, endGroup, registers);
        --registers.recursionDepth;
        registers.object1 = msg;
        return endPosition;
    }

    static int decodeVarint32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        IntArrayList output = (IntArrayList)list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        output.addInt(registers.int1);
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeVarint32(data, nextPosition, registers);
            output.addInt(registers.int1);
        }
        return position;
    }

    static int decodeVarint64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        LongArrayList output = (LongArrayList)list;
        position = ArrayDecoders.decodeVarint64(data, position, registers);
        output.addLong(registers.long1);
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeVarint64(data, nextPosition, registers);
            output.addLong(registers.long1);
        }
        return position;
    }

    static int decodeFixed32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        IntArrayList output = (IntArrayList)list;
        output.addInt(ArrayDecoders.decodeFixed32(data, position));
        position += 4;
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            output.addInt(ArrayDecoders.decodeFixed32(data, nextPosition));
            position = nextPosition + 4;
        }
        return position;
    }

    static int decodeFixed64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        LongArrayList output = (LongArrayList)list;
        output.addLong(ArrayDecoders.decodeFixed64(data, position));
        position += 8;
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            output.addLong(ArrayDecoders.decodeFixed64(data, nextPosition));
            position = nextPosition + 8;
        }
        return position;
    }

    static int decodeFloatList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        FloatArrayList output = (FloatArrayList)list;
        output.addFloat(ArrayDecoders.decodeFloat(data, position));
        position += 4;
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            output.addFloat(ArrayDecoders.decodeFloat(data, nextPosition));
            position = nextPosition + 4;
        }
        return position;
    }

    static int decodeDoubleList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        DoubleArrayList output = (DoubleArrayList)list;
        output.addDouble(ArrayDecoders.decodeDouble(data, position));
        position += 8;
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            output.addDouble(ArrayDecoders.decodeDouble(data, nextPosition));
            position = nextPosition + 8;
        }
        return position;
    }

    static int decodeBoolList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        BooleanArrayList output = (BooleanArrayList)list;
        position = ArrayDecoders.decodeVarint64(data, position, registers);
        output.addBoolean(registers.long1 != 0L);
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeVarint64(data, nextPosition, registers);
            output.addBoolean(registers.long1 != 0L);
        }
        return position;
    }

    static int decodeSInt32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        IntArrayList output = (IntArrayList)list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeVarint32(data, nextPosition, registers);
            output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
        }
        return position;
    }

    static int decodeSInt64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
        LongArrayList output = (LongArrayList)list;
        position = ArrayDecoders.decodeVarint64(data, position, registers);
        output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeVarint64(data, nextPosition, registers);
            output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
        }
        return position;
    }

    static int decodePackedVarint32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        IntArrayList output = (IntArrayList)list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int fieldLimit = position + registers.int1;
        while (position < fieldLimit) {
            position = ArrayDecoders.decodeVarint32(data, position, registers);
            output.addInt(registers.int1);
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodePackedVarint64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        LongArrayList output = (LongArrayList)list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int fieldLimit = position + registers.int1;
        while (position < fieldLimit) {
            position = ArrayDecoders.decodeVarint64(data, position, registers);
            output.addLong(registers.long1);
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodePackedFixed32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        int packedDataByteSize;
        IntArrayList output = (IntArrayList)list;
        int fieldLimit = (position = ArrayDecoders.decodeVarint32(data, position, registers)) + (packedDataByteSize = registers.int1);
        if (fieldLimit > data.length) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        output.ensureCapacity(output.size() + packedDataByteSize / 4);
        while (position < fieldLimit) {
            output.addInt(ArrayDecoders.decodeFixed32(data, position));
            position += 4;
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodePackedFixed64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        int packedDataByteSize;
        LongArrayList output = (LongArrayList)list;
        int fieldLimit = (position = ArrayDecoders.decodeVarint32(data, position, registers)) + (packedDataByteSize = registers.int1);
        if (fieldLimit > data.length) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        output.ensureCapacity(output.size() + packedDataByteSize / 8);
        while (position < fieldLimit) {
            output.addLong(ArrayDecoders.decodeFixed64(data, position));
            position += 8;
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodePackedFloatList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        int packedDataByteSize;
        FloatArrayList output = (FloatArrayList)list;
        int fieldLimit = (position = ArrayDecoders.decodeVarint32(data, position, registers)) + (packedDataByteSize = registers.int1);
        if (fieldLimit > data.length) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        output.ensureCapacity(output.size() + packedDataByteSize / 4);
        while (position < fieldLimit) {
            output.addFloat(ArrayDecoders.decodeFloat(data, position));
            position += 4;
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodePackedDoubleList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        int packedDataByteSize;
        DoubleArrayList output = (DoubleArrayList)list;
        int fieldLimit = (position = ArrayDecoders.decodeVarint32(data, position, registers)) + (packedDataByteSize = registers.int1);
        if (fieldLimit > data.length) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        output.ensureCapacity(output.size() + packedDataByteSize / 8);
        while (position < fieldLimit) {
            output.addDouble(ArrayDecoders.decodeDouble(data, position));
            position += 8;
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodePackedBoolList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        BooleanArrayList output = (BooleanArrayList)list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int fieldLimit = position + registers.int1;
        while (position < fieldLimit) {
            position = ArrayDecoders.decodeVarint64(data, position, registers);
            output.addBoolean(registers.long1 != 0L);
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodePackedSInt32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        IntArrayList output = (IntArrayList)list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int fieldLimit = position + registers.int1;
        while (position < fieldLimit) {
            position = ArrayDecoders.decodeVarint32(data, position, registers);
            output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodePackedSInt64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        LongArrayList output = (LongArrayList)list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int fieldLimit = position + registers.int1;
        while (position < fieldLimit) {
            position = ArrayDecoders.decodeVarint64(data, position, registers);
            output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
        }
        if (position != fieldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return position;
    }

    static int decodeStringList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        Internal.ProtobufList<?> output = list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length == 0) {
            output.add("");
        } else {
            String value = new String(data, position, length, Internal.UTF_8);
            output.add(value);
            position += length;
        }
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if (nextLength == 0) {
                output.add("");
                continue;
            }
            String value = new String(data, position, nextLength, Internal.UTF_8);
            output.add(value);
            position += nextLength;
        }
        return position;
    }

    static int decodeStringListRequireUtf8(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        Internal.ProtobufList<?> output = list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length == 0) {
            output.add("");
        } else {
            if (!Utf8.isValidUtf8(data, position, position + length)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            String value = new String(data, position, length, Internal.UTF_8);
            output.add(value);
            position += length;
        }
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if (nextLength == 0) {
                output.add("");
                continue;
            }
            if (!Utf8.isValidUtf8(data, position, position + nextLength)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            String value = new String(data, position, nextLength, Internal.UTF_8);
            output.add(value);
            position += nextLength;
        }
        return position;
    }

    static int decodeBytesList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
        Internal.ProtobufList<?> output = list;
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (length > data.length - position) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        if (length == 0) {
            output.add(ByteString.EMPTY);
        } else {
            output.add(ByteString.copyFrom(data, position, length));
            position += length;
        }
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeVarint32(data, nextPosition, registers);
            int nextLength = registers.int1;
            if (nextLength < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if (nextLength > data.length - position) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            if (nextLength == 0) {
                output.add(ByteString.EMPTY);
                continue;
            }
            output.add(ByteString.copyFrom(data, position, nextLength));
            position += nextLength;
        }
        return position;
    }

    static int decodeMessageList(Schema<?> schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws IOException {
        Internal.ProtobufList<?> output = list;
        position = ArrayDecoders.decodeMessageField(schema, data, position, limit, registers);
        output.add(registers.object1);
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeMessageField(schema, data, nextPosition, limit, registers);
            output.add(registers.object1);
        }
        return position;
    }

    static int decodeGroupList(Schema<?> schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<Object> output, Registers registers) throws IOException {
        int endgroup = tag & 0xFFFFFFF8 | 4;
        position = ArrayDecoders.decodeGroupField(schema, data, position, limit, endgroup, registers);
        output.add(registers.object1);
        while (position < limit) {
            int nextPosition = ArrayDecoders.decodeVarint32(data, position, registers);
            if (tag != registers.int1) break;
            position = ArrayDecoders.decodeGroupField(schema, data, nextPosition, limit, endgroup, registers);
            output.add(registers.object1);
        }
        return position;
    }

    static int decodeExtensionOrUnknownField(int tag, byte[] data, int position, int limit, Object message, MessageLite defaultInstance, UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema, Registers registers) throws IOException {
        int number = tag >>> 3;
        GeneratedMessageLite.GeneratedExtension<MessageLite, ?> extension = registers.extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
        if (extension == null) {
            return ArrayDecoders.decodeUnknownField(tag, data, position, limit, MessageSchema.getMutableUnknownFields(message), registers);
        }
        FieldSet<GeneratedMessageLite.ExtensionDescriptor> unused = ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
        return ArrayDecoders.decodeExtension(tag, data, position, limit, (GeneratedMessageLite.ExtendableMessage)message, extension, unknownFieldSchema, registers);
    }

    static int decodeExtension(int tag, byte[] data, int position, int limit, GeneratedMessageLite.ExtendableMessage<?, ?> message, GeneratedMessageLite.GeneratedExtension<?, ?> extension, UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema, Registers registers) throws IOException {
        block40: {
            int fieldNumber;
            FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions;
            block39: {
                extensions = message.extensions;
                fieldNumber = tag >>> 3;
                if (!extension.descriptor.isRepeated() || !extension.descriptor.isPacked()) break block39;
                switch (extension.getLiteType()) {
                    case DOUBLE: {
                        DoubleArrayList list = new DoubleArrayList();
                        position = ArrayDecoders.decodePackedDoubleList(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case FLOAT: {
                        FloatArrayList list = new FloatArrayList();
                        position = ArrayDecoders.decodePackedFloatList(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case INT64: 
                    case UINT64: {
                        LongArrayList list = new LongArrayList();
                        position = ArrayDecoders.decodePackedVarint64List(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case INT32: 
                    case UINT32: {
                        IntArrayList list = new IntArrayList();
                        position = ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case FIXED64: 
                    case SFIXED64: {
                        LongArrayList list = new LongArrayList();
                        position = ArrayDecoders.decodePackedFixed64List(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case FIXED32: 
                    case SFIXED32: {
                        IntArrayList list = new IntArrayList();
                        position = ArrayDecoders.decodePackedFixed32List(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case BOOL: {
                        BooleanArrayList list = new BooleanArrayList();
                        position = ArrayDecoders.decodePackedBoolList(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case SINT32: {
                        IntArrayList list = new IntArrayList();
                        position = ArrayDecoders.decodePackedSInt32List(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case SINT64: {
                        LongArrayList list = new LongArrayList();
                        position = ArrayDecoders.decodePackedSInt64List(data, position, list, registers);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    case ENUM: {
                        IntArrayList list = new IntArrayList();
                        position = ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
                        SchemaUtil.filterUnknownEnumList(message, fieldNumber, (List<Integer>)list, extension.descriptor.getEnumType(), null, unknownFieldSchema);
                        extensions.setField(extension.descriptor, list);
                        break block40;
                    }
                    default: {
                        throw new IllegalStateException("Type cannot be packed: " + (Object)((Object)extension.descriptor.getLiteType()));
                    }
                }
            }
            Object value = null;
            if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                Object enumValue = extension.descriptor.getEnumType().findValueByNumber(registers.int1);
                if (enumValue == null) {
                    SchemaUtil.storeUnknownEnum(message, fieldNumber, registers.int1, null, unknownFieldSchema);
                    return position;
                }
                value = registers.int1;
            } else {
                switch (extension.getLiteType()) {
                    case DOUBLE: {
                        value = ArrayDecoders.decodeDouble(data, position);
                        position += 8;
                        break;
                    }
                    case FLOAT: {
                        value = Float.valueOf(ArrayDecoders.decodeFloat(data, position));
                        position += 4;
                        break;
                    }
                    case INT64: 
                    case UINT64: {
                        position = ArrayDecoders.decodeVarint64(data, position, registers);
                        value = registers.long1;
                        break;
                    }
                    case INT32: 
                    case UINT32: {
                        position = ArrayDecoders.decodeVarint32(data, position, registers);
                        value = registers.int1;
                        break;
                    }
                    case FIXED64: 
                    case SFIXED64: {
                        value = ArrayDecoders.decodeFixed64(data, position);
                        position += 8;
                        break;
                    }
                    case FIXED32: 
                    case SFIXED32: {
                        value = ArrayDecoders.decodeFixed32(data, position);
                        position += 4;
                        break;
                    }
                    case BOOL: {
                        position = ArrayDecoders.decodeVarint64(data, position, registers);
                        value = registers.long1 != 0L;
                        break;
                    }
                    case BYTES: {
                        position = ArrayDecoders.decodeBytes(data, position, registers);
                        value = registers.object1;
                        break;
                    }
                    case SINT32: {
                        position = ArrayDecoders.decodeVarint32(data, position, registers);
                        value = CodedInputStream.decodeZigZag32(registers.int1);
                        break;
                    }
                    case SINT64: {
                        position = ArrayDecoders.decodeVarint64(data, position, registers);
                        value = CodedInputStream.decodeZigZag64(registers.long1);
                        break;
                    }
                    case STRING: {
                        position = ArrayDecoders.decodeString(data, position, registers);
                        value = registers.object1;
                        break;
                    }
                    case GROUP: {
                        int endTag = fieldNumber << 3 | 4;
                        Schema<Class<?>> fieldSchema = Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass());
                        if (extension.isRepeated()) {
                            position = ArrayDecoders.decodeGroupField(fieldSchema, data, position, limit, endTag, registers);
                            extensions.addRepeatedField(extension.descriptor, registers.object1);
                        } else {
                            Class<?> oldValue = extensions.getField(extension.descriptor);
                            if (oldValue == null) {
                                oldValue = fieldSchema.newInstance();
                                extensions.setField(extension.descriptor, oldValue);
                            }
                            position = ArrayDecoders.mergeGroupField(oldValue, fieldSchema, data, position, limit, endTag, registers);
                        }
                        return position;
                    }
                    case MESSAGE: {
                        Schema<Class<?>> fieldSchema = Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass());
                        if (extension.isRepeated()) {
                            position = ArrayDecoders.decodeMessageField(fieldSchema, data, position, limit, registers);
                            extensions.addRepeatedField(extension.descriptor, registers.object1);
                        } else {
                            Class<?> oldValue = extensions.getField(extension.descriptor);
                            if (oldValue == null) {
                                oldValue = fieldSchema.newInstance();
                                extensions.setField(extension.descriptor, oldValue);
                            }
                            position = ArrayDecoders.mergeMessageField(oldValue, fieldSchema, data, position, limit, registers);
                        }
                        return position;
                    }
                    case ENUM: {
                        throw new IllegalStateException("Shouldn't reach here.");
                    }
                }
            }
            if (extension.isRepeated()) {
                extensions.addRepeatedField(extension.descriptor, value);
            } else {
                extensions.setField(extension.descriptor, value);
            }
        }
        return position;
    }

    static int decodeUnknownField(int tag, byte[] data, int position, int limit, UnknownFieldSetLite unknownFields, Registers registers) throws InvalidProtocolBufferException {
        if (WireFormat.getTagFieldNumber(tag) == 0) {
            throw InvalidProtocolBufferException.invalidTag();
        }
        switch (WireFormat.getTagWireType(tag)) {
            case 0: {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                unknownFields.storeField(tag, registers.long1);
                return position;
            }
            case 5: {
                unknownFields.storeField(tag, ArrayDecoders.decodeFixed32(data, position));
                return position + 4;
            }
            case 1: {
                unknownFields.storeField(tag, ArrayDecoders.decodeFixed64(data, position));
                return position + 8;
            }
            case 2: {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                int length = registers.int1;
                if (length < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                if (length > data.length - position) {
                    throw InvalidProtocolBufferException.truncatedMessage();
                }
                if (length == 0) {
                    unknownFields.storeField(tag, ByteString.EMPTY);
                } else {
                    unknownFields.storeField(tag, ByteString.copyFrom(data, position, length));
                }
                return position + length;
            }
            case 3: {
                UnknownFieldSetLite child = UnknownFieldSetLite.newInstance();
                int endGroup = tag & 0xFFFFFFF8 | 4;
                int lastTag = 0;
                ++registers.recursionDepth;
                ArrayDecoders.checkRecursionLimit(registers.recursionDepth);
                while (position < limit) {
                    position = ArrayDecoders.decodeVarint32(data, position, registers);
                    lastTag = registers.int1;
                    if (lastTag == endGroup) break;
                    position = ArrayDecoders.decodeUnknownField(lastTag, data, position, limit, child, registers);
                }
                --registers.recursionDepth;
                if (position > limit || lastTag != endGroup) {
                    throw InvalidProtocolBufferException.parseFailure();
                }
                unknownFields.storeField(tag, child);
                return position;
            }
        }
        throw InvalidProtocolBufferException.invalidTag();
    }

    static int skipField(int tag, byte[] data, int position, int limit, Registers registers) throws InvalidProtocolBufferException {
        if (WireFormat.getTagFieldNumber(tag) == 0) {
            throw InvalidProtocolBufferException.invalidTag();
        }
        switch (WireFormat.getTagWireType(tag)) {
            case 0: {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                return position;
            }
            case 5: {
                return position + 4;
            }
            case 1: {
                return position + 8;
            }
            case 2: {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                return position + registers.int1;
            }
            case 3: {
                int endGroup = tag & 0xFFFFFFF8 | 4;
                int lastTag = 0;
                while (position < limit) {
                    position = ArrayDecoders.decodeVarint32(data, position, registers);
                    lastTag = registers.int1;
                    if (lastTag == endGroup) break;
                    position = ArrayDecoders.skipField(lastTag, data, position, limit, registers);
                }
                if (position > limit || lastTag != endGroup) {
                    throw InvalidProtocolBufferException.parseFailure();
                }
                return position;
            }
        }
        throw InvalidProtocolBufferException.invalidTag();
    }

    public static void setRecursionLimit(int limit) {
        recursionLimit = limit;
    }

    private static void checkRecursionLimit(int depth) throws InvalidProtocolBufferException {
        if (depth >= recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
        }
    }

    static final class Registers {
        public int int1;
        public long long1;
        public Object object1;
        public final ExtensionRegistryLite extensionRegistry;
        public int recursionDepth;

        Registers() {
            this.extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
        }

        Registers(ExtensionRegistryLite extensionRegistry) {
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            this.extensionRegistry = extensionRegistry;
        }
    }
}

