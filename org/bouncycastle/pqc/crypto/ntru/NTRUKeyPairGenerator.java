/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUOWCPA;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.OWCPAKeyPair;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

public class NTRUKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private NTRUKeyGenerationParameters params;
    private SecureRandom random;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (NTRUKeyGenerationParameters)keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        NTRUParameters nTRUParameters = this.params.getParameters();
        NTRUParameterSet nTRUParameterSet = nTRUParameters.getParameterSet();
        byte[] byArray = new byte[nTRUParameterSet.sampleFgBytes()];
        this.random.nextBytes(byArray);
        NTRUOWCPA nTRUOWCPA = new NTRUOWCPA(nTRUParameterSet);
        OWCPAKeyPair oWCPAKeyPair = nTRUOWCPA.keypair(byArray);
        byte[] byArray2 = oWCPAKeyPair.publicKey;
        byte[] byArray3 = new byte[nTRUParameterSet.prfKeyBytes()];
        this.random.nextBytes(byArray3);
        byte[] byArray4 = Arrays.concatenate(oWCPAKeyPair.privateKey, byArray3);
        return new AsymmetricCipherKeyPair(new NTRUPublicKeyParameters(nTRUParameters, byArray2), new NTRUPrivateKeyParameters(nTRUParameters, byArray4));
    }
}

