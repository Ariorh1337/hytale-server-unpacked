/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

public interface StrictJsonWriter {
    public void writeName(String var1);

    public void writeBoolean(boolean var1);

    public void writeBoolean(String var1, boolean var2);

    public void writeNumber(String var1);

    public void writeNumber(String var1, String var2);

    public void writeString(String var1);

    public void writeString(String var1, String var2);

    public void writeRaw(String var1);

    public void writeRaw(String var1, String var2);

    public void writeNull();

    public void writeNull(String var1);

    public void writeStartArray();

    public void writeStartArray(String var1);

    public void writeStartObject();

    public void writeStartObject(String var1);

    public void writeEndArray();

    public void writeEndObject();

    public boolean isTruncated();
}

