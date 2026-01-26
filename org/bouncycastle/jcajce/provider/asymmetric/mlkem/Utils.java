/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;

class Utils {
    private static Map parameters = new HashMap();

    Utils() {
    }

    static MLKEMParameters getParameters(String string) {
        return (MLKEMParameters)parameters.get(string);
    }

    static {
        parameters.put(MLKEMParameterSpec.ml_kem_512.getName(), MLKEMParameters.ml_kem_512);
        parameters.put(MLKEMParameterSpec.ml_kem_768.getName(), MLKEMParameters.ml_kem_768);
        parameters.put(MLKEMParameterSpec.ml_kem_1024.getName(), MLKEMParameters.ml_kem_1024);
    }
}

