/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.jcajce.interfaces.MLDSAKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPublicKey;

public interface MLDSAPrivateKey
extends PrivateKey,
MLDSAKey {
    public MLDSAPublicKey getPublicKey();

    public byte[] getPrivateData();

    public byte[] getSeed();

    public MLDSAPrivateKey getPrivateKey(boolean var1);
}

