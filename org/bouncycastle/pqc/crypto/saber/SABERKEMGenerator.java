/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.saber.SABEREngine;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

public class SABERKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom sr;

    public SABERKEMGenerator(SecureRandom secureRandom) {
        this.sr = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        SABERPublicKeyParameters sABERPublicKeyParameters = (SABERPublicKeyParameters)asymmetricKeyParameter;
        SABEREngine sABEREngine = sABERPublicKeyParameters.getParameters().getEngine();
        byte[] byArray = new byte[sABEREngine.getCipherTextSize()];
        byte[] byArray2 = new byte[sABEREngine.getSessionKeySize()];
        sABEREngine.crypto_kem_enc(byArray, byArray2, sABERPublicKeyParameters.getPublicKey(), this.sr);
        return new SecretWithEncapsulationImpl(byArray2, byArray);
    }
}

