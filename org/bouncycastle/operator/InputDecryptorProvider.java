/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.OperatorCreationException;

public interface InputDecryptorProvider {
    public InputDecryptor get(AlgorithmIdentifier var1) throws OperatorCreationException;
}

