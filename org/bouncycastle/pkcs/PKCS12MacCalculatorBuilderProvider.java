/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public interface PKCS12MacCalculatorBuilderProvider {
    public PKCS12MacCalculatorBuilder get(AlgorithmIdentifier var1);
}

