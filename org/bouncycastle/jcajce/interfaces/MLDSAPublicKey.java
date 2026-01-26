/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PublicKey;
import org.bouncycastle.jcajce.interfaces.MLDSAKey;

public interface MLDSAPublicKey
extends PublicKey,
MLDSAKey {
    public byte[] getPublicData();
}

