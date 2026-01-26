/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.frodo.FrodoEngine;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

public class FrodoKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom sr;

    public FrodoKEMGenerator(SecureRandom secureRandom) {
        this.sr = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        FrodoPublicKeyParameters frodoPublicKeyParameters = (FrodoPublicKeyParameters)asymmetricKeyParameter;
        FrodoEngine frodoEngine = frodoPublicKeyParameters.getParameters().getEngine();
        byte[] byArray = new byte[frodoEngine.getCipherTextSize()];
        byte[] byArray2 = new byte[frodoEngine.getSessionKeySize()];
        frodoEngine.kem_enc(byArray, byArray2, frodoPublicKeyParameters.getPublicKey(), this.sr);
        return new SecretWithEncapsulationImpl(byArray2, byArray);
    }
}

