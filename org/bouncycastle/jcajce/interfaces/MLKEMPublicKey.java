/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PublicKey;
import org.bouncycastle.jcajce.interfaces.MLKEMKey;

public interface MLKEMPublicKey
extends PublicKey,
MLKEMKey {
    public byte[] getPublicData();
}

