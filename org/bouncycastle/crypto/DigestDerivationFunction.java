/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.Digest;

public interface DigestDerivationFunction
extends DerivationFunction {
    public Digest getDigest();
}

