/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;

@Immutable
public interface Serialization {
    public Bytes getObjectIdentifier();
}

