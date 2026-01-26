/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.NHKey;

public interface NHPrivateKey
extends NHKey,
PrivateKey {
    public short[] getSecretData();
}

