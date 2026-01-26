/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.GCMParameterSpec;

class ClassUtil {
    ClassUtil() {
    }

    public static AlgorithmParameterSpec getGCMSpec(byte[] byArray, int n) {
        return new GCMParameterSpec(n, byArray);
    }
}

