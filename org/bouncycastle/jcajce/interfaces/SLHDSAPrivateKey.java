/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.jcajce.interfaces.SLHDSAKey;
import org.bouncycastle.jcajce.interfaces.SLHDSAPublicKey;

public interface SLHDSAPrivateKey
extends PrivateKey,
SLHDSAKey {
    public SLHDSAPublicKey getPublicKey();
}

