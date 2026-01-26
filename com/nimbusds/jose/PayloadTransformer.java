/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Payload;

public interface PayloadTransformer<T> {
    public T transform(Payload var1);
}

