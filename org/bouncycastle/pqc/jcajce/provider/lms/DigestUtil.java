/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.lms;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;

class DigestUtil {
    DigestUtil() {
    }

    public static byte[] getDigestResult(Digest digest) {
        byte[] byArray = new byte[digest.getDigestSize()];
        if (digest instanceof Xof) {
            ((Xof)digest).doFinal(byArray, 0, byArray.length);
        } else {
            digest.doFinal(byArray, 0);
        }
        return byArray;
    }
}

