/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.util.Strings;

public class MLKEMParameterSpec
implements AlgorithmParameterSpec {
    public static final MLKEMParameterSpec ml_kem_512 = new MLKEMParameterSpec("ML-KEM-512");
    public static final MLKEMParameterSpec ml_kem_768 = new MLKEMParameterSpec("ML-KEM-768");
    public static final MLKEMParameterSpec ml_kem_1024 = new MLKEMParameterSpec("ML-KEM-1024");
    private static Map parameters = new HashMap();
    private final String name;

    private MLKEMParameterSpec(String string) {
        this.name = string;
    }

    public String getName() {
        return this.name;
    }

    public static MLKEMParameterSpec fromName(String string) {
        if (string == null) {
            throw new NullPointerException("name cannot be null");
        }
        MLKEMParameterSpec mLKEMParameterSpec = (MLKEMParameterSpec)parameters.get(Strings.toLowerCase(string));
        if (mLKEMParameterSpec == null) {
            throw new IllegalArgumentException("unknown parameter name: " + string);
        }
        return mLKEMParameterSpec;
    }

    static {
        parameters.put("ml-kem-512", ml_kem_512);
        parameters.put("ml-kem-768", ml_kem_768);
        parameters.put("ml-kem-1024", ml_kem_1024);
        parameters.put("kyber512", ml_kem_512);
        parameters.put("kyber768", ml_kem_768);
        parameters.put("kyber1024", ml_kem_1024);
    }
}

