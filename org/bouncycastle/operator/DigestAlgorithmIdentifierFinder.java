/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface DigestAlgorithmIdentifierFinder {
    public AlgorithmIdentifier find(AlgorithmIdentifier var1);

    public AlgorithmIdentifier find(ASN1ObjectIdentifier var1);

    public AlgorithmIdentifier find(String var1);
}

