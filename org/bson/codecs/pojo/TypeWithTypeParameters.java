/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.List;

public interface TypeWithTypeParameters<T> {
    public Class<T> getType();

    public List<? extends TypeWithTypeParameters<?>> getTypeParameters();
}

