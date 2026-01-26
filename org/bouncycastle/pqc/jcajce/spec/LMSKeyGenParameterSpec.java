/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

public class LMSKeyGenParameterSpec
implements AlgorithmParameterSpec {
    private static final Map<String, LMSigParameters> sigParameters = new HashMap<String, LMSigParameters>();
    private static final Map<String, LMOtsParameters> otsParameters = new HashMap<String, LMOtsParameters>();
    private final LMSigParameters lmSigParams;
    private final LMOtsParameters lmOtsParameters;

    public LMSKeyGenParameterSpec(LMSigParameters lMSigParameters, LMOtsParameters lMOtsParameters) {
        this.lmSigParams = lMSigParameters;
        this.lmOtsParameters = lMOtsParameters;
    }

    public LMSigParameters getSigParams() {
        return this.lmSigParams;
    }

    public LMOtsParameters getOtsParams() {
        return this.lmOtsParameters;
    }

    public static LMSKeyGenParameterSpec fromNames(String string, String string2) {
        if (!sigParameters.containsKey(string)) {
            throw new IllegalArgumentException("LM signature parameter name " + string + " not recognized");
        }
        if (!otsParameters.containsKey(string2)) {
            throw new IllegalArgumentException("LM OTS parameter name " + string2 + " not recognized");
        }
        return new LMSKeyGenParameterSpec(sigParameters.get(string), otsParameters.get(string2));
    }

    static {
        sigParameters.put("lms-sha256-n32-h5", LMSigParameters.lms_sha256_n32_h5);
        sigParameters.put("lms-sha256-n32-h10", LMSigParameters.lms_sha256_n32_h10);
        sigParameters.put("lms-sha256-n32-h15", LMSigParameters.lms_sha256_n32_h15);
        sigParameters.put("lms-sha256-n32-h20", LMSigParameters.lms_sha256_n32_h20);
        sigParameters.put("lms-sha256-n32-h25", LMSigParameters.lms_sha256_n32_h25);
        sigParameters.put("lms-sha256-n24-h5", LMSigParameters.lms_sha256_n24_h5);
        sigParameters.put("lms-sha256-n24-h10", LMSigParameters.lms_sha256_n24_h10);
        sigParameters.put("lms-sha256-n24-h15", LMSigParameters.lms_sha256_n24_h15);
        sigParameters.put("lms-sha256-n24-h20", LMSigParameters.lms_sha256_n24_h20);
        sigParameters.put("lms-sha256-n24-h25", LMSigParameters.lms_sha256_n24_h25);
        sigParameters.put("lms-shake256-n32-h5", LMSigParameters.lms_shake256_n32_h5);
        sigParameters.put("lms-shake256-n32-h10", LMSigParameters.lms_shake256_n32_h10);
        sigParameters.put("lms-shake256-n32-h15", LMSigParameters.lms_shake256_n32_h15);
        sigParameters.put("lms-shake256-n32-h20", LMSigParameters.lms_shake256_n32_h20);
        sigParameters.put("lms-shake256-n32-h25", LMSigParameters.lms_shake256_n32_h25);
        sigParameters.put("lms-shake256-n24-h5", LMSigParameters.lms_shake256_n24_h5);
        sigParameters.put("lms-shake256-n24-h10", LMSigParameters.lms_shake256_n24_h10);
        sigParameters.put("lms-shake256-n24-h15", LMSigParameters.lms_shake256_n24_h15);
        sigParameters.put("lms-shake256-n24-h20", LMSigParameters.lms_shake256_n24_h20);
        sigParameters.put("lms-shake256-n24-h25", LMSigParameters.lms_shake256_n24_h25);
        otsParameters.put("sha256-n32-w1", LMOtsParameters.sha256_n32_w1);
        otsParameters.put("sha256-n32-w2", LMOtsParameters.sha256_n32_w2);
        otsParameters.put("sha256-n32-w4", LMOtsParameters.sha256_n32_w4);
        otsParameters.put("sha256-n32-w8", LMOtsParameters.sha256_n32_w8);
    }
}

