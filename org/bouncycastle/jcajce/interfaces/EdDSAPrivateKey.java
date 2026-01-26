/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.jcajce.interfaces.EdDSAKey;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;

public interface EdDSAPrivateKey
extends EdDSAKey,
PrivateKey {
    public EdDSAPublicKey getPublicKey();
}

