/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.EncodableService;

public interface EncodableDigest
extends EncodableService {
    @Override
    public byte[] getEncodedState();
}

