/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.AEADCipher;

public interface AEADBlockCipher
extends AEADCipher {
    public BlockCipher getUnderlyingCipher();
}

