/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CryptoServicePurpose;

class Utils {
    Utils() {
    }

    static CryptoServicePurpose getPurpose(boolean bl) {
        return bl ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION;
    }
}

