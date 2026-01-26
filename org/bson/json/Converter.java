/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.json.StrictJsonWriter;

public interface Converter<T> {
    public void convert(T var1, StrictJsonWriter var2);
}

