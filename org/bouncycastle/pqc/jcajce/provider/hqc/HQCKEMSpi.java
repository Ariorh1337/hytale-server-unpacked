/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.pqc.jcajce.provider.hqc.HQCDecapsulatorSpi
 *  org.bouncycastle.pqc.jcajce.provider.hqc.HQCEncapsulatorSpi
 *  org.bouncycastle.pqc.jcajce.provider.hqc.HQCKEMSpi
 */
package org.bouncycastle.pqc.jcajce.provider.hqc;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KEMSpi;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPublicKey;
import org.bouncycastle.pqc.jcajce.provider.hqc.HQCDecapsulatorSpi;
import org.bouncycastle.pqc.jcajce.provider.hqc.HQCEncapsulatorSpi;

public class HQCKEMSpi
implements KEMSpi {
    @Override
    public KEMSpi.EncapsulatorSpi engineNewEncapsulator(PublicKey publicKey, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (!(publicKey instanceof BCHQCPublicKey)) {
            throw new InvalidKeyException("unsupported key");
        }
        if (algorithmParameterSpec == null) {
            algorithmParameterSpec = new KTSParameterSpec.Builder("Generic", 256).withNoKdf().build();
        }
        if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("HQC can only accept KTSParameterSpec");
        }
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        return new HQCEncapsulatorSpi((BCHQCPublicKey)publicKey, (KTSParameterSpec)algorithmParameterSpec, secureRandom);
    }

    @Override
    public KEMSpi.DecapsulatorSpi engineNewDecapsulator(PrivateKey privateKey, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (!(privateKey instanceof BCHQCPrivateKey)) {
            throw new InvalidKeyException("unsupported key");
        }
        if (algorithmParameterSpec == null) {
            algorithmParameterSpec = new KTSParameterSpec.Builder("Generic", 256).withNoKdf().build();
        }
        if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("HQC can only accept KTSParameterSpec");
        }
        return new HQCDecapsulatorSpi((BCHQCPrivateKey)privateKey, (KTSParameterSpec)algorithmParameterSpec);
    }
}

