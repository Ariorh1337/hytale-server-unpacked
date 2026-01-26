/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface CMSSignatureEncryptionAlgorithmFinder {
    public AlgorithmIdentifier findEncryptionAlgorithm(AlgorithmIdentifier var1);
}

