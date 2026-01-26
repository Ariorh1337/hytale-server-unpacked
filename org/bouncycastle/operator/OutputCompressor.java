/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface OutputCompressor {
    public AlgorithmIdentifier getAlgorithmIdentifier();

    public OutputStream getOutputStream(OutputStream var1);
}

