/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import java.io.IOException;
import org.bouncycastle.openssl.PEMKeyPair;

interface PEMKeyPairParser {
    public PEMKeyPair parse(byte[] var1) throws IOException;
}

