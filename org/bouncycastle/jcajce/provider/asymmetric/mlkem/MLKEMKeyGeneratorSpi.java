/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPublicKey;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;
import org.bouncycastle.util.Arrays;

public class MLKEMKeyGeneratorSpi
extends KeyGeneratorSpi {
    private KEMGenerateSpec genSpec;
    private SecureRandom random;
    private KEMExtractSpec extSpec;
    private MLKEMParameters kyberParameters;

    public MLKEMKeyGeneratorSpi() {
        this(null);
    }

    protected MLKEMKeyGeneratorSpi(MLKEMParameters mLKEMParameters) {
        this.kyberParameters = mLKEMParameters;
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
            if (this.kyberParameters != null && !(string = MLKEMParameterSpec.fromName(this.kyberParameters.getName()).getName()).equals(this.genSpec.getPublicKey().getAlgorithm())) {
                throw new InvalidAlgorithmParameterException("key generator locked to " + string);
            }
        } else if (algorithmParameterSpec instanceof KEMExtractSpec) {
            String string;
            this.genSpec = null;
            this.extSpec = (KEMExtractSpec)algorithmParameterSpec;
            if (this.kyberParameters != null && !(string = MLKEMParameterSpec.fromName(this.kyberParameters.getName()).getName()).equals(this.extSpec.getPrivateKey().getAlgorithm())) {
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
            BCMLKEMPublicKey bCMLKEMPublicKey = (BCMLKEMPublicKey)this.genSpec.getPublicKey();
            MLKEMGenerator mLKEMGenerator = new MLKEMGenerator(this.random);
            SecretWithEncapsulation secretWithEncapsulation = mLKEMGenerator.generateEncapsulated(bCMLKEMPublicKey.getKeyParams());
            byte[] byArray = secretWithEncapsulation.getSecret();
            byte[] byArray2 = KdfUtil.makeKeyBytes(this.genSpec, byArray);
            Arrays.clear(byArray);
            SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(byArray2, this.genSpec.getKeyAlgorithmName()), secretWithEncapsulation.getEncapsulation());
            try {
                secretWithEncapsulation.destroy();
            }
            catch (DestroyFailedException destroyFailedException) {
                throw new IllegalStateException("key cleanup failed");
            }
            return secretKeyWithEncapsulation;
        }
        BCMLKEMPrivateKey bCMLKEMPrivateKey = (BCMLKEMPrivateKey)this.extSpec.getPrivateKey();
        MLKEMExtractor mLKEMExtractor = new MLKEMExtractor(bCMLKEMPrivateKey.getKeyParams());
        byte[] byArray = this.extSpec.getEncapsulation();
        byte[] byArray3 = mLKEMExtractor.extractSecret(byArray);
        byte[] byArray4 = KdfUtil.makeKeyBytes(this.extSpec, byArray3);
        Arrays.clear(byArray3);
        SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(byArray4, this.extSpec.getKeyAlgorithmName()), byArray);
        Arrays.clear(byArray4);
        return secretKeyWithEncapsulation;
    }

    public static class MLKEM1024
    extends MLKEMKeyGeneratorSpi {
        public MLKEM1024() {
            super(MLKEMParameters.ml_kem_1024);
        }
    }

    public static class MLKEM512
    extends MLKEMKeyGeneratorSpi {
        public MLKEM512() {
            super(MLKEMParameters.ml_kem_512);
        }
    }

    public static class MLKEM768
    extends MLKEMKeyGeneratorSpi {
        public MLKEM768() {
            super(MLKEMParameters.ml_kem_768);
        }
    }
}

