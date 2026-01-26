/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public interface BSONCallback {
    public void objectStart();

    public void objectStart(String var1);

    public Object objectDone();

    public void reset();

    public Object get();

    public BSONCallback createBSONCallback();

    public void arrayStart();

    public void arrayStart(String var1);

    public Object arrayDone();

    public void gotNull(String var1);

    public void gotUndefined(String var1);

    public void gotMinKey(String var1);

    public void gotMaxKey(String var1);

    public void gotBoolean(String var1, boolean var2);

    public void gotDouble(String var1, double var2);

    public void gotDecimal128(String var1, Decimal128 var2);

    public void gotInt(String var1, int var2);

    public void gotLong(String var1, long var2);

    public void gotDate(String var1, long var2);

    public void gotString(String var1, String var2);

    public void gotSymbol(String var1, String var2);

    public void gotRegex(String var1, String var2, String var3);

    public void gotTimestamp(String var1, int var2, int var3);

    public void gotObjectId(String var1, ObjectId var2);

    public void gotDBRef(String var1, String var2, ObjectId var3);

    public void gotBinary(String var1, byte var2, byte[] var3);

    public void gotUUID(String var1, long var2, long var4);

    public void gotCode(String var1, String var2);

    public void gotCodeWScope(String var1, String var2, Object var3);
}

