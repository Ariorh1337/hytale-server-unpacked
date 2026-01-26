/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.jcajce.interfaces.XDHKey;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;

public interface XDHPrivateKey
extends XDHKey,
PrivateKey {
    public XDHPublicKey getPublicKey();
}

