/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.SPHINCSPlusKey;
import org.bouncycastle.pqc.jcajce.interfaces.SPHINCSPlusPublicKey;

public interface SPHINCSPlusPrivateKey
extends PrivateKey,
SPHINCSPlusKey {
    public SPHINCSPlusPublicKey getPublicKey();
}

