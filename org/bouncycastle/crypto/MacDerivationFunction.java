/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.Mac;

public interface MacDerivationFunction
extends DerivationFunction {
    public Mac getMac();
}

