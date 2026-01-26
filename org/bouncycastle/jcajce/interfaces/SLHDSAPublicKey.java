/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PublicKey;
import org.bouncycastle.jcajce.interfaces.SLHDSAKey;

public interface SLHDSAPublicKey
extends PublicKey,
SLHDSAKey {
    public byte[] getPublicData();
}

