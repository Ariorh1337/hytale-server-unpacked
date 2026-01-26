/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonBinary;
import org.bson.BsonDbPointer;
import org.bson.BsonReader;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public interface BsonWriter {
    public void flush();

    public void writeBinaryData(BsonBinary var1);

    public void writeBinaryData(String var1, BsonBinary var2);

    public void writeBoolean(boolean var1);

    public void writeBoolean(String var1, boolean var2);

    public void writeDateTime(long var1);

    public void writeDateTime(String var1, long var2);

    public void writeDBPointer(BsonDbPointer var1);

    public void writeDBPointer(String var1, BsonDbPointer var2);

    public void writeDouble(double var1);

    public void writeDouble(String var1, double var2);

    public void writeEndArray();

    public void writeEndDocument();

    public void writeInt32(int var1);

    public void writeInt32(String var1, int var2);

    public void writeInt64(long var1);

    public void writeInt64(String var1, long var2);

    public void writeDecimal128(Decimal128 var1);

    public void writeDecimal128(String var1, Decimal128 var2);

    public void writeJavaScript(String var1);

    public void writeJavaScript(String var1, String var2);

    public void writeJavaScriptWithScope(String var1);

    public void writeJavaScriptWithScope(String var1, String var2);

    public void writeMaxKey();

    public void writeMaxKey(String var1);

    public void writeMinKey();

    public void writeMinKey(String var1);

    public void writeName(String var1);

    public void writeNull();

    public void writeNull(String var1);

    public void writeObjectId(ObjectId var1);

    public void writeObjectId(String var1, ObjectId var2);

    public void writeRegularExpression(BsonRegularExpression var1);

    public void writeRegularExpression(String var1, BsonRegularExpression var2);

    public void writeStartArray();

    public void writeStartArray(String var1);

    public void writeStartDocument();

    public void writeStartDocument(String var1);

    public void writeString(String var1);

    public void writeString(String var1, String var2);

    public void writeSymbol(String var1);

    public void writeSymbol(String var1, String var2);

    public void writeTimestamp(BsonTimestamp var1);

    public void writeTimestamp(String var1, BsonTimestamp var2);

    public void writeUndefined();

    public void writeUndefined(String var1);

    public void pipe(BsonReader var1);
}

