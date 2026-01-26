/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface KemEncapsulationLengthProvider {
    public int getEncapsulationLength(AlgorithmIdentifier var1);
}

