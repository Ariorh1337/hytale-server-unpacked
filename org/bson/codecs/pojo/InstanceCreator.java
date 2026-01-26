/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import org.bson.codecs.pojo.PropertyModel;

public interface InstanceCreator<T> {
    public <S> void set(S var1, PropertyModel<S> var2);

    public T getInstance();
}

