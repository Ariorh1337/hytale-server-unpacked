/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.hqc;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.pqc.crypto.hqc.HQCKEMExtractor;
import org.bouncycastle.pqc.crypto.hqc.HQCKEMGenerator;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPublicKey;
import org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;
import org.bouncycastle.util.Arrays;

public class HQCKeyGeneratorSpi
extends KeyGeneratorSpi {
    private KEMGenerateSpec genSpec;
    private SecureRandom random;
    private KEMExtractSpec extSpec;
    private HQCParameters hqcParameters;

    public HQCKeyGeneratorSpi() {
        this(null);
    }

    public HQCKeyGeneratorSpi(HQCParameters hQCParameters) {
        this.hqcParameters = hQCParameters;
    }

    @Override
    protected void engineInit(SecureRandom secureRandom) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        this.random = secureRandom;
        if (algorithmParameterSpec instanceof KEMGenerateSpec) {
            String string;
            this.genSpec = (KEMGenerateSpec)algorithmParameterSpec;
            this.extSpec = null;
            if (this.hqcParameters != null && !(string = HQCParameterSpec.fromName(this.hqcParameters.getName()).getName()).equals(this.genSpec.getPublicKey().getAlgorithm())) {
                throw new InvalidAlgorithmParameterException("key generator locked to " + string);
            }
        } else if (algorithmParameterSpec instanceof KEMExtractSpec) {
            String string;
            this.genSpec = null;
            this.extSpec = (KEMExtractSpec)algorithmParameterSpec;
            if (this.hqcParameters != null && !(string = HQCParameterSpec.fromName(this.hqcParameters.getName()).getName()).equals(this.extSpec.getPrivateKey().getAlgorithm())) {
                throw new InvalidAlgorithmParameterException("key generator locked to " + string);
            }
        } else {
            throw new InvalidAlgorithmParameterException("unknown spec");
        }
    }

    @Override
    protected void engineInit(int n, SecureRandom secureRandom) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    protected SecretKey engineGenerateKey() {
        if (this.genSpec != null) {
            BCHQCPublicKey bCHQCPublicKey = (BCHQCPublicKey)this.genSpec.getPublicKey();
            HQCKEMGenerator hQCKEMGenerator = new HQCKEMGenerator(this.random);
            SecretWithEncapsulation secretWithEncapsulation = hQCKEMGenerator.generateEncapsulated(bCHQCPublicKey.getKeyParams());
            SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(secretWithEncapsulation.getSecret(), this.genSpec.getKeyAlgorithmName()), secretWithEncapsulation.getEncapsulation());
            try {
                secretWithEncapsulation.destroy();
            }
            catch (DestroyFailedException destroyFailedException) {
                throw new IllegalStateException("key cleanup failed");
            }
            return secretKeyWithEncapsulation;
        }
        BCHQCPrivateKey bCHQCPrivateKey = (BCHQCPrivateKey)this.extSpec.getPrivateKey();
        HQCKEMExtractor hQCKEMExtractor = new HQCKEMExtractor(bCHQCPrivateKey.getKeyParams());
        byte[] byArray = this.extSpec.getEncapsulation();
        byte[] byArray2 = hQCKEMExtractor.extractSecret(byArray);
        SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(byArray2, this.extSpec.getKeyAlgorithmName()), byArray);
        Arrays.clear(byArray2);
        return secretKeyWithEncapsulation;
    }

    public static class HQC128
    extends HQCKeyGeneratorSpi {
        public HQC128() {
            super(HQCParameters.hqc128);
        }
    }

    public static class HQC192
    extends HQCKeyGeneratorSpi {
        public HQC192() {
            super(HQCParameters.hqc192);
        }
    }

    public static class HQC256
    extends HQCKeyGeneratorSpi {
        public HQC256() {
            super(HQCParameters.hqc256);
        }
    }
}

