/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumKey;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumPublicKey;

public interface DilithiumPrivateKey
extends PrivateKey,
DilithiumKey {
    public DilithiumPublicKey getPublicKey();
}

