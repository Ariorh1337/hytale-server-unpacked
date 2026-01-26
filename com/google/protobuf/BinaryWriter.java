/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AllocatedBuffer;
import com.google.protobuf.BooleanArrayList;
import com.google.protobuf.BufferAllocator;
import com.google.protobuf.ByteOutput;
import com.google.protobuf.ByteString;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DoubleArrayList;
import com.google.protobuf.FloatArrayList;
import com.google.protobuf.IntArrayList;
import com.google.protobuf.Internal;
import com.google.protobuf.Java8Compatibility;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.LongArrayList;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.Protobuf;
import com.google.protobuf.Schema;
import com.google.protobuf.UnsafeUtil;
import com.google.protobuf.Utf8;
import com.google.protobuf.WireFormat;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@CheckReturnValue
abstract class BinaryWriter
extends ByteOutput
implements Writer {
    public static final int DEFAULT_CHUNK_SIZE = 4096;
    private final BufferAllocator alloc;
    private final int chunkSize;
    final ArrayDeque<AllocatedBuffer> buffers = new ArrayDeque(4);
    int totalDoneBytes;
    private static final int MAP_KEY_NUMBER = 1;
    private static final int MAP_VALUE_NUMBER = 2;

    public static BinaryWriter newHeapInstance(BufferAllocator alloc) {
        return BinaryWriter.newHeapInstance(alloc, 4096);
    }

    public static BinaryWriter newHeapInstance(BufferAllocator alloc, int chunkSize) {
        return BinaryWriter.isUnsafeHeapSupported() ? BinaryWriter.newUnsafeHeapInstance(alloc, chunkSize) : BinaryWriter.newSafeHeapInstance(alloc, chunkSize);
    }

    public static BinaryWriter newDirectInstance(BufferAllocator alloc) {
        return BinaryWriter.newDirectInstance(alloc, 4096);
    }

    public static BinaryWriter newDirectInstance(BufferAllocator alloc, int chunkSize) {
        return BinaryWriter.isUnsafeDirectSupported() ? BinaryWriter.newUnsafeDirectInstance(alloc, chunkSize) : BinaryWriter.newSafeDirectInstance(alloc, chunkSize);
    }

    static boolean isUnsafeHeapSupported() {
        return UnsafeHeapWriter.isSupported();
    }

    static boolean isUnsafeDirectSupported() {
        return UnsafeDirectWriter.isSupported();
    }

    static BinaryWriter newSafeHeapInstance(BufferAllocator alloc, int chunkSize) {
        return new SafeHeapWriter(alloc, chunkSize);
    }

    static BinaryWriter newUnsafeHeapInstance(BufferAllocator alloc, int chunkSize) {
        if (!BinaryWriter.isUnsafeHeapSupported()) {
            throw new UnsupportedOperationException("Unsafe operations not supported");
        }
        return new UnsafeHeapWriter(alloc, chunkSize);
    }

    static BinaryWriter newSafeDirectInstance(BufferAllocator alloc, int chunkSize) {
        return new SafeDirectWriter(alloc, chunkSize);
    }

    static BinaryWriter newUnsafeDirectInstance(BufferAllocator alloc, int chunkSize) {
        if (!BinaryWriter.isUnsafeDirectSupported()) {
            throw new UnsupportedOperationException("Unsafe operations not supported");
        }
        return new UnsafeDirectWriter(alloc, chunkSize);
    }

    private BinaryWriter(BufferAllocator alloc, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be > 0");
        }
        this.alloc = Internal.checkNotNull(alloc, "alloc");
        this.chunkSize = chunkSize;
    }

    @Override
    public final Writer.FieldOrder fieldOrder() {
        return Writer.FieldOrder.DESCENDING;
    }

    @CanIgnoreReturnValue
    public final Queue<AllocatedBuffer> complete() {
        this.finishCurrentBuffer();
        return this.buffers;
    }

    @Override
    public final void writeSFixed32(int fieldNumber, int value) throws IOException {
        this.writeFixed32(fieldNumber, value);
    }

    @Override
    public final void writeInt64(int fieldNumber, long value) throws IOException {
        this.writeUInt64(fieldNumber, value);
    }

    @Override
    public final void writeSFixed64(int fieldNumber, long value) throws IOException {
        this.writeFixed64(fieldNumber, value);
    }

    @Override
    public final void writeFloat(int fieldNumber, float value) throws IOException {
        this.writeFixed32(fieldNumber, Float.floatToRawIntBits(value));
    }

    @Override
    public final void writeDouble(int fieldNumber, double value) throws IOException {
        this.writeFixed64(fieldNumber, Double.doubleToRawLongBits(value));
    }

    @Override
    public final void writeEnum(int fieldNumber, int value) throws IOException {
        this.writeInt32(fieldNumber, value);
    }

    @Override
    public final void writeInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        if (list instanceof IntArrayList) {
            this.writeInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
        } else {
            this.writeInt32List_Internal(fieldNumber, list, packed);
        }
    }

    private void writeInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 10);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeInt32(list.get(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeInt32(fieldNumber, list.get(i));
            }
        }
    }

    private void writeInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 10);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeInt32(list.getInt(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeInt32(fieldNumber, list.getInt(i));
            }
        }
    }

    @Override
    public final void writeFixed32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        if (list instanceof IntArrayList) {
            this.writeFixed32List_Internal(fieldNumber, (IntArrayList)list, packed);
        } else {
            this.writeFixed32List_Internal(fieldNumber, list, packed);
        }
    }

    private void writeFixed32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 4);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed32(list.get(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed32(fieldNumber, list.get(i));
            }
        }
    }

    private void writeFixed32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 4);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed32(list.getInt(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed32(fieldNumber, list.getInt(i));
            }
        }
    }

    @Override
    public final void writeInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
        this.writeUInt64List(fieldNumber, list, packed);
    }

    @Override
    public final void writeUInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
        if (list instanceof LongArrayList) {
            this.writeUInt64List_Internal(fieldNumber, (LongArrayList)list, packed);
        } else {
            this.writeUInt64List_Internal(fieldNumber, list, packed);
        }
    }

    private void writeUInt64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 10);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeVarint64(list.get(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeUInt64(fieldNumber, list.get(i));
            }
        }
    }

    private void writeUInt64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 10);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeVarint64(list.getLong(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeUInt64(fieldNumber, list.getLong(i));
            }
        }
    }

    @Override
    public final void writeFixed64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
        if (list instanceof LongArrayList) {
            this.writeFixed64List_Internal(fieldNumber, (LongArrayList)list, packed);
        } else {
            this.writeFixed64List_Internal(fieldNumber, list, packed);
        }
    }

    private void writeFixed64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 8);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed64(list.get(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed64(fieldNumber, list.get(i));
            }
        }
    }

    private void writeFixed64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 8);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed64(list.getLong(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed64(fieldNumber, list.getLong(i));
            }
        }
    }

    @Override
    public final void writeFloatList(int fieldNumber, List<Float> list, boolean packed) throws IOException {
        if (list instanceof FloatArrayList) {
            this.writeFloatList_Internal(fieldNumber, (FloatArrayList)list, packed);
        } else {
            this.writeFloatList_Internal(fieldNumber, list, packed);
        }
    }

    private void writeFloatList_Internal(int fieldNumber, List<Float> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 4);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed32(Float.floatToRawIntBits(list.get(i).floatValue()));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFloat(fieldNumber, list.get(i).floatValue());
            }
        }
    }

    private void writeFloatList_Internal(int fieldNumber, FloatArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 4);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed32(Float.floatToRawIntBits(list.getFloat(i)));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFloat(fieldNumber, list.getFloat(i));
            }
        }
    }

    @Override
    public final void writeDoubleList(int fieldNumber, List<Double> list, boolean packed) throws IOException {
        if (list instanceof DoubleArrayList) {
            this.writeDoubleList_Internal(fieldNumber, (DoubleArrayList)list, packed);
        } else {
            this.writeDoubleList_Internal(fieldNumber, list, packed);
        }
    }

    private void writeDoubleList_Internal(int fieldNumber, List<Double> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 8);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed64(Double.doubleToRawLongBits(list.get(i)));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeDouble(fieldNumber, list.get(i));
            }
        }
    }

    private void writeDoubleList_Internal(int fieldNumber, DoubleArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 8);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeFixed64(Double.doubleToRawLongBits(list.getDouble(i)));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeDouble(fieldNumber, list.getDouble(i));
            }
        }
    }

    @Override
    public final void writeEnumList(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        this.writeInt32List(fieldNumber, list, packed);
    }

    @Override
    public final void writeBoolList(int fieldNumber, List<Boolean> list, boolean packed) throws IOException {
        if (list instanceof BooleanArrayList) {
            this.writeBoolList_Internal(fieldNumber, (BooleanArrayList)list, packed);
        } else {
            this.writeBoolList_Internal(fieldNumber, list, packed);
        }
    }

    private void writeBoolList_Internal(int fieldNumber, List<Boolean> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size());
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeBool(list.get(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeBool(fieldNumber, list.get(i));
            }
        }
    }

    private void writeBoolList_Internal(int fieldNumber, BooleanArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size());
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeBool(list.getBoolean(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeBool(fieldNumber, list.getBoolean(i));
            }
        }
    }

    @Override
    public final void writeStringList(int fieldNumber, List<String> list) throws IOException {
        if (list instanceof LazyStringList) {
            LazyStringList lazyList = (LazyStringList)list;
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeLazyString(fieldNumber, lazyList.getRaw(i));
            }
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeString(fieldNumber, list.get(i));
            }
        }
    }

    private void writeLazyString(int fieldNumber, Object value) throws IOException {
        if (value instanceof String) {
            this.writeString(fieldNumber, (String)value);
        } else {
            this.writeBytes(fieldNumber, (ByteString)value);
        }
    }

    @Override
    public final void writeBytesList(int fieldNumber, List<ByteString> list) throws IOException {
        for (int i = list.size() - 1; i >= 0; --i) {
            this.writeBytes(fieldNumber, list.get(i));
        }
    }

    @Override
    public final void writeUInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        if (list instanceof IntArrayList) {
            this.writeUInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
        } else {
            this.writeUInt32List_Internal(fieldNumber, list, packed);
        }
    }

    private void writeUInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 5);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeVarint32(list.get(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeUInt32(fieldNumber, list.get(i));
            }
        }
    }

    private void writeUInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 5);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeVarint32(list.getInt(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeUInt32(fieldNumber, list.getInt(i));
            }
        }
    }

    @Override
    public final void writeSFixed32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        this.writeFixed32List(fieldNumber, list, packed);
    }

    @Override
    public final void writeSFixed64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
        this.writeFixed64List(fieldNumber, list, packed);
    }

    @Override
    public final void writeSInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        if (list instanceof IntArrayList) {
            this.writeSInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
        } else {
            this.writeSInt32List_Internal(fieldNumber, list, packed);
        }
    }

    private void writeSInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 5);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeSInt32(list.get(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeSInt32(fieldNumber, list.get(i));
            }
        }
    }

    private void writeSInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 5);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeSInt32(list.getInt(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeSInt32(fieldNumber, list.getInt(i));
            }
        }
    }

    @Override
    public final void writeSInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
        if (list instanceof LongArrayList) {
            this.writeSInt64List_Internal(fieldNumber, (LongArrayList)list, packed);
        } else {
            this.writeSInt64List_Internal(fieldNumber, list, packed);
        }
    }

    @Override
    public <K, V> void writeMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            int prevBytes = this.getTotalBytesWritten();
            BinaryWriter.writeMapEntryField(this, 2, metadata.valueType, entry.getValue());
            BinaryWriter.writeMapEntryField(this, 1, metadata.keyType, entry.getKey());
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }
    }

    static final void writeMapEntryField(Writer writer, int fieldNumber, WireFormat.FieldType fieldType, Object object) throws IOException {
        switch (fieldType) {
            case BOOL: {
                writer.writeBool(fieldNumber, (Boolean)object);
                break;
            }
            case FIXED32: {
                writer.writeFixed32(fieldNumber, (Integer)object);
                break;
            }
            case FIXED64: {
                writer.writeFixed64(fieldNumber, (Long)object);
                break;
            }
            case INT32: {
                writer.writeInt32(fieldNumber, (Integer)object);
                break;
            }
            case INT64: {
                writer.writeInt64(fieldNumber, (Long)object);
                break;
            }
            case SFIXED32: {
                writer.writeSFixed32(fieldNumber, (Integer)object);
                break;
            }
            case SFIXED64: {
                writer.writeSFixed64(fieldNumber, (Long)object);
                break;
            }
            case SINT32: {
                writer.writeSInt32(fieldNumber, (Integer)object);
                break;
            }
            case SINT64: {
                writer.writeSInt64(fieldNumber, (Long)object);
                break;
            }
            case STRING: {
                writer.writeString(fieldNumber, (String)object);
                break;
            }
            case UINT32: {
                writer.writeUInt32(fieldNumber, (Integer)object);
                break;
            }
            case UINT64: {
                writer.writeUInt64(fieldNumber, (Long)object);
                break;
            }
            case FLOAT: {
                writer.writeFloat(fieldNumber, ((Float)object).floatValue());
                break;
            }
            case DOUBLE: {
                writer.writeDouble(fieldNumber, (Double)object);
                break;
            }
            case MESSAGE: {
                writer.writeMessage(fieldNumber, object);
                break;
            }
            case BYTES: {
                writer.writeBytes(fieldNumber, (ByteString)object);
                break;
            }
            case ENUM: {
                if (object instanceof Internal.EnumLite) {
                    writer.writeEnum(fieldNumber, ((Internal.EnumLite)object).getNumber());
                    break;
                }
                if (object instanceof Integer) {
                    writer.writeEnum(fieldNumber, (Integer)object);
                    break;
                }
                throw new IllegalArgumentException("Unexpected type for enum in map.");
            }
            default: {
                throw new IllegalArgumentException("Unsupported map value type for: " + (Object)((Object)fieldType));
            }
        }
    }

    private void writeSInt64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 10);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeSInt64(list.get(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeSInt64(fieldNumber, list.get(i));
            }
        }
    }

    private void writeSInt64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
        if (packed) {
            this.requireSpace(10 + list.size() * 10);
            int prevBytes = this.getTotalBytesWritten();
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeSInt64(list.getLong(i));
            }
            int length = this.getTotalBytesWritten() - prevBytes;
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        } else {
            for (int i = list.size() - 1; i >= 0; --i) {
                this.writeSInt64(fieldNumber, list.getLong(i));
            }
        }
    }

    @Override
    public final void writeMessageList(int fieldNumber, List<?> list) throws IOException {
        for (int i = list.size() - 1; i >= 0; --i) {
            this.writeMessage(fieldNumber, list.get(i));
        }
    }

    @Override
    public final void writeMessageList(int fieldNumber, List<?> list, Schema schema) throws IOException {
        for (int i = list.size() - 1; i >= 0; --i) {
            this.writeMessage(fieldNumber, list.get(i), schema);
        }
    }

    @Override
    @Deprecated
    public final void writeGroupList(int fieldNumber, List<?> list) throws IOException {
        for (int i = list.size() - 1; i >= 0; --i) {
            this.writeGroup(fieldNumber, list.get(i));
        }
    }

    @Override
    @Deprecated
    public final void writeGroupList(int fieldNumber, List<?> list, Schema schema) throws IOException {
        for (int i = list.size() - 1; i >= 0; --i) {
            this.writeGroup(fieldNumber, list.get(i), schema);
        }
    }

    @Override
    public final void writeMessageSetItem(int fieldNumber, Object value) throws IOException {
        this.writeTag(1, 4);
        if (value instanceof ByteString) {
            this.writeBytes(3, (ByteString)value);
        } else {
            this.writeMessage(3, value);
        }
        this.writeUInt32(2, fieldNumber);
        this.writeTag(1, 3);
    }

    final AllocatedBuffer newHeapBuffer() {
        return this.alloc.allocateHeapBuffer(this.chunkSize);
    }

    final AllocatedBuffer newHeapBuffer(int capacity) {
        return this.alloc.allocateHeapBuffer(Math.max(capacity, this.chunkSize));
    }

    final AllocatedBuffer newDirectBuffer() {
        return this.alloc.allocateDirectBuffer(this.chunkSize);
    }

    final AllocatedBuffer newDirectBuffer(int capacity) {
        return this.alloc.allocateDirectBuffer(Math.max(capacity, this.chunkSize));
    }

    public abstract int getTotalBytesWritten();

    abstract void requireSpace(int var1);

    abstract void finishCurrentBuffer();

    abstract void writeTag(int var1, int var2);

    abstract void writeVarint32(int var1);

    abstract void writeInt32(int var1);

    abstract void writeSInt32(int var1);

    abstract void writeFixed32(int var1);

    abstract void writeVarint64(long var1);

    abstract void writeSInt64(long var1);

    abstract void writeFixed64(long var1);

    abstract void writeBool(boolean var1);

    abstract void writeString(String var1);

    private static byte computeUInt64SizeNoTag(long value) {
        if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
            return 1;
        }
        if (value < 0L) {
            return 10;
        }
        byte n = 2;
        if ((value & 0xFFFFFFF800000000L) != 0L) {
            n = (byte)(n + 4);
            value >>>= 28;
        }
        if ((value & 0xFFFFFFFFFFE00000L) != 0L) {
            n = (byte)(n + 2);
            value >>>= 14;
        }
        if ((value & 0xFFFFFFFFFFFFC000L) != 0L) {
            n = (byte)(n + 1);
        }
        return n;
    }

    private static final class UnsafeHeapWriter
    extends BinaryWriter {
        private AllocatedBuffer allocatedBuffer;
        private byte[] buffer;
        private long offset;
        private long limit;
        private long offsetMinusOne;
        private long limitMinusOne;
        private long pos;

        UnsafeHeapWriter(BufferAllocator alloc, int chunkSize) {
            super(alloc, chunkSize);
            this.nextBuffer();
        }

        static boolean isSupported() {
            return UnsafeUtil.hasUnsafeArrayOperations();
        }

        @Override
        void finishCurrentBuffer() {
            if (this.allocatedBuffer != null) {
                this.totalDoneBytes += this.bytesWrittenToCurrentBuffer();
                this.allocatedBuffer.position(this.arrayPos() - this.allocatedBuffer.arrayOffset() + 1);
                this.allocatedBuffer = null;
                this.pos = 0L;
                this.limitMinusOne = 0L;
            }
        }

        private int arrayPos() {
            return (int)this.pos;
        }

        private void nextBuffer() {
            this.nextBuffer(this.newHeapBuffer());
        }

        private void nextBuffer(int capacity) {
            this.nextBuffer(this.newHeapBuffer(capacity));
        }

        private void nextBuffer(AllocatedBuffer allocatedBuffer) {
            if (!allocatedBuffer.hasArray()) {
                throw new RuntimeException("Allocator returned non-heap buffer");
            }
            this.finishCurrentBuffer();
            this.buffers.addFirst(allocatedBuffer);
            this.allocatedBuffer = allocatedBuffer;
            this.buffer = allocatedBuffer.array();
            int arrayOffset = allocatedBuffer.arrayOffset();
            this.limit = (long)arrayOffset + (long)allocatedBuffer.limit();
            this.offset = (long)arrayOffset + (long)allocatedBuffer.position();
            this.offsetMinusOne = this.offset - 1L;
            this.pos = this.limitMinusOne = this.limit - 1L;
        }

        @Override
        public int getTotalBytesWritten() {
            return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
        }

        int bytesWrittenToCurrentBuffer() {
            return (int)(this.limitMinusOne - this.pos);
        }

        int spaceLeft() {
            return (int)(this.pos - this.offsetMinusOne);
        }

        @Override
        public void writeUInt32(int fieldNumber, int value) {
            this.requireSpace(10);
            this.writeVarint32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeInt32(int fieldNumber, int value) {
            this.requireSpace(15);
            this.writeInt32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeSInt32(int fieldNumber, int value) {
            this.requireSpace(10);
            this.writeSInt32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeFixed32(int fieldNumber, int value) {
            this.requireSpace(9);
            this.writeFixed32(value);
            this.writeTag(fieldNumber, 5);
        }

        @Override
        public void writeUInt64(int fieldNumber, long value) {
            this.requireSpace(15);
            this.writeVarint64(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeSInt64(int fieldNumber, long value) {
            this.requireSpace(15);
            this.writeSInt64(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeFixed64(int fieldNumber, long value) {
            this.requireSpace(13);
            this.writeFixed64(value);
            this.writeTag(fieldNumber, 1);
        }

        @Override
        public void writeBool(int fieldNumber, boolean value) {
            this.requireSpace(6);
            this.write((byte)(value ? 1 : 0));
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeString(int fieldNumber, String value) {
            int prevBytes = this.getTotalBytesWritten();
            this.writeString(value);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeBytes(int fieldNumber, ByteString value) {
            try {
                value.writeToReverse(this);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.requireSpace(10);
            this.writeVarint32(value.size());
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeMessage(int fieldNumber, Object value) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            Protobuf.getInstance().writeTo(value, this);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            schema.writeTo(value, this);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeGroup(int fieldNumber, Object value) throws IOException {
            this.writeTag(fieldNumber, 4);
            Protobuf.getInstance().writeTo(value, this);
            this.writeTag(fieldNumber, 3);
        }

        @Override
        public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
            this.writeTag(fieldNumber, 4);
            schema.writeTo(value, this);
            this.writeTag(fieldNumber, 3);
        }

        @Override
        public void writeStartGroup(int fieldNumber) {
            this.writeTag(fieldNumber, 3);
        }

        @Override
        public void writeEndGroup(int fieldNumber) {
            this.writeTag(fieldNumber, 4);
        }

        @Override
        void writeInt32(int value) {
            if (value >= 0) {
                this.writeVarint32(value);
            } else {
                this.writeVarint64(value);
            }
        }

        @Override
        void writeSInt32(int value) {
            this.writeVarint32(CodedOutputStream.encodeZigZag32(value));
        }

        @Override
        void writeSInt64(long value) {
            this.writeVarint64(CodedOutputStream.encodeZigZag64(value));
        }

        @Override
        void writeBool(boolean value) {
            this.write((byte)(value ? 1 : 0));
        }

        @Override
        void writeTag(int fieldNumber, int wireType) {
            this.writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override
        void writeVarint32(int value) {
            if ((value & 0xFFFFFF80) == 0) {
                this.writeVarint32OneByte(value);
            } else if ((value & 0xFFFFC000) == 0) {
                this.writeVarint32TwoBytes(value);
            } else if ((value & 0xFFE00000) == 0) {
                this.writeVarint32ThreeBytes(value);
            } else if ((value & 0xF0000000) == 0) {
                this.writeVarint32FourBytes(value);
            } else {
                this.writeVarint32FiveBytes(value);
            }
        }

        private void writeVarint32OneByte(int value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)value);
        }

        private void writeVarint32TwoBytes(int value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
        }

        private void writeVarint32ThreeBytes(int value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
        }

        private void writeVarint32FourBytes(int value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
        }

        private void writeVarint32FiveBytes(int value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
        }

        @Override
        void writeVarint64(long value) {
            switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
                case 1: {
                    this.writeVarint64OneByte(value);
                    break;
                }
                case 2: {
                    this.writeVarint64TwoBytes(value);
                    break;
                }
                case 3: {
                    this.writeVarint64ThreeBytes(value);
                    break;
                }
                case 4: {
                    this.writeVarint64FourBytes(value);
                    break;
                }
                case 5: {
                    this.writeVarint64FiveBytes(value);
                    break;
                }
                case 6: {
                    this.writeVarint64SixBytes(value);
                    break;
                }
                case 7: {
                    this.writeVarint64SevenBytes(value);
                    break;
                }
                case 8: {
                    this.writeVarint64EightBytes(value);
                    break;
                }
                case 9: {
                    this.writeVarint64NineBytes(value);
                    break;
                }
                case 10: {
                    this.writeVarint64TenBytes(value);
                }
            }
        }

        private void writeVarint64OneByte(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)value);
        }

        private void writeVarint64TwoBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)value & 0x7F | 0x80));
        }

        private void writeVarint64ThreeBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)value >>> 14));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64FourBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64FiveBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64SixBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 35));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64SevenBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 42));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 35 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64EightBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 49));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 42 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 35 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64NineBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 56));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 49 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 42 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 35 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64TenBytes(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 63));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 56 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 49 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 42 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 35 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        @Override
        void writeFixed32(int value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 24));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 16));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 8));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)value);
        }

        @Override
        void writeFixed64(long value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 56));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 48));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 40));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 32));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 24));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 16));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 8));
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)value);
        }

        @Override
        void writeString(String in) {
            char c;
            int i;
            this.requireSpace(in.length());
            for (i = in.length() - 1; i >= 0 && (c = in.charAt(i)) < '\u0080'; --i) {
                UnsafeUtil.putByte(this.buffer, this.pos--, (byte)c);
            }
            if (i == -1) {
                return;
            }
            while (i >= 0) {
                c = in.charAt(i);
                if (c < '\u0080' && this.pos > this.offsetMinusOne) {
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)c);
                } else if (c < '\u0800' && this.pos > this.offset) {
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c));
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x3C0 | c >>> 6));
                } else if ((c < '\ud800' || '\udfff' < c) && this.pos > this.offset + 1L) {
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c));
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x1E0 | c >>> 12));
                } else if (this.pos > this.offset + 2L) {
                    char high;
                    if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
                        throw new Utf8.UnpairedSurrogateException(i - 1, i);
                    }
                    --i;
                    int codePoint = Character.toCodePoint(high, c);
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint));
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
                    UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0xF0 | codePoint >>> 18));
                } else {
                    this.requireSpace(i);
                    ++i;
                }
                --i;
            }
        }

        @Override
        public void write(byte value) {
            UnsafeUtil.putByte(this.buffer, this.pos--, value);
        }

        @Override
        public void write(byte[] value, int offset, int length) {
            if (offset < 0 || offset + length > value.length) {
                throw new ArrayIndexOutOfBoundsException(String.format("value.length=%d, offset=%d, length=%d", value.length, offset, length));
            }
            this.requireSpace(length);
            this.pos -= (long)length;
            System.arraycopy(value, offset, this.buffer, this.arrayPos() + 1, length);
        }

        @Override
        public void writeLazy(byte[] value, int offset, int length) {
            if (offset < 0 || offset + length > value.length) {
                throw new ArrayIndexOutOfBoundsException(String.format("value.length=%d, offset=%d, length=%d", value.length, offset, length));
            }
            if (this.spaceLeft() < length) {
                this.totalDoneBytes += length;
                this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
                this.nextBuffer();
                return;
            }
            this.pos -= (long)length;
            System.arraycopy(value, offset, this.buffer, this.arrayPos() + 1, length);
        }

        @Override
        public void write(ByteBuffer value) {
            int length = value.remaining();
            this.requireSpace(length);
            this.pos -= (long)length;
            value.get(this.buffer, this.arrayPos() + 1, length);
        }

        @Override
        public void writeLazy(ByteBuffer value) {
            int length = value.remaining();
            if (this.spaceLeft() < length) {
                this.totalDoneBytes += length;
                this.buffers.addFirst(AllocatedBuffer.wrap(value));
                this.nextBuffer();
            }
            this.pos -= (long)length;
            value.get(this.buffer, this.arrayPos() + 1, length);
        }

        @Override
        void requireSpace(int size) {
            if (this.spaceLeft() < size) {
                this.nextBuffer(size);
            }
        }
    }

    private static final class UnsafeDirectWriter
    extends BinaryWriter {
        private ByteBuffer buffer;
        private long bufferOffset;
        private long limitMinusOne;
        private long pos;

        UnsafeDirectWriter(BufferAllocator alloc, int chunkSize) {
            super(alloc, chunkSize);
            this.nextBuffer();
        }

        private static boolean isSupported() {
            return UnsafeUtil.hasUnsafeByteBufferOperations();
        }

        private void nextBuffer() {
            this.nextBuffer(this.newDirectBuffer());
        }

        private void nextBuffer(int capacity) {
            this.nextBuffer(this.newDirectBuffer(capacity));
        }

        private void nextBuffer(AllocatedBuffer allocatedBuffer) {
            if (!allocatedBuffer.hasNioBuffer()) {
                throw new RuntimeException("Allocated buffer does not have NIO buffer");
            }
            ByteBuffer nioBuffer = allocatedBuffer.nioBuffer();
            if (!nioBuffer.isDirect()) {
                throw new RuntimeException("Allocator returned non-direct buffer");
            }
            this.finishCurrentBuffer();
            this.buffers.addFirst(allocatedBuffer);
            this.buffer = nioBuffer;
            Java8Compatibility.limit(this.buffer, this.buffer.capacity());
            Java8Compatibility.position(this.buffer, 0);
            this.bufferOffset = UnsafeUtil.addressOffset(this.buffer);
            this.pos = this.limitMinusOne = this.bufferOffset + (long)(this.buffer.limit() - 1);
        }

        @Override
        public int getTotalBytesWritten() {
            return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
        }

        private int bytesWrittenToCurrentBuffer() {
            return (int)(this.limitMinusOne - this.pos);
        }

        private int spaceLeft() {
            return this.bufferPos() + 1;
        }

        @Override
        void finishCurrentBuffer() {
            if (this.buffer != null) {
                this.totalDoneBytes += this.bytesWrittenToCurrentBuffer();
                Java8Compatibility.position(this.buffer, this.bufferPos() + 1);
                this.buffer = null;
                this.pos = 0L;
                this.limitMinusOne = 0L;
            }
        }

        private int bufferPos() {
            return (int)(this.pos - this.bufferOffset);
        }

        @Override
        public void writeUInt32(int fieldNumber, int value) {
            this.requireSpace(10);
            this.writeVarint32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeInt32(int fieldNumber, int value) {
            this.requireSpace(15);
            this.writeInt32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeSInt32(int fieldNumber, int value) {
            this.requireSpace(10);
            this.writeSInt32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeFixed32(int fieldNumber, int value) {
            this.requireSpace(9);
            this.writeFixed32(value);
            this.writeTag(fieldNumber, 5);
        }

        @Override
        public void writeUInt64(int fieldNumber, long value) {
            this.requireSpace(15);
            this.writeVarint64(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeSInt64(int fieldNumber, long value) {
            this.requireSpace(15);
            this.writeSInt64(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeFixed64(int fieldNumber, long value) {
            this.requireSpace(13);
            this.writeFixed64(value);
            this.writeTag(fieldNumber, 1);
        }

        @Override
        public void writeBool(int fieldNumber, boolean value) {
            this.requireSpace(6);
            this.write((byte)(value ? 1 : 0));
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeString(int fieldNumber, String value) {
            int prevBytes = this.getTotalBytesWritten();
            this.writeString(value);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeBytes(int fieldNumber, ByteString value) {
            try {
                value.writeToReverse(this);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.requireSpace(10);
            this.writeVarint32(value.size());
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeMessage(int fieldNumber, Object value) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            Protobuf.getInstance().writeTo(value, this);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            schema.writeTo(value, this);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeGroup(int fieldNumber, Object value) throws IOException {
            this.writeTag(fieldNumber, 4);
            Protobuf.getInstance().writeTo(value, this);
            this.writeTag(fieldNumber, 3);
        }

        @Override
        public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
            this.writeTag(fieldNumber, 4);
            schema.writeTo(value, this);
            this.writeTag(fieldNumber, 3);
        }

        @Override
        @Deprecated
        public void writeStartGroup(int fieldNumber) {
            this.writeTag(fieldNumber, 3);
        }

        @Override
        @Deprecated
        public void writeEndGroup(int fieldNumber) {
            this.writeTag(fieldNumber, 4);
        }

        @Override
        void writeInt32(int value) {
            if (value >= 0) {
                this.writeVarint32(value);
            } else {
                this.writeVarint64(value);
            }
        }

        @Override
        void writeSInt32(int value) {
            this.writeVarint32(CodedOutputStream.encodeZigZag32(value));
        }

        @Override
        void writeSInt64(long value) {
            this.writeVarint64(CodedOutputStream.encodeZigZag64(value));
        }

        @Override
        void writeBool(boolean value) {
            this.write((byte)(value ? 1 : 0));
        }

        @Override
        void writeTag(int fieldNumber, int wireType) {
            this.writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override
        void writeVarint32(int value) {
            if ((value & 0xFFFFFF80) == 0) {
                this.writeVarint32OneByte(value);
            } else if ((value & 0xFFFFC000) == 0) {
                this.writeVarint32TwoBytes(value);
            } else if ((value & 0xFFE00000) == 0) {
                this.writeVarint32ThreeBytes(value);
            } else if ((value & 0xF0000000) == 0) {
                this.writeVarint32FourBytes(value);
            } else {
                this.writeVarint32FiveBytes(value);
            }
        }

        private void writeVarint32OneByte(int value) {
            UnsafeUtil.putByte(this.pos--, (byte)value);
        }

        private void writeVarint32TwoBytes(int value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
        }

        private void writeVarint32ThreeBytes(int value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
        }

        private void writeVarint32FourBytes(int value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
        }

        private void writeVarint32FiveBytes(int value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
        }

        @Override
        void writeVarint64(long value) {
            switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
                case 1: {
                    this.writeVarint64OneByte(value);
                    break;
                }
                case 2: {
                    this.writeVarint64TwoBytes(value);
                    break;
                }
                case 3: {
                    this.writeVarint64ThreeBytes(value);
                    break;
                }
                case 4: {
                    this.writeVarint64FourBytes(value);
                    break;
                }
                case 5: {
                    this.writeVarint64FiveBytes(value);
                    break;
                }
                case 6: {
                    this.writeVarint64SixBytes(value);
                    break;
                }
                case 7: {
                    this.writeVarint64SevenBytes(value);
                    break;
                }
                case 8: {
                    this.writeVarint64EightBytes(value);
                    break;
                }
                case 9: {
                    this.writeVarint64NineBytes(value);
                    break;
                }
                case 10: {
                    this.writeVarint64TenBytes(value);
                }
            }
        }

        private void writeVarint64OneByte(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)value);
        }

        private void writeVarint64TwoBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7));
            UnsafeUtil.putByte(this.pos--, (byte)((int)value & 0x7F | 0x80));
        }

        private void writeVarint64ThreeBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)((int)value >>> 14));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64FourBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64FiveBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64SixBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 35));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64SevenBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 42));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 35 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64EightBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 49));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 42 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 35 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64NineBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 56));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 49 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 42 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 35 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        private void writeVarint64TenBytes(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 63));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 56 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 49 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 42 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 35 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7FL | 0x80L));
            UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7FL | 0x80L));
        }

        @Override
        void writeFixed32(int value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 24));
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 16));
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 8));
            UnsafeUtil.putByte(this.pos--, (byte)value);
        }

        @Override
        void writeFixed64(long value) {
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 56));
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 48));
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 40));
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 32));
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 24));
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 16));
            UnsafeUtil.putByte(this.pos--, (byte)(value >> 8));
            UnsafeUtil.putByte(this.pos--, (byte)value);
        }

        @Override
        void writeString(String in) {
            char c;
            int i;
            this.requireSpace(in.length());
            for (i = in.length() - 1; i >= 0 && (c = in.charAt(i)) < '\u0080'; --i) {
                UnsafeUtil.putByte(this.pos--, (byte)c);
            }
            if (i == -1) {
                return;
            }
            while (i >= 0) {
                c = in.charAt(i);
                if (c < '\u0080' && this.pos >= this.bufferOffset) {
                    UnsafeUtil.putByte(this.pos--, (byte)c);
                } else if (c < '\u0800' && this.pos > this.bufferOffset) {
                    UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c));
                    UnsafeUtil.putByte(this.pos--, (byte)(0x3C0 | c >>> 6));
                } else if ((c < '\ud800' || '\udfff' < c) && this.pos > this.bufferOffset + 1L) {
                    UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c));
                    UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
                    UnsafeUtil.putByte(this.pos--, (byte)(0x1E0 | c >>> 12));
                } else if (this.pos > this.bufferOffset + 2L) {
                    char high;
                    if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
                        throw new Utf8.UnpairedSurrogateException(i - 1, i);
                    }
                    --i;
                    int codePoint = Character.toCodePoint(high, c);
                    UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint));
                    UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
                    UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
                    UnsafeUtil.putByte(this.pos--, (byte)(0xF0 | codePoint >>> 18));
                } else {
                    this.requireSpace(i);
                    ++i;
                }
                --i;
            }
        }

        @Override
        public void write(byte value) {
            UnsafeUtil.putByte(this.pos--, value);
        }

        @Override
        public void write(byte[] value, int offset, int length) {
            if (this.spaceLeft() < length) {
                this.nextBuffer(length);
            }
            this.pos -= (long)length;
            Java8Compatibility.position(this.buffer, this.bufferPos() + 1);
            this.buffer.put(value, offset, length);
        }

        @Override
        public void writeLazy(byte[] value, int offset, int length) {
            if (this.spaceLeft() < length) {
                this.totalDoneBytes += length;
                this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
                this.nextBuffer();
                return;
            }
            this.pos -= (long)length;
            Java8Compatibility.position(this.buffer, this.bufferPos() + 1);
            this.buffer.put(value, offset, length);
        }

        @Override
        public void write(ByteBuffer value) {
            int length = value.remaining();
            if (this.spaceLeft() < length) {
                this.nextBuffer(length);
            }
            this.pos -= (long)length;
            Java8Compatibility.position(this.buffer, this.bufferPos() + 1);
            this.buffer.put(value);
        }

        @Override
        public void writeLazy(ByteBuffer value) {
            int length = value.remaining();
            if (this.spaceLeft() < length) {
                this.totalDoneBytes += length;
                this.buffers.addFirst(AllocatedBuffer.wrap(value));
                this.nextBuffer();
                return;
            }
            this.pos -= (long)length;
            Java8Compatibility.position(this.buffer, this.bufferPos() + 1);
            this.buffer.put(value);
        }

        @Override
        void requireSpace(int size) {
            if (this.spaceLeft() < size) {
                this.nextBuffer(size);
            }
        }
    }

    private static final class SafeHeapWriter
    extends BinaryWriter {
        private AllocatedBuffer allocatedBuffer;
        private byte[] buffer;
        private int offset;
        private int limit;
        private int offsetMinusOne;
        private int limitMinusOne;
        private int pos;

        SafeHeapWriter(BufferAllocator alloc, int chunkSize) {
            super(alloc, chunkSize);
            this.nextBuffer();
        }

        @Override
        void finishCurrentBuffer() {
            if (this.allocatedBuffer != null) {
                this.totalDoneBytes += this.bytesWrittenToCurrentBuffer();
                this.allocatedBuffer.position(this.pos - this.allocatedBuffer.arrayOffset() + 1);
                this.allocatedBuffer = null;
                this.pos = 0;
                this.limitMinusOne = 0;
            }
        }

        private void nextBuffer() {
            this.nextBuffer(this.newHeapBuffer());
        }

        private void nextBuffer(int capacity) {
            this.nextBuffer(this.newHeapBuffer(capacity));
        }

        private void nextBuffer(AllocatedBuffer allocatedBuffer) {
            if (!allocatedBuffer.hasArray()) {
                throw new RuntimeException("Allocator returned non-heap buffer");
            }
            this.finishCurrentBuffer();
            this.buffers.addFirst(allocatedBuffer);
            this.allocatedBuffer = allocatedBuffer;
            this.buffer = allocatedBuffer.array();
            int arrayOffset = allocatedBuffer.arrayOffset();
            this.limit = arrayOffset + allocatedBuffer.limit();
            this.offset = arrayOffset + allocatedBuffer.position();
            this.offsetMinusOne = this.offset - 1;
            this.pos = this.limitMinusOne = this.limit - 1;
        }

        @Override
        public int getTotalBytesWritten() {
            return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
        }

        int bytesWrittenToCurrentBuffer() {
            return this.limitMinusOne - this.pos;
        }

        int spaceLeft() {
            return this.pos - this.offsetMinusOne;
        }

        @Override
        public void writeUInt32(int fieldNumber, int value) throws IOException {
            this.requireSpace(10);
            this.writeVarint32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeInt32(int fieldNumber, int value) throws IOException {
            this.requireSpace(15);
            this.writeInt32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeSInt32(int fieldNumber, int value) throws IOException {
            this.requireSpace(10);
            this.writeSInt32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeFixed32(int fieldNumber, int value) throws IOException {
            this.requireSpace(9);
            this.writeFixed32(value);
            this.writeTag(fieldNumber, 5);
        }

        @Override
        public void writeUInt64(int fieldNumber, long value) throws IOException {
            this.requireSpace(15);
            this.writeVarint64(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeSInt64(int fieldNumber, long value) throws IOException {
            this.requireSpace(15);
            this.writeSInt64(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeFixed64(int fieldNumber, long value) throws IOException {
            this.requireSpace(13);
            this.writeFixed64(value);
            this.writeTag(fieldNumber, 1);
        }

        @Override
        public void writeBool(int fieldNumber, boolean value) throws IOException {
            this.requireSpace(6);
            this.write((byte)(value ? 1 : 0));
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeString(int fieldNumber, String value) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            this.writeString(value);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeBytes(int fieldNumber, ByteString value) throws IOException {
            try {
                value.writeToReverse(this);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.requireSpace(10);
            this.writeVarint32(value.size());
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeMessage(int fieldNumber, Object value) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            Protobuf.getInstance().writeTo(value, this);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            schema.writeTo(value, this);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        @Deprecated
        public void writeGroup(int fieldNumber, Object value) throws IOException {
            this.writeTag(fieldNumber, 4);
            Protobuf.getInstance().writeTo(value, this);
            this.writeTag(fieldNumber, 3);
        }

        @Override
        public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
            this.writeTag(fieldNumber, 4);
            schema.writeTo(value, this);
            this.writeTag(fieldNumber, 3);
        }

        @Override
        public void writeStartGroup(int fieldNumber) {
            this.writeTag(fieldNumber, 3);
        }

        @Override
        public void writeEndGroup(int fieldNumber) {
            this.writeTag(fieldNumber, 4);
        }

        @Override
        void writeInt32(int value) {
            if (value >= 0) {
                this.writeVarint32(value);
            } else {
                this.writeVarint64(value);
            }
        }

        @Override
        void writeSInt32(int value) {
            this.writeVarint32(CodedOutputStream.encodeZigZag32(value));
        }

        @Override
        void writeSInt64(long value) {
            this.writeVarint64(CodedOutputStream.encodeZigZag64(value));
        }

        @Override
        void writeBool(boolean value) {
            this.write((byte)(value ? 1 : 0));
        }

        @Override
        void writeTag(int fieldNumber, int wireType) {
            this.writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override
        void writeVarint32(int value) {
            if ((value & 0xFFFFFF80) == 0) {
                this.writeVarint32OneByte(value);
            } else if ((value & 0xFFFFC000) == 0) {
                this.writeVarint32TwoBytes(value);
            } else if ((value & 0xFFE00000) == 0) {
                this.writeVarint32ThreeBytes(value);
            } else if ((value & 0xF0000000) == 0) {
                this.writeVarint32FourBytes(value);
            } else {
                this.writeVarint32FiveBytes(value);
            }
        }

        private void writeVarint32OneByte(int value) {
            this.buffer[this.pos--] = (byte)value;
        }

        private void writeVarint32TwoBytes(int value) {
            this.buffer[this.pos--] = (byte)(value >>> 7);
            this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
        }

        private void writeVarint32ThreeBytes(int value) {
            this.buffer[this.pos--] = (byte)(value >>> 14);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
            this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
        }

        private void writeVarint32FourBytes(int value) {
            this.buffer[this.pos--] = (byte)(value >>> 21);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7F | 0x80);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
            this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
        }

        private void writeVarint32FiveBytes(int value) {
            this.buffer[this.pos--] = (byte)(value >>> 28);
            this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7F | 0x80);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7F | 0x80);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
            this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
        }

        @Override
        void writeVarint64(long value) {
            switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
                case 1: {
                    this.writeVarint64OneByte(value);
                    break;
                }
                case 2: {
                    this.writeVarint64TwoBytes(value);
                    break;
                }
                case 3: {
                    this.writeVarint64ThreeBytes(value);
                    break;
                }
                case 4: {
                    this.writeVarint64FourBytes(value);
                    break;
                }
                case 5: {
                    this.writeVarint64FiveBytes(value);
                    break;
                }
                case 6: {
                    this.writeVarint64SixBytes(value);
                    break;
                }
                case 7: {
                    this.writeVarint64SevenBytes(value);
                    break;
                }
                case 8: {
                    this.writeVarint64EightBytes(value);
                    break;
                }
                case 9: {
                    this.writeVarint64NineBytes(value);
                    break;
                }
                case 10: {
                    this.writeVarint64TenBytes(value);
                }
            }
        }

        private void writeVarint64OneByte(long value) {
            this.buffer[this.pos--] = (byte)value;
        }

        private void writeVarint64TwoBytes(long value) {
            this.buffer[this.pos--] = (byte)(value >>> 7);
            this.buffer[this.pos--] = (byte)((int)value & 0x7F | 0x80);
        }

        private void writeVarint64ThreeBytes(long value) {
            this.buffer[this.pos--] = (byte)((int)value >>> 14);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value & 0x7FL | 0x80L);
        }

        private void writeVarint64FourBytes(long value) {
            this.buffer[this.pos--] = (byte)(value >>> 21);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value & 0x7FL | 0x80L);
        }

        private void writeVarint64FiveBytes(long value) {
            this.buffer[this.pos--] = (byte)(value >>> 28);
            this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value & 0x7FL | 0x80L);
        }

        private void writeVarint64SixBytes(long value) {
            this.buffer[this.pos--] = (byte)(value >>> 35);
            this.buffer[this.pos--] = (byte)(value >>> 28 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value & 0x7FL | 0x80L);
        }

        private void writeVarint64SevenBytes(long value) {
            this.buffer[this.pos--] = (byte)(value >>> 42);
            this.buffer[this.pos--] = (byte)(value >>> 35 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 28 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value & 0x7FL | 0x80L);
        }

        private void writeVarint64EightBytes(long value) {
            this.buffer[this.pos--] = (byte)(value >>> 49);
            this.buffer[this.pos--] = (byte)(value >>> 42 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 35 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 28 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value & 0x7FL | 0x80L);
        }

        private void writeVarint64NineBytes(long value) {
            this.buffer[this.pos--] = (byte)(value >>> 56);
            this.buffer[this.pos--] = (byte)(value >>> 49 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 42 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 35 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 28 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value & 0x7FL | 0x80L);
        }

        private void writeVarint64TenBytes(long value) {
            this.buffer[this.pos--] = (byte)(value >>> 63);
            this.buffer[this.pos--] = (byte)(value >>> 56 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 49 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 42 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 35 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 28 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7FL | 0x80L);
            this.buffer[this.pos--] = (byte)(value & 0x7FL | 0x80L);
        }

        @Override
        void writeFixed32(int value) {
            this.buffer[this.pos--] = (byte)(value >> 24);
            this.buffer[this.pos--] = (byte)(value >> 16);
            this.buffer[this.pos--] = (byte)(value >> 8);
            this.buffer[this.pos--] = (byte)value;
        }

        @Override
        void writeFixed64(long value) {
            this.buffer[this.pos--] = (byte)(value >> 56);
            this.buffer[this.pos--] = (byte)(value >> 48);
            this.buffer[this.pos--] = (byte)(value >> 40);
            this.buffer[this.pos--] = (byte)(value >> 32);
            this.buffer[this.pos--] = (byte)(value >> 24);
            this.buffer[this.pos--] = (byte)(value >> 16);
            this.buffer[this.pos--] = (byte)(value >> 8);
            this.buffer[this.pos--] = (byte)value;
        }

        @Override
        void writeString(String in) {
            char c;
            int i;
            this.requireSpace(in.length());
            this.pos -= i;
            for (i = in.length() - 1; i >= 0 && (c = in.charAt(i)) < '\u0080'; --i) {
                this.buffer[this.pos + i] = (byte)c;
            }
            if (i == -1) {
                --this.pos;
                return;
            }
            this.pos += i;
            while (i >= 0) {
                c = in.charAt(i);
                if (c < '\u0080' && this.pos > this.offsetMinusOne) {
                    this.buffer[this.pos--] = (byte)c;
                } else if (c < '\u0800' && this.pos > this.offset) {
                    this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c);
                    this.buffer[this.pos--] = (byte)(0x3C0 | c >>> 6);
                } else if ((c < '\ud800' || '\udfff' < c) && this.pos > this.offset + 1) {
                    this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c);
                    this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c >>> 6);
                    this.buffer[this.pos--] = (byte)(0x1E0 | c >>> 12);
                } else if (this.pos > this.offset + 2) {
                    char high = '\u0000';
                    if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
                        throw new Utf8.UnpairedSurrogateException(i - 1, i);
                    }
                    --i;
                    int codePoint = Character.toCodePoint(high, c);
                    this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint);
                    this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint >>> 6);
                    this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint >>> 12);
                    this.buffer[this.pos--] = (byte)(0xF0 | codePoint >>> 18);
                } else {
                    this.requireSpace(i);
                    ++i;
                }
                --i;
            }
        }

        @Override
        public void write(byte value) {
            this.buffer[this.pos--] = value;
        }

        @Override
        public void write(byte[] value, int offset, int length) {
            if (this.spaceLeft() < length) {
                this.nextBuffer(length);
            }
            this.pos -= length;
            System.arraycopy(value, offset, this.buffer, this.pos + 1, length);
        }

        @Override
        public void writeLazy(byte[] value, int offset, int length) {
            if (this.spaceLeft() < length) {
                this.totalDoneBytes += length;
                this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
                this.nextBuffer();
                return;
            }
            this.pos -= length;
            System.arraycopy(value, offset, this.buffer, this.pos + 1, length);
        }

        @Override
        public void write(ByteBuffer value) {
            int length = value.remaining();
            if (this.spaceLeft() < length) {
                this.nextBuffer(length);
            }
            this.pos -= length;
            value.get(this.buffer, this.pos + 1, length);
        }

        @Override
        public void writeLazy(ByteBuffer value) {
            int length = value.remaining();
            if (this.spaceLeft() < length) {
                this.totalDoneBytes += length;
                this.buffers.addFirst(AllocatedBuffer.wrap(value));
                this.nextBuffer();
            }
            this.pos -= length;
            value.get(this.buffer, this.pos + 1, length);
        }

        @Override
        void requireSpace(int size) {
            if (this.spaceLeft() < size) {
                this.nextBuffer(size);
            }
        }
    }

    private static final class SafeDirectWriter
    extends BinaryWriter {
        private ByteBuffer buffer;
        private int limitMinusOne;
        private int pos;

        SafeDirectWriter(BufferAllocator alloc, int chunkSize) {
            super(alloc, chunkSize);
            this.nextBuffer();
        }

        private void nextBuffer() {
            this.nextBuffer(this.newDirectBuffer());
        }

        private void nextBuffer(int capacity) {
            this.nextBuffer(this.newDirectBuffer(capacity));
        }

        private void nextBuffer(AllocatedBuffer allocatedBuffer) {
            if (!allocatedBuffer.hasNioBuffer()) {
                throw new RuntimeException("Allocated buffer does not have NIO buffer");
            }
            ByteBuffer nioBuffer = allocatedBuffer.nioBuffer();
            if (!nioBuffer.isDirect()) {
                throw new RuntimeException("Allocator returned non-direct buffer");
            }
            this.finishCurrentBuffer();
            this.buffers.addFirst(allocatedBuffer);
            this.buffer = nioBuffer;
            Java8Compatibility.limit(this.buffer, this.buffer.capacity());
            Java8Compatibility.position(this.buffer, 0);
            this.buffer.order(ByteOrder.LITTLE_ENDIAN);
            this.pos = this.limitMinusOne = this.buffer.limit() - 1;
        }

        @Override
        public int getTotalBytesWritten() {
            return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
        }

        private int bytesWrittenToCurrentBuffer() {
            return this.limitMinusOne - this.pos;
        }

        private int spaceLeft() {
            return this.pos + 1;
        }

        @Override
        void finishCurrentBuffer() {
            if (this.buffer != null) {
                this.totalDoneBytes += this.bytesWrittenToCurrentBuffer();
                Java8Compatibility.position(this.buffer, this.pos + 1);
                this.buffer = null;
                this.pos = 0;
                this.limitMinusOne = 0;
            }
        }

        @Override
        public void writeUInt32(int fieldNumber, int value) {
            this.requireSpace(10);
            this.writeVarint32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeInt32(int fieldNumber, int value) {
            this.requireSpace(15);
            this.writeInt32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeSInt32(int fieldNumber, int value) {
            this.requireSpace(10);
            this.writeSInt32(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeFixed32(int fieldNumber, int value) {
            this.requireSpace(9);
            this.writeFixed32(value);
            this.writeTag(fieldNumber, 5);
        }

        @Override
        public void writeUInt64(int fieldNumber, long value) {
            this.requireSpace(15);
            this.writeVarint64(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeSInt64(int fieldNumber, long value) {
            this.requireSpace(15);
            this.writeSInt64(value);
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeFixed64(int fieldNumber, long value) {
            this.requireSpace(13);
            this.writeFixed64(value);
            this.writeTag(fieldNumber, 1);
        }

        @Override
        public void writeBool(int fieldNumber, boolean value) {
            this.requireSpace(6);
            this.write((byte)(value ? 1 : 0));
            this.writeTag(fieldNumber, 0);
        }

        @Override
        public void writeString(int fieldNumber, String value) {
            int prevBytes = this.getTotalBytesWritten();
            this.writeString(value);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeBytes(int fieldNumber, ByteString value) {
            try {
                value.writeToReverse(this);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.requireSpace(10);
            this.writeVarint32(value.size());
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeMessage(int fieldNumber, Object value) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            Protobuf.getInstance().writeTo(value, this);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
            int prevBytes = this.getTotalBytesWritten();
            schema.writeTo(value, this);
            int length = this.getTotalBytesWritten() - prevBytes;
            this.requireSpace(10);
            this.writeVarint32(length);
            this.writeTag(fieldNumber, 2);
        }

        @Override
        @Deprecated
        public void writeGroup(int fieldNumber, Object value) throws IOException {
            this.writeTag(fieldNumber, 4);
            Protobuf.getInstance().writeTo(value, this);
            this.writeTag(fieldNumber, 3);
        }

        @Override
        public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
            this.writeTag(fieldNumber, 4);
            schema.writeTo(value, this);
            this.writeTag(fieldNumber, 3);
        }

        @Override
        @Deprecated
        public void writeStartGroup(int fieldNumber) {
            this.writeTag(fieldNumber, 3);
        }

        @Override
        @Deprecated
        public void writeEndGroup(int fieldNumber) {
            this.writeTag(fieldNumber, 4);
        }

        @Override
        void writeInt32(int value) {
            if (value >= 0) {
                this.writeVarint32(value);
            } else {
                this.writeVarint64(value);
            }
        }

        @Override
        void writeSInt32(int value) {
            this.writeVarint32(CodedOutputStream.encodeZigZag32(value));
        }

        @Override
        void writeSInt64(long value) {
            this.writeVarint64(CodedOutputStream.encodeZigZag64(value));
        }

        @Override
        void writeBool(boolean value) {
            this.write((byte)(value ? 1 : 0));
        }

        @Override
        void writeTag(int fieldNumber, int wireType) {
            this.writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override
        void writeVarint32(int value) {
            if ((value & 0xFFFFFF80) == 0) {
                this.writeVarint32OneByte(value);
            } else if ((value & 0xFFFFC000) == 0) {
                this.writeVarint32TwoBytes(value);
            } else if ((value & 0xFFE00000) == 0) {
                this.writeVarint32ThreeBytes(value);
            } else if ((value & 0xF0000000) == 0) {
                this.writeVarint32FourBytes(value);
            } else {
                this.writeVarint32FiveBytes(value);
            }
        }

        private void writeVarint32OneByte(int value) {
            this.buffer.put(this.pos--, (byte)value);
        }

        private void writeVarint32TwoBytes(int value) {
            this.pos -= 2;
            this.buffer.putShort(this.pos + 1, (short)((value & 0x3F80) << 1 | (value & 0x7F | 0x80)));
        }

        private void writeVarint32ThreeBytes(int value) {
            this.pos -= 3;
            this.buffer.putInt(this.pos, (value & 0x1FC000) << 10 | (value & 0x3F80 | 0x4000) << 9 | (value & 0x7F | 0x80) << 8);
        }

        private void writeVarint32FourBytes(int value) {
            this.pos -= 4;
            this.buffer.putInt(this.pos + 1, (value & 0xFE00000) << 3 | (value & 0x1FC000 | 0x200000) << 2 | (value & 0x3F80 | 0x4000) << 1 | (value & 0x7F | 0x80));
        }

        private void writeVarint32FiveBytes(int value) {
            this.buffer.put(this.pos--, (byte)(value >>> 28));
            this.pos -= 4;
            this.buffer.putInt(this.pos + 1, (value >>> 21 & 0x7F | 0x80) << 24 | (value >>> 14 & 0x7F | 0x80) << 16 | (value >>> 7 & 0x7F | 0x80) << 8 | (value & 0x7F | 0x80));
        }

        @Override
        void writeVarint64(long value) {
            switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
                case 1: {
                    this.writeVarint64OneByte(value);
                    break;
                }
                case 2: {
                    this.writeVarint64TwoBytes(value);
                    break;
                }
                case 3: {
                    this.writeVarint64ThreeBytes(value);
                    break;
                }
                case 4: {
                    this.writeVarint64FourBytes(value);
                    break;
                }
                case 5: {
                    this.writeVarint64FiveBytes(value);
                    break;
                }
                case 6: {
                    this.writeVarint64SixBytes(value);
                    break;
                }
                case 7: {
                    this.writeVarint64SevenBytes(value);
                    break;
                }
                case 8: {
                    this.writeVarint64EightBytes(value);
                    break;
                }
                case 9: {
                    this.writeVarint64NineBytes(value);
                    break;
                }
                case 10: {
                    this.writeVarint64TenBytes(value);
                }
            }
        }

        private void writeVarint64OneByte(long value) {
            this.writeVarint32OneByte((int)value);
        }

        private void writeVarint64TwoBytes(long value) {
            this.writeVarint32TwoBytes((int)value);
        }

        private void writeVarint64ThreeBytes(long value) {
            this.writeVarint32ThreeBytes((int)value);
        }

        private void writeVarint64FourBytes(long value) {
            this.writeVarint32FourBytes((int)value);
        }

        private void writeVarint64FiveBytes(long value) {
            this.pos -= 5;
            this.buffer.putLong(this.pos - 2, (value & 0x7F0000000L) << 28 | (value & 0xFE00000L | 0x10000000L) << 27 | (value & 0x1FC000L | 0x200000L) << 26 | (value & 0x3F80L | 0x4000L) << 25 | (value & 0x7FL | 0x80L) << 24);
        }

        private void writeVarint64SixBytes(long value) {
            this.pos -= 6;
            this.buffer.putLong(this.pos - 1, (value & 0x3F800000000L) << 21 | (value & 0x7F0000000L | 0x800000000L) << 20 | (value & 0xFE00000L | 0x10000000L) << 19 | (value & 0x1FC000L | 0x200000L) << 18 | (value & 0x3F80L | 0x4000L) << 17 | (value & 0x7FL | 0x80L) << 16);
        }

        private void writeVarint64SevenBytes(long value) {
            this.pos -= 7;
            this.buffer.putLong(this.pos, (value & 0x1FC0000000000L) << 14 | (value & 0x3F800000000L | 0x40000000000L) << 13 | (value & 0x7F0000000L | 0x800000000L) << 12 | (value & 0xFE00000L | 0x10000000L) << 11 | (value & 0x1FC000L | 0x200000L) << 10 | (value & 0x3F80L | 0x4000L) << 9 | (value & 0x7FL | 0x80L) << 8);
        }

        private void writeVarint64EightBytes(long value) {
            this.pos -= 8;
            this.buffer.putLong(this.pos + 1, (value & 0xFE000000000000L) << 7 | (value & 0x1FC0000000000L | 0x2000000000000L) << 6 | (value & 0x3F800000000L | 0x40000000000L) << 5 | (value & 0x7F0000000L | 0x800000000L) << 4 | (value & 0xFE00000L | 0x10000000L) << 3 | (value & 0x1FC000L | 0x200000L) << 2 | (value & 0x3F80L | 0x4000L) << 1 | (value & 0x7FL | 0x80L));
        }

        private void writeVarint64EightBytesWithSign(long value) {
            this.pos -= 8;
            this.buffer.putLong(this.pos + 1, (value & 0xFE000000000000L | 0x100000000000000L) << 7 | (value & 0x1FC0000000000L | 0x2000000000000L) << 6 | (value & 0x3F800000000L | 0x40000000000L) << 5 | (value & 0x7F0000000L | 0x800000000L) << 4 | (value & 0xFE00000L | 0x10000000L) << 3 | (value & 0x1FC000L | 0x200000L) << 2 | (value & 0x3F80L | 0x4000L) << 1 | (value & 0x7FL | 0x80L));
        }

        private void writeVarint64NineBytes(long value) {
            this.buffer.put(this.pos--, (byte)(value >>> 56));
            this.writeVarint64EightBytesWithSign(value & 0xFFFFFFFFFFFFFFL);
        }

        private void writeVarint64TenBytes(long value) {
            this.buffer.put(this.pos--, (byte)(value >>> 63));
            this.buffer.put(this.pos--, (byte)(value >>> 56 & 0x7FL | 0x80L));
            this.writeVarint64EightBytesWithSign(value & 0xFFFFFFFFFFFFFFL);
        }

        @Override
        void writeFixed32(int value) {
            this.pos -= 4;
            this.buffer.putInt(this.pos + 1, value);
        }

        @Override
        void writeFixed64(long value) {
            this.pos -= 8;
            this.buffer.putLong(this.pos + 1, value);
        }

        @Override
        void writeString(String in) {
            char c;
            int i;
            this.requireSpace(in.length());
            this.pos -= i;
            for (i = in.length() - 1; i >= 0 && (c = in.charAt(i)) < '\u0080'; --i) {
                this.buffer.put(this.pos + i, (byte)c);
            }
            if (i == -1) {
                --this.pos;
                return;
            }
            this.pos += i;
            while (i >= 0) {
                c = in.charAt(i);
                if (c < '\u0080' && this.pos >= 0) {
                    this.buffer.put(this.pos--, (byte)c);
                } else if (c < '\u0800' && this.pos > 0) {
                    this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c));
                    this.buffer.put(this.pos--, (byte)(0x3C0 | c >>> 6));
                } else if ((c < '\ud800' || '\udfff' < c) && this.pos > 1) {
                    this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c));
                    this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
                    this.buffer.put(this.pos--, (byte)(0x1E0 | c >>> 12));
                } else if (this.pos > 2) {
                    char high = '\u0000';
                    if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
                        throw new Utf8.UnpairedSurrogateException(i - 1, i);
                    }
                    --i;
                    int codePoint = Character.toCodePoint(high, c);
                    this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint));
                    this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
                    this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
                    this.buffer.put(this.pos--, (byte)(0xF0 | codePoint >>> 18));
                } else {
                    this.requireSpace(i);
                    ++i;
                }
                --i;
            }
        }

        @Override
        public void write(byte value) {
            this.buffer.put(this.pos--, value);
        }

        @Override
        public void write(byte[] value, int offset, int length) {
            if (this.spaceLeft() < length) {
                this.nextBuffer(length);
            }
            this.pos -= length;
            Java8Compatibility.position(this.buffer, this.pos + 1);
            this.buffer.put(value, offset, length);
        }

        @Override
        public void writeLazy(byte[] value, int offset, int length) {
            if (this.spaceLeft() < length) {
                this.totalDoneBytes += length;
                this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
                this.nextBuffer();
                return;
            }
            this.pos -= length;
            Java8Compatibility.position(this.buffer, this.pos + 1);
            this.buffer.put(value, offset, length);
        }

        @Override
        public void write(ByteBuffer value) {
            int length = value.remaining();
            if (this.spaceLeft() < length) {
                this.nextBuffer(length);
            }
            this.pos -= length;
            Java8Compatibility.position(this.buffer, this.pos + 1);
            this.buffer.put(value);
        }

        @Override
        public void writeLazy(ByteBuffer value) {
            int length = value.remaining();
            if (this.spaceLeft() < length) {
                this.totalDoneBytes += length;
                this.buffers.addFirst(AllocatedBuffer.wrap(value));
                this.nextBuffer();
                return;
            }
            this.pos -= length;
            Java8Compatibility.position(this.buffer, this.pos + 1);
            this.buffer.put(value);
        }

        @Override
        void requireSpace(int size) {
            if (this.spaceLeft() < size) {
                this.nextBuffer(size);
            }
        }
    }
}

