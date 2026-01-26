/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;

class DHUtil {
    DHUtil() {
    }

    static String privateKeyToString(String string, BigInteger bigInteger, DHParameters dHParameters) {
        StringBuilder stringBuilder = new StringBuilder();
        String string2 = Strings.lineSeparator();
        BigInteger bigInteger2 = dHParameters.getG().modPow(bigInteger, dHParameters.getP());
        stringBuilder.append(string);
        stringBuilder.append(" Private Key [").append(DHUtil.generateKeyFingerprint(bigInteger2, dHParameters)).append("]").append(string2);
        stringBuilder.append("              Y: ").append(bigInteger2.toString(16)).append(string2);
        return stringBuilder.toString();
    }

    static String publicKeyToString(String string, BigInteger bigInteger, DHParameters dHParameters) {
        StringBuilder stringBuilder = new StringBuilder();
        String string2 = Strings.lineSeparator();
        stringBuilder.append(string);
        stringBuilder.append(" Public Key [").append(DHUtil.generateKeyFingerprint(bigInteger, dHParameters)).append("]").append(string2);
        stringBuilder.append("             Y: ").append(bigInteger.toString(16)).append(string2);
        return stringBuilder.toString();
    }

    private static String generateKeyFingerprint(BigInteger bigInteger, DHParameters dHParameters) {
        return new Fingerprint(Arrays.concatenate(bigInteger.toByteArray(), dHParameters.getP().toByteArray(), dHParameters.getG().toByteArray())).toString();
    }
}

