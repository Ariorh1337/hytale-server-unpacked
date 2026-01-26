/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMEngine;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

public class MLKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom sr;

    public MLKEMGenerator(SecureRandom secureRandom) {
        this.sr = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        byte[] byArray = new byte[32];
        this.sr.nextBytes(byArray);
        return this.internalGenerateEncapsulated(asymmetricKeyParameter, byArray);
    }

    public SecretWithEncapsulation internalGenerateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter, byte[] byArray) {
        MLKEMPublicKeyParameters mLKEMPublicKeyParameters = (MLKEMPublicKeyParameters)asymmetricKeyParameter;
        MLKEMEngine mLKEMEngine = mLKEMPublicKeyParameters.getParameters().getEngine();
        mLKEMEngine.init(this.sr);
        byte[][] byArray2 = mLKEMEngine.kemEncrypt(mLKEMPublicKeyParameters, byArray);
        return new SecretWithEncapsulationImpl(byArray2[0], byArray2[1]);
    }
}

