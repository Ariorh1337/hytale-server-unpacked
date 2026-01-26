/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

interface JsonBuffer {
    public int getPosition();

    public int read();

    public void unread(int var1);

    public int mark();

    public void reset(int var1);

    public void discard(int var1);
}

