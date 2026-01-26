/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.Schema;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@CheckReturnValue
interface Reader {
    public static final int READ_DONE = Integer.MAX_VALUE;
    public static final int TAG_UNKNOWN = 0;

    public boolean shouldDiscardUnknownFields();

    public int getFieldNumber() throws IOException;

    public int getTag();

    public boolean skipField() throws IOException;

    public double readDouble() throws IOException;

    public float readFloat() throws IOException;

    public long readUInt64() throws IOException;

    public long readInt64() throws IOException;

    public int readInt32() throws IOException;

    public long readFixed64() throws IOException;

    public int readFixed32() throws IOException;

    public boolean readBool() throws IOException;

    public String readString() throws IOException;

    public String readStringRequireUtf8() throws IOException;

    public <T> T readMessageBySchemaWithCheck(Schema<T> var1, ExtensionRegistryLite var2) throws IOException;

    public <T> T readMessage(Class<T> var1, ExtensionRegistryLite var2) throws IOException;

    @Deprecated
    public <T> T readGroup(Class<T> var1, ExtensionRegistryLite var2) throws IOException;

    @Deprecated
    public <T> T readGroupBySchemaWithCheck(Schema<T> var1, ExtensionRegistryLite var2) throws IOException;

    public <T> void mergeMessageField(T var1, Schema<T> var2, ExtensionRegistryLite var3) throws IOException;

    public <T> void mergeGroupField(T var1, Schema<T> var2, ExtensionRegistryLite var3) throws IOException;

    public ByteString readBytes() throws IOException;

    public int readUInt32() throws IOException;

    public int readEnum() throws IOException;

    public int readSFixed32() throws IOException;

    public long readSFixed64() throws IOException;

    public int readSInt32() throws IOException;

    public long readSInt64() throws IOException;

    public void readDoubleList(List<Double> var1) throws IOException;

    public void readFloatList(List<Float> var1) throws IOException;

    public void readUInt64List(List<Long> var1) throws IOException;

    public void readInt64List(List<Long> var1) throws IOException;

    public void readInt32List(List<Integer> var1) throws IOException;

    public void readFixed64List(List<Long> var1) throws IOException;

    public void readFixed32List(List<Integer> var1) throws IOException;

    public void readBoolList(List<Boolean> var1) throws IOException;

    public void readStringList(List<String> var1) throws IOException;

    public void readStringListRequireUtf8(List<String> var1) throws IOException;

    public <T> void readMessageList(List<T> var1, Schema<T> var2, ExtensionRegistryLite var3) throws IOException;

    public <T> void readMessageList(List<T> var1, Class<T> var2, ExtensionRegistryLite var3) throws IOException;

    @Deprecated
    public <T> void readGroupList(List<T> var1, Class<T> var2, ExtensionRegistryLite var3) throws IOException;

    @Deprecated
    public <T> void readGroupList(List<T> var1, Schema<T> var2, ExtensionRegistryLite var3) throws IOException;

    public void readBytesList(List<ByteString> var1) throws IOException;

    public void readUInt32List(List<Integer> var1) throws IOException;

    public void readEnumList(List<Integer> var1) throws IOException;

    public void readSFixed32List(List<Integer> var1) throws IOException;

    public void readSFixed64List(List<Long> var1) throws IOException;

    public void readSInt32List(List<Integer> var1) throws IOException;

    public void readSInt64List(List<Long> var1) throws IOException;

    public <K, V> void readMap(Map<K, V> var1, MapEntryLite.Metadata<K, V> var2, ExtensionRegistryLite var3) throws IOException;
}

