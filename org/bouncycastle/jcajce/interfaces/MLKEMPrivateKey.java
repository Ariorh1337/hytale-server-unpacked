/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.jcajce.interfaces.MLKEMKey;
import org.bouncycastle.jcajce.interfaces.MLKEMPublicKey;

public interface MLKEMPrivateKey
extends PrivateKey,
MLKEMKey {
    public MLKEMPublicKey getPublicKey();

    public byte[] getPrivateData();

    public byte[] getSeed();

    public MLKEMPrivateKey getPrivateKey(boolean var1);
}

