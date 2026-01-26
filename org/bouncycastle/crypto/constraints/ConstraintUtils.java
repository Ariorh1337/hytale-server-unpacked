/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;

public class ConstraintUtils {
    public static int bitsOfSecurityFor(BigInteger bigInteger) {
        return ConstraintUtils.bitsOfSecurityForFF(bigInteger.bitLength());
    }

    public static int bitsOfSecurityFor(ECCurve eCCurve) {
        int n = (eCCurve.getFieldSize() + 1) / 2;
        return n > 256 ? 256 : n;
    }

    public static int bitsOfSecurityForFF(int n) {
        if (n >= 2048) {
            return n >= 3072 ? (n >= 7680 ? (n >= 15360 ? 256 : 192) : 128) : 112;
        }
        return n >= 1024 ? 80 : 20;
    }
}

