/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.InputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface InputExpander {
    public AlgorithmIdentifier getAlgorithmIdentifier();

    public InputStream getInputStream(InputStream var1);
}

