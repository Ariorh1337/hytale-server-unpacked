/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.KyberKey;
import org.bouncycastle.pqc.jcajce.interfaces.KyberPublicKey;

public interface KyberPrivateKey
extends PrivateKey,
KyberKey {
    public KyberPublicKey getPublicKey();
}

