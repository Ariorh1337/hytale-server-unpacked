/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.Schema;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@CheckReturnValue
interface Writer {
    public FieldOrder fieldOrder();

    public void writeSFixed32(int var1, int var2) throws IOException;

    public void writeInt64(int var1, long var2) throws IOException;

    public void writeSFixed64(int var1, long var2) throws IOException;

    public void writeFloat(int var1, float var2) throws IOException;

    public void writeDouble(int var1, double var2) throws IOException;

    public void writeEnum(int var1, int var2) throws IOException;

    public void writeUInt64(int var1, long var2) throws IOException;

    public void writeInt32(int var1, int var2) throws IOException;

    public void writeFixed64(int var1, long var2) throws IOException;

    public void writeFixed32(int var1, int var2) throws IOException;

    public void writeBool(int var1, boolean var2) throws IOException;

    public void writeString(int var1, String var2) throws IOException;

    public void writeBytes(int var1, ByteString var2) throws IOException;

    public void writeUInt32(int var1, int var2) throws IOException;

    public void writeSInt32(int var1, int var2) throws IOException;

    public void writeSInt64(int var1, long var2) throws IOException;

    public void writeMessage(int var1, Object var2) throws IOException;

    public void writeMessage(int var1, Object var2, Schema var3) throws IOException;

    @Deprecated
    public void writeGroup(int var1, Object var2) throws IOException;

    @Deprecated
    public void writeGroup(int var1, Object var2, Schema var3) throws IOException;

    @Deprecated
    public void writeStartGroup(int var1) throws IOException;

    @Deprecated
    public void writeEndGroup(int var1) throws IOException;

    public void writeInt32List(int var1, List<Integer> var2, boolean var3) throws IOException;

    public void writeFixed32List(int var1, List<Integer> var2, boolean var3) throws IOException;

    public void writeInt64List(int var1, List<Long> var2, boolean var3) throws IOException;

    public void writeUInt64List(int var1, List<Long> var2, boolean var3) throws IOException;

    public void writeFixed64List(int var1, List<Long> var2, boolean var3) throws IOException;

    public void writeFloatList(int var1, List<Float> var2, boolean var3) throws IOException;

    public void writeDoubleList(int var1, List<Double> var2, boolean var3) throws IOException;

    public void writeEnumList(int var1, List<Integer> var2, boolean var3) throws IOException;

    public void writeBoolList(int var1, List<Boolean> var2, boolean var3) throws IOException;

    public void writeStringList(int var1, List<String> var2) throws IOException;

    public void writeBytesList(int var1, List<ByteString> var2) throws IOException;

    public void writeUInt32List(int var1, List<Integer> var2, boolean var3) throws IOException;

    public void writeSFixed32List(int var1, List<Integer> var2, boolean var3) throws IOException;

    public void writeSFixed64List(int var1, List<Long> var2, boolean var3) throws IOException;

    public void writeSInt32List(int var1, List<Integer> var2, boolean var3) throws IOException;

    public void writeSInt64List(int var1, List<Long> var2, boolean var3) throws IOException;

    public void writeMessageList(int var1, List<?> var2) throws IOException;

    public void writeMessageList(int var1, List<?> var2, Schema var3) throws IOException;

    @Deprecated
    public void writeGroupList(int var1, List<?> var2) throws IOException;

    @Deprecated
    public void writeGroupList(int var1, List<?> var2, Schema var3) throws IOException;

    public void writeMessageSetItem(int var1, Object var2) throws IOException;

    public <K, V> void writeMap(int var1, MapEntryLite.Metadata<K, V> var2, Map<K, V> var3) throws IOException;

    public static enum FieldOrder {
        ASCENDING,
        DESCENDING;

    }
}

