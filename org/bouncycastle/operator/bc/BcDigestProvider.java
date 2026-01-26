/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.operator.OperatorCreationException;

public interface BcDigestProvider {
    public ExtendedDigest get(AlgorithmIdentifier var1) throws OperatorCreationException;
}

