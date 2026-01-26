/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

public interface PropertyAccessor<T> {
    public <S> T get(S var1);

    public <S> void set(S var1, T var2);
}

