/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.operator.OperatorCreationException;

public interface PEMDecryptorProvider {
    public PEMDecryptor get(String var1) throws OperatorCreationException;
}

