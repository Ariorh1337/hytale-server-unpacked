/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.dane;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;

public class TruncatingDigestCalculator
implements DigestCalculator {
    private final DigestCalculator baseCalculator;
    private final int length;

    public TruncatingDigestCalculator(DigestCalculator digestCalculator) {
        this(digestCalculator, 28);
    }

    public TruncatingDigestCalculator(DigestCalculator digestCalculator, int n) {
        this.baseCalculator = digestCalculator;
        this.length = n;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.baseCalculator.getAlgorithmIdentifier();
    }

    @Override
    public OutputStream getOutputStream() {
        return this.baseCalculator.getOutputStream();
    }

    @Override
    public byte[] getDigest() {
        byte[] byArray = new byte[this.length];
        byte[] byArray2 = this.baseCalculator.getDigest();
        System.arraycopy(byArray2, 0, byArray, 0, byArray.length);
        return byArray;
    }
}

