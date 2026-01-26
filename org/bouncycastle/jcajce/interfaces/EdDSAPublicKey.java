/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PublicKey;
import org.bouncycastle.jcajce.interfaces.EdDSAKey;

public interface EdDSAPublicKey
extends EdDSAKey,
PublicKey {
    public byte[] getPointEncoding();
}

