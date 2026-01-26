/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.bike.BIKEEngine;
import org.bouncycastle.pqc.crypto.bike.BIKEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;

public class BIKEKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom sr;

    public BIKEKEMGenerator(SecureRandom secureRandom) {
        this.sr = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        BIKEPublicKeyParameters bIKEPublicKeyParameters = (BIKEPublicKeyParameters)asymmetricKeyParameter;
        BIKEEngine bIKEEngine = bIKEPublicKeyParameters.getParameters().getEngine();
        byte[] byArray = new byte[bIKEPublicKeyParameters.getParameters().getLByte()];
        byte[] byArray2 = new byte[bIKEPublicKeyParameters.getParameters().getRByte()];
        byte[] byArray3 = new byte[bIKEPublicKeyParameters.getParameters().getLByte()];
        byte[] byArray4 = bIKEPublicKeyParameters.publicKey;
        bIKEEngine.encaps(byArray2, byArray3, byArray, byArray4, this.sr);
        byte[] byArray5 = Arrays.concatenate(byArray2, byArray3);
        return new SecretWithEncapsulationImpl(Arrays.copyOfRange(byArray, 0, bIKEPublicKeyParameters.getParameters().getSessionKeySize() / 8), byArray5);
    }
}

