/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;

public interface MacCalculator {
    public AlgorithmIdentifier getAlgorithmIdentifier();

    public OutputStream getOutputStream();

    public byte[] getMac();

    public GenericKey getKey();
}

