/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle.prf;

import com.google.errorprone.annotations.Immutable;
import java.io.InputStream;

@Immutable
public interface StreamingPrf {
    public InputStream computePrf(byte[] var1);
}

