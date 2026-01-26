/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.io.Closeable;
import org.bson.BsonBinary;
import org.bson.BsonDbPointer;
import org.bson.BsonReaderMark;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public interface BsonReader
extends Closeable {
    public BsonType getCurrentBsonType();

    public String getCurrentName();

    public BsonBinary readBinaryData();

    public byte peekBinarySubType();

    public int peekBinarySize();

    public BsonBinary readBinaryData(String var1);

    public boolean readBoolean();

    public boolean readBoolean(String var1);

    public BsonType readBsonType();

    public long readDateTime();

    public long readDateTime(String var1);

    public double readDouble();

    public double readDouble(String var1);

    public void readEndArray();

    public void readEndDocument();

    public int readInt32();

    public int readInt32(String var1);

    public long readInt64();

    public long readInt64(String var1);

    public Decimal128 readDecimal128();

    public Decimal128 readDecimal128(String var1);

    public String readJavaScript();

    public String readJavaScript(String var1);

    public String readJavaScriptWithScope();

    public String readJavaScriptWithScope(String var1);

    public void readMaxKey();

    public void readMaxKey(String var1);

    public void readMinKey();

    public void readMinKey(String var1);

    public String readName();

    public void readName(String var1);

    public void readNull();

    public void readNull(String var1);

    public ObjectId readObjectId();

    public ObjectId readObjectId(String var1);

    public BsonRegularExpression readRegularExpression();

    public BsonRegularExpression readRegularExpression(String var1);

    public BsonDbPointer readDBPointer();

    public BsonDbPointer readDBPointer(String var1);

    public void readStartArray();

    public void readStartDocument();

    public String readString();

    public String readString(String var1);

    public String readSymbol();

    public String readSymbol(String var1);

    public BsonTimestamp readTimestamp();

    public BsonTimestamp readTimestamp(String var1);

    public void readUndefined();

    public void readUndefined(String var1);

    public void skipName();

    public void skipValue();

    public BsonReaderMark getMark();

    @Override
    public void close();
}

