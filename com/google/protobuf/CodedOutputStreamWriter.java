/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.BooleanArrayList;
import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DoubleArrayList;
import com.google.protobuf.FloatArrayList;
import com.google.protobuf.IntArrayList;
import com.google.protobuf.Internal;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.LongArrayList;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Schema;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@CheckReturnValue
final class CodedOutputStreamWriter
implements Writer {
    private final CodedOutputStream output;

    public static CodedOutputStreamWriter forCodedOutput(CodedOutputStream output) {
        if (output.wrapper != null) {
            return (CodedOutputStreamWriter)output.wrapper;
        }
        return new CodedOutputStreamWriter(output);
    }

    private CodedOutputStreamWriter(CodedOutputStream output) {
        this.output = Internal.checkNotNull(output, "output");
        this.output.wrapper = this;
    }

    @Override
    public Writer.FieldOrder fieldOrder() {
        return Writer.FieldOrder.ASCENDING;
    }

    public int getTotalBytesWritten() {
        return this.output.getTotalBytesWritten();
    }

    @Override
    public void writeSFixed32(int fieldNumber, int value) throws IOException {
        this.output.writeSFixed32(fieldNumber, value);
    }

    @Override
    public void writeInt64(int fieldNumber, long value) throws IOException {
        this.output.writeInt64(fieldNumber, value);
    }

    @Override
    public void writeSFixed64(int fieldNumber, long value) throws IOException {
        this.output.writeSFixed64(fieldNumber, value);
    }

    @Override
    public void writeFloat(int fieldNumber, float value) throws IOException {
        this.output.writeFloat(fieldNumber, value);
    }

    @Override
    public void writeDouble(int fieldNumber, double value) throws IOException {
        this.output.writeDouble(fieldNumber, value);
    }

    @Override
    public void writeEnum(int fieldNumber, int value) throws IOException {
        this.output.writeEnum(fieldNumber, value);
    }

    @Override
    public void writeUInt64(int fieldNumber, long value) throws IOException {
        this.output.writeUInt64(fieldNumber, value);
    }

    @Override
    public void writeInt32(int fieldNumber, int value) throws IOException {
        this.output.writeInt32(fieldNumber, value);
    }

    @Override
    public void writeFixed64(int fieldNumber, long value) throws IOException {
        this.output.writeFixed64(fieldNumber, value);
    }

    @Override
    public void writeFixed32(int fieldNumber, int value) throws IOException {
        this.output.writeFixed32(fieldNumber, value);
    }

    @Override
    public void writeBool(int fieldNumber, boolean value) throws IOException {
        this.output.writeBool(fieldNumber, value);
    }

    @Override
    public void writeString(int fieldNumber, String value) throws IOException {
        this.output.writeString(fieldNumber, value);
    }

    @Override
    public void writeBytes(int fieldNumber, ByteString value) throws IOException {
        this.output.writeBytes(fieldNumber, value);
    }

    @Override
    public void writeUInt32(int fieldNumber, int value) throws IOException {
        this.output.writeUInt32(fieldNumber, value);
    }

    @Override
    public void writeSInt32(int fieldNumber, int value) throws IOException {
        this.output.writeSInt32(fieldNumber, value);
    }

    @Override
    public void writeSInt64(int fieldNumber, long value) throws IOException {
        this.output.writeSInt64(fieldNumber, value);
    }

    @Override
    public void writeMessage(int fieldNumber, Object value) throws IOException {
        this.output.writeMessage(fieldNumber, (MessageLite)value);
    }

    @Override
    public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
        AbstractMessageLite message = (AbstractMessageLite)value;
        this.output.writeTag(fieldNumber, 2);
        this.output.writeUInt32NoTag(message.getSerializedSize(schema));
        schema.writeTo(message, this);
    }

    @Override
    @Deprecated
    public void writeGroup(int fieldNumber, Object value) throws IOException {
        this.output.writeGroup(fieldNumber, (MessageLite)value);
    }

    @Override
    public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
        AbstractMessageLite message = (AbstractMessageLite)value;
        this.output.writeTag(fieldNumber, 3);
        schema.writeTo(message, this);
        this.output.writeTag(fieldNumber, 4);
    }

    @Override
    @Deprecated
    public void writeStartGroup(int fieldNumber) throws IOException {
        this.output.writeTag(fieldNumber, 3);
    }

    @Override
    @Deprecated
    public void writeEndGroup(int fieldNumber) throws IOException {
        this.output.writeTag(fieldNumber, 4);
    }

    @Override
    public final void writeMessageSetItem(int fieldNumber, Object value) throws IOException {
        if (value instanceof ByteString) {
            this.output.writeRawMessageSetExtension(fieldNumber, (ByteString)value);
        } else {
            this.output.writeMessageSetExtension(fieldNumber, (MessageLite)value);
        }
    }

    @Override
    public void writeInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (value instanceof IntArrayList) {
            this.writeInt32ListInternal(fieldNumber, (IntArrayList)value, packed);
        } else {
            this.writeInt32ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeInt32ListInternal(int fieldNumber, IntArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeInt32SizeNoTag(value.getInt(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeInt32NoTag(value.getInt(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeInt32(fieldNumber, value.getInt(i));
            }
        }
    }

    private void writeInt32ListInternal(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeInt32SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeInt32NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeInt32(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeFixed32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (value instanceof IntArrayList) {
            this.writeFixed32ListInternal(fieldNumber, (IntArrayList)value, packed);
        } else {
            this.writeFixed32ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeFixed32ListInternal(int fieldNumber, IntArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeFixed32SizeNoTag(value.getInt(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeFixed32NoTag(value.getInt(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeFixed32(fieldNumber, value.getInt(i));
            }
        }
    }

    private void writeFixed32ListInternal(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeFixed32SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeFixed32NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeFixed32(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (value instanceof LongArrayList) {
            this.writeInt64ListInternal(fieldNumber, (LongArrayList)value, packed);
        } else {
            this.writeInt64ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeInt64ListInternal(int fieldNumber, LongArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeInt64SizeNoTag(value.getLong(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeInt64NoTag(value.getLong(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeInt64(fieldNumber, value.getLong(i));
            }
        }
    }

    private void writeInt64ListInternal(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeInt64SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeInt64NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeInt64(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeUInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (value instanceof LongArrayList) {
            this.writeUInt64ListInternal(fieldNumber, (LongArrayList)value, packed);
        } else {
            this.writeUInt64ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeUInt64ListInternal(int fieldNumber, LongArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeUInt64SizeNoTag(value.getLong(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeUInt64NoTag(value.getLong(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeUInt64(fieldNumber, value.getLong(i));
            }
        }
    }

    private void writeUInt64ListInternal(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeUInt64SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeUInt64NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeUInt64(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeFixed64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (value instanceof LongArrayList) {
            this.writeFixed64ListInternal(fieldNumber, (LongArrayList)value, packed);
        } else {
            this.writeFixed64ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeFixed64ListInternal(int fieldNumber, LongArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeFixed64SizeNoTag(value.getLong(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeFixed64NoTag(value.getLong(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeFixed64(fieldNumber, value.getLong(i));
            }
        }
    }

    private void writeFixed64ListInternal(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeFixed64SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeFixed64NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeFixed64(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeFloatList(int fieldNumber, List<Float> value, boolean packed) throws IOException {
        if (value instanceof FloatArrayList) {
            this.writeFloatListInternal(fieldNumber, (FloatArrayList)value, packed);
        } else {
            this.writeFloatListInternal(fieldNumber, value, packed);
        }
    }

    private void writeFloatListInternal(int fieldNumber, FloatArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeFloatSizeNoTag(value.getFloat(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeFloatNoTag(value.getFloat(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeFloat(fieldNumber, value.getFloat(i));
            }
        }
    }

    private void writeFloatListInternal(int fieldNumber, List<Float> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeFloatSizeNoTag(value.get(i).floatValue());
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeFloatNoTag(value.get(i).floatValue());
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeFloat(fieldNumber, value.get(i).floatValue());
            }
        }
    }

    @Override
    public void writeDoubleList(int fieldNumber, List<Double> value, boolean packed) throws IOException {
        if (value instanceof DoubleArrayList) {
            this.writeDoubleListInternal(fieldNumber, (DoubleArrayList)value, packed);
        } else {
            this.writeDoubleListInternal(fieldNumber, value, packed);
        }
    }

    private void writeDoubleListInternal(int fieldNumber, DoubleArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeDoubleSizeNoTag(value.getDouble(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeDoubleNoTag(value.getDouble(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeDouble(fieldNumber, value.getDouble(i));
            }
        }
    }

    private void writeDoubleListInternal(int fieldNumber, List<Double> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeDoubleSizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeDoubleNoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeDouble(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeEnumList(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (value instanceof IntArrayList) {
            this.writeEnumListInternal(fieldNumber, (IntArrayList)value, packed);
        } else {
            this.writeEnumListInternal(fieldNumber, value, packed);
        }
    }

    private void writeEnumListInternal(int fieldNumber, IntArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeEnumSizeNoTag(value.getInt(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeEnumNoTag(value.getInt(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeEnum(fieldNumber, value.getInt(i));
            }
        }
    }

    private void writeEnumListInternal(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeEnumSizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeEnumNoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeEnum(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeBoolList(int fieldNumber, List<Boolean> value, boolean packed) throws IOException {
        if (value instanceof BooleanArrayList) {
            this.writeBoolListInternal(fieldNumber, (BooleanArrayList)value, packed);
        } else {
            this.writeBoolListInternal(fieldNumber, value, packed);
        }
    }

    private void writeBoolListInternal(int fieldNumber, BooleanArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeBoolSizeNoTag(value.getBoolean(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeBoolNoTag(value.getBoolean(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeBool(fieldNumber, value.getBoolean(i));
            }
        }
    }

    private void writeBoolListInternal(int fieldNumber, List<Boolean> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeBoolSizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeBoolNoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeBool(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeStringList(int fieldNumber, List<String> value) throws IOException {
        if (value instanceof LazyStringList) {
            LazyStringList lazyList = (LazyStringList)value;
            for (int i = 0; i < value.size(); ++i) {
                this.writeLazyString(fieldNumber, lazyList.getRaw(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeString(fieldNumber, value.get(i));
            }
        }
    }

    private void writeLazyString(int fieldNumber, Object value) throws IOException {
        if (value instanceof String) {
            this.output.writeString(fieldNumber, (String)value);
        } else {
            this.output.writeBytes(fieldNumber, (ByteString)value);
        }
    }

    @Override
    public void writeBytesList(int fieldNumber, List<ByteString> value) throws IOException {
        for (int i = 0; i < value.size(); ++i) {
            this.output.writeBytes(fieldNumber, value.get(i));
        }
    }

    @Override
    public void writeUInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (value instanceof IntArrayList) {
            this.writeUInt32ListInternal(fieldNumber, (IntArrayList)value, packed);
        } else {
            this.writeUInt32ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeUInt32ListInternal(int fieldNumber, IntArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeUInt32SizeNoTag(value.getInt(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeUInt32NoTag(value.getInt(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeUInt32(fieldNumber, value.getInt(i));
            }
        }
    }

    public void writeUInt32ListInternal(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeUInt32SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeUInt32NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeUInt32(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeSFixed32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (value instanceof IntArrayList) {
            this.writeSFixed32ListInternal(fieldNumber, (IntArrayList)value, packed);
        } else {
            this.writeSFixed32ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeSFixed32ListInternal(int fieldNumber, IntArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeSFixed32SizeNoTag(value.getInt(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeSFixed32NoTag(value.getInt(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeSFixed32(fieldNumber, value.getInt(i));
            }
        }
    }

    private void writeSFixed32ListInternal(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeSFixed32SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeSFixed32NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeSFixed32(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeSFixed64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (value instanceof LongArrayList) {
            this.writeSFixed64ListInternal(fieldNumber, (LongArrayList)value, packed);
        } else {
            this.writeSFixed64ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeSFixed64ListInternal(int fieldNumber, LongArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeSFixed64SizeNoTag(value.getLong(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeSFixed64NoTag(value.getLong(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeSFixed64(fieldNumber, value.getLong(i));
            }
        }
    }

    private void writeSFixed64ListInternal(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeSFixed64SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeSFixed64NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeSFixed64(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeSInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (value instanceof IntArrayList) {
            this.writeSInt32ListInternal(fieldNumber, (IntArrayList)value, packed);
        } else {
            this.writeSInt32ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeSInt32ListInternal(int fieldNumber, IntArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeSInt32SizeNoTag(value.getInt(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeSInt32NoTag(value.getInt(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeSInt32(fieldNumber, value.getInt(i));
            }
        }
    }

    public void writeSInt32ListInternal(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeSInt32SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeSInt32NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeSInt32(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeSInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (value instanceof LongArrayList) {
            this.writeSInt64ListInternal(fieldNumber, (LongArrayList)value, packed);
        } else {
            this.writeSInt64ListInternal(fieldNumber, value, packed);
        }
    }

    private void writeSInt64ListInternal(int fieldNumber, LongArrayList value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeSInt64SizeNoTag(value.getLong(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeSInt64NoTag(value.getLong(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeSInt64(fieldNumber, value.getLong(i));
            }
        }
    }

    private void writeSInt64ListInternal(int fieldNumber, List<Long> value, boolean packed) throws IOException {
        if (packed) {
            int i;
            this.output.writeTag(fieldNumber, 2);
            int dataSize = 0;
            for (i = 0; i < value.size(); ++i) {
                dataSize += CodedOutputStream.computeSInt64SizeNoTag(value.get(i));
            }
            this.output.writeUInt32NoTag(dataSize);
            for (i = 0; i < value.size(); ++i) {
                this.output.writeSInt64NoTag(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size(); ++i) {
                this.output.writeSInt64(fieldNumber, value.get(i));
            }
        }
    }

    @Override
    public void writeMessageList(int fieldNumber, List<?> value) throws IOException {
        for (int i = 0; i < value.size(); ++i) {
            this.writeMessage(fieldNumber, value.get(i));
        }
    }

    @Override
    public void writeMessageList(int fieldNumber, List<?> value, Schema schema) throws IOException {
        for (int i = 0; i < value.size(); ++i) {
            this.writeMessage(fieldNumber, value.get(i), schema);
        }
    }

    @Override
    @Deprecated
    public void writeGroupList(int fieldNumber, List<?> value) throws IOException {
        for (int i = 0; i < value.size(); ++i) {
            this.writeGroup(fieldNumber, value.get(i));
        }
    }

    @Override
    public void writeGroupList(int fieldNumber, List<?> value, Schema schema) throws IOException {
        for (int i = 0; i < value.size(); ++i) {
            this.writeGroup(fieldNumber, value.get(i), schema);
        }
    }

    @Override
    public <K, V> void writeMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
        if (this.output.isSerializationDeterministic()) {
            this.writeDeterministicMap(fieldNumber, metadata, map);
            return;
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            this.output.writeTag(fieldNumber, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, entry.getKey(), entry.getValue()));
            MapEntryLite.writeTo(this.output, metadata, entry.getKey(), entry.getValue());
        }
    }

    private <K, V> void writeDeterministicMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
        switch (metadata.keyType) {
            case BOOL: {
                V value = map.get(false);
                if (value != null) {
                    this.writeDeterministicBooleanMapEntry(fieldNumber, false, value, metadata);
                }
                if ((value = map.get(true)) == null) break;
                this.writeDeterministicBooleanMapEntry(fieldNumber, true, value, metadata);
                break;
            }
            case FIXED32: 
            case INT32: 
            case SFIXED32: 
            case SINT32: 
            case UINT32: {
                this.writeDeterministicIntegerMap(fieldNumber, metadata, map);
                break;
            }
            case FIXED64: 
            case INT64: 
            case SFIXED64: 
            case SINT64: 
            case UINT64: {
                this.writeDeterministicLongMap(fieldNumber, metadata, map);
                break;
            }
            case STRING: {
                this.writeDeterministicStringMap(fieldNumber, metadata, map);
                break;
            }
            default: {
                throw new IllegalArgumentException("does not support key type: " + (Object)((Object)metadata.keyType));
            }
        }
    }

    private <V> void writeDeterministicBooleanMapEntry(int fieldNumber, boolean key, V value, MapEntryLite.Metadata<Boolean, V> metadata) throws IOException {
        this.output.writeTag(fieldNumber, 2);
        this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, key, value));
        MapEntryLite.writeTo(this.output, metadata, key, value);
    }

    private <V> void writeDeterministicIntegerMap(int fieldNumber, MapEntryLite.Metadata<Integer, V> metadata, Map<Integer, V> map) throws IOException {
        int[] keys = new int[map.size()];
        int index = 0;
        Iterator<Integer> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            int k = iterator.next();
            keys[index++] = k;
        }
        Arrays.sort(keys);
        for (Object key : (Iterator<Integer>)keys) {
            V value = map.get((int)key);
            this.output.writeTag(fieldNumber, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, (int)key, value));
            MapEntryLite.writeTo(this.output, metadata, (int)key, value);
        }
    }

    private <V> void writeDeterministicLongMap(int fieldNumber, MapEntryLite.Metadata<Long, V> metadata, Map<Long, V> map) throws IOException {
        long[] keys = new long[map.size()];
        int index = 0;
        Iterator<Long> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            long k = iterator.next();
            keys[index++] = k;
        }
        Arrays.sort(keys);
        for (Object key : (Iterator<Long>)keys) {
            V value = map.get((long)key);
            this.output.writeTag(fieldNumber, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, (long)key, value));
            MapEntryLite.writeTo(this.output, metadata, (long)key, value);
        }
    }

    private <V> void writeDeterministicStringMap(int fieldNumber, MapEntryLite.Metadata<String, V> metadata, Map<String, V> map) throws IOException {
        Object[] keys = new String[map.size()];
        int index = 0;
        for (String k : map.keySet()) {
            keys[index++] = k;
        }
        Arrays.sort(keys);
        for (Object key : keys) {
            V value = map.get(key);
            this.output.writeTag(fieldNumber, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, key, value));
            MapEntryLite.writeTo(this.output, metadata, key, value);
        }
    }
}

