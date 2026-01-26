/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.cmce.CMCEEngine;
import org.bouncycastle.pqc.crypto.cmce.CMCEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

public class CMCEKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom sr;

    public CMCEKEMGenerator(SecureRandom secureRandom) {
        this.sr = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        CMCEPublicKeyParameters cMCEPublicKeyParameters = (CMCEPublicKeyParameters)asymmetricKeyParameter;
        CMCEEngine cMCEEngine = cMCEPublicKeyParameters.getParameters().getEngine();
        return this.generateEncapsulated(asymmetricKeyParameter, cMCEEngine.getDefaultSessionKeySize());
    }

    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter, int n) {
        CMCEPublicKeyParameters cMCEPublicKeyParameters = (CMCEPublicKeyParameters)asymmetricKeyParameter;
        CMCEEngine cMCEEngine = cMCEPublicKeyParameters.getParameters().getEngine();
        byte[] byArray = new byte[cMCEEngine.getCipherTextSize()];
        byte[] byArray2 = new byte[n / 8];
        cMCEEngine.kem_enc(byArray, byArray2, cMCEPublicKeyParameters.getPublicKey(), this.sr);
        return new SecretWithEncapsulationImpl(byArray2, byArray);
    }
}

