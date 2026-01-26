/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.pqc.jcajce.provider.ntru.NTRUDecapsulatorSpi
 *  org.bouncycastle.pqc.jcajce.provider.ntru.NTRUEncapsulatorSpi
 *  org.bouncycastle.pqc.jcajce.provider.ntru.NTRUKEMSpi
 */
package org.bouncycastle.pqc.jcajce.provider.ntru;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KEMSpi;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPublicKey;
import org.bouncycastle.pqc.jcajce.provider.ntru.NTRUDecapsulatorSpi;
import org.bouncycastle.pqc.jcajce.provider.ntru.NTRUEncapsulatorSpi;

public class NTRUKEMSpi
implements KEMSpi {
    @Override
    public KEMSpi.EncapsulatorSpi engineNewEncapsulator(PublicKey publicKey, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (!(publicKey instanceof BCNTRUPublicKey)) {
            throw new InvalidKeyException("unsupported key");
        }
        if (algorithmParameterSpec == null) {
            algorithmParameterSpec = new KTSParameterSpec.Builder("Generic", 256).withNoKdf().build();
        }
        if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("NTRU can only accept KTSParameterSpec");
        }
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        return new NTRUEncapsulatorSpi((BCNTRUPublicKey)publicKey, (KTSParameterSpec)algorithmParameterSpec, secureRandom);
    }

    @Override
    public KEMSpi.DecapsulatorSpi engineNewDecapsulator(PrivateKey privateKey, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (!(privateKey instanceof BCNTRUPrivateKey)) {
            throw new InvalidKeyException("unsupported key");
        }
        if (algorithmParameterSpec == null) {
            algorithmParameterSpec = new KTSParameterSpec.Builder("Generic", 256).withNoKdf().build();
        }
        if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("NTRU can only accept KTSParameterSpec");
        }
        return new NTRUDecapsulatorSpi((BCNTRUPrivateKey)privateKey, (KTSParameterSpec)algorithmParameterSpec);
    }
}

