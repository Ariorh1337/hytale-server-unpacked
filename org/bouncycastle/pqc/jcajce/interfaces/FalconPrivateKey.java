/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.FalconKey;
import org.bouncycastle.pqc.jcajce.interfaces.FalconPublicKey;

public interface FalconPrivateKey
extends PrivateKey,
FalconKey {
    public FalconPublicKey getPublicKey();
}

