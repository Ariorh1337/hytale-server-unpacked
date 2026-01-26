/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMDecapsulatorSpi
 *  org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMEncapsulatorSpi
 *  org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMSpi
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KEMSpi;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMDecapsulatorSpi;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMEncapsulatorSpi;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;

public class MLKEMSpi
implements KEMSpi {
    @Override
    public KEMSpi.EncapsulatorSpi engineNewEncapsulator(PublicKey publicKey, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (!(publicKey instanceof BCMLKEMPublicKey)) {
            throw new InvalidKeyException("unsupported key");
        }
        if (algorithmParameterSpec == null) {
            algorithmParameterSpec = new KTSParameterSpec.Builder("Generic", 256).withNoKdf().build();
        }
        if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("MLKEM can only accept KTSParameterSpec");
        }
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        return new MLKEMEncapsulatorSpi((BCMLKEMPublicKey)publicKey, (KTSParameterSpec)algorithmParameterSpec, secureRandom);
    }

    @Override
    public KEMSpi.DecapsulatorSpi engineNewDecapsulator(PrivateKey privateKey, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (!(privateKey instanceof BCMLKEMPrivateKey)) {
            throw new InvalidKeyException("unsupported key");
        }
        if (algorithmParameterSpec == null) {
            algorithmParameterSpec = new KTSParameterSpec.Builder("Generic", 256).withNoKdf().build();
        }
        if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("MLKEM can only accept KTSParameterSpec");
        }
        return new MLKEMDecapsulatorSpi((BCMLKEMPrivateKey)privateKey, (KTSParameterSpec)algorithmParameterSpec);
    }
}

