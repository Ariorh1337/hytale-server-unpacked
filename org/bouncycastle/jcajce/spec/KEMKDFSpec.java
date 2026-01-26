/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class KEMKDFSpec {
    private final String keyAlgorithmName;
    private final int keySizeInBits;
    private final AlgorithmIdentifier kdfAlgorithm;
    private final byte[] otherInfo;

    protected KEMKDFSpec(AlgorithmIdentifier algorithmIdentifier, byte[] byArray, String string, int n) {
        this.keyAlgorithmName = string;
        this.keySizeInBits = n;
        this.kdfAlgorithm = algorithmIdentifier;
        this.otherInfo = byArray;
    }

    public String getKeyAlgorithmName() {
        return this.keyAlgorithmName;
    }

    public int getKeySize() {
        return this.keySizeInBits;
    }

    public AlgorithmIdentifier getKdfAlgorithm() {
        return this.kdfAlgorithm;
    }

    public byte[] getOtherInfo() {
        return Arrays.clone(this.otherInfo);
    }
}

