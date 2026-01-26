/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeDecapsulatorSpi
 *  org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeEncapsulatorSpi
 *  org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKEMSpi
 */
package org.bouncycastle.pqc.jcajce.provider.ntruprime;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KEMSpi;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.BCSNTRUPrimePrivateKey;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.BCSNTRUPrimePublicKey;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeDecapsulatorSpi;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeEncapsulatorSpi;

public class SNTRUPrimeKEMSpi
implements KEMSpi {
    @Override
    public KEMSpi.EncapsulatorSpi engineNewEncapsulator(PublicKey publicKey, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (!(publicKey instanceof BCSNTRUPrimePublicKey)) {
            throw new InvalidKeyException("unsupported key");
        }
        if (algorithmParameterSpec == null) {
            algorithmParameterSpec = new KTSParameterSpec.Builder("Generic", 256).withNoKdf().build();
        }
        if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("SNTRUPrime can only accept KTSParameterSpec");
        }
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        return new SNTRUPrimeEncapsulatorSpi((BCSNTRUPrimePublicKey)publicKey, (KTSParameterSpec)algorithmParameterSpec, secureRandom);
    }

    @Override
    public KEMSpi.DecapsulatorSpi engineNewDecapsulator(PrivateKey privateKey, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (!(privateKey instanceof BCSNTRUPrimePrivateKey)) {
            throw new InvalidKeyException("unsupported key");
        }
        if (algorithmParameterSpec == null) {
            algorithmParameterSpec = new KTSParameterSpec.Builder("Generic", 256).withNoKdf().build();
        }
        if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("SNTRUPrime can only accept KTSParameterSpec");
        }
        return new SNTRUPrimeDecapsulatorSpi((BCSNTRUPrimePrivateKey)privateKey, (KTSParameterSpec)algorithmParameterSpec);
    }
}

