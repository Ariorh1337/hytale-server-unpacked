/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.util.BigIntegers;

class ECUtil {
    ECUtil() {
    }

    static BigInteger generateK(BigInteger bigInteger, SecureRandom secureRandom) {
        BigInteger bigInteger2;
        int n = bigInteger.bitLength();
        while ((bigInteger2 = BigIntegers.createRandomBigInteger(n, secureRandom)).equals(ECConstants.ZERO) || bigInteger2.compareTo(bigInteger) >= 0) {
        }
        return bigInteger2;
    }
}

