/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.math.BigInteger;
import java.security.PublicKey;
import org.bouncycastle.jcajce.interfaces.XDHKey;

public interface XDHPublicKey
extends XDHKey,
PublicKey {
    public BigInteger getU();

    public byte[] getUEncoding();
}

