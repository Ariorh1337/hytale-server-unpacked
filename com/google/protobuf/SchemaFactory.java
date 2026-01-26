/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.Schema;

@CheckReturnValue
interface SchemaFactory {
    public <T> Schema<T> createSchema(Class<T> var1);
}

