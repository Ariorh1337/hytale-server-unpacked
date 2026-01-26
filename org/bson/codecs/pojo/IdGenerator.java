/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

public interface IdGenerator<T> {
    public T generate();

    public Class<T> getType();
}

