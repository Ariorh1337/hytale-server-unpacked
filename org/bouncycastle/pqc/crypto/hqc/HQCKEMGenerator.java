/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.hqc.HQCEngine;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;

public class HQCKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom sr;

    public HQCKEMGenerator(SecureRandom secureRandom) {
        this.sr = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        HQCPublicKeyParameters hQCPublicKeyParameters = (HQCPublicKeyParameters)asymmetricKeyParameter;
        HQCEngine hQCEngine = hQCPublicKeyParameters.getParameters().getEngine();
        byte[] byArray = new byte[hQCPublicKeyParameters.getParameters().getSHA512_BYTES()];
        byte[] byArray2 = new byte[hQCPublicKeyParameters.getParameters().getN_BYTES()];
        byte[] byArray3 = new byte[hQCPublicKeyParameters.getParameters().getN1N2_BYTES()];
        byte[] byArray4 = new byte[hQCPublicKeyParameters.getParameters().getSALT_SIZE_BYTES()];
        byte[] byArray5 = hQCPublicKeyParameters.getPublicKey();
        hQCEngine.encaps(byArray2, byArray3, byArray, byArray5, byArray4, this.sr);
        byte[] byArray6 = Arrays.concatenate(byArray2, byArray3, byArray4);
        return new SecretWithEncapsulationImpl(Arrays.copyOfRange(byArray, 0, 32), byArray6);
    }
}

