/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.ntru;

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
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMExtractor;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMGenerator;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPublicKey;
import org.bouncycastle.util.Arrays;

public class NTRUKeyGeneratorSpi
extends KeyGeneratorSpi {
    private KEMGenerateSpec genSpec;
    private SecureRandom random;
    private KEMExtractSpec extSpec;

    @Override
    protected void engineInit(SecureRandom secureRandom) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        this.random = secureRandom;
        if (algorithmParameterSpec instanceof KEMGenerateSpec) {
            this.genSpec = (KEMGenerateSpec)algorithmParameterSpec;
            this.extSpec = null;
        } else if (algorithmParameterSpec instanceof KEMExtractSpec) {
            this.genSpec = null;
            this.extSpec = (KEMExtractSpec)algorithmParameterSpec;
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
            BCNTRUPublicKey bCNTRUPublicKey = (BCNTRUPublicKey)this.genSpec.getPublicKey();
            NTRUKEMGenerator nTRUKEMGenerator = new NTRUKEMGenerator(this.random);
            SecretWithEncapsulation secretWithEncapsulation = nTRUKEMGenerator.generateEncapsulated(bCNTRUPublicKey.getKeyParams());
            byte[] byArray = secretWithEncapsulation.getSecret();
            byte[] byArray2 = Arrays.copyOfRange(byArray, 0, (this.genSpec.getKeySize() + 7) / 8);
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
        BCNTRUPrivateKey bCNTRUPrivateKey = (BCNTRUPrivateKey)this.extSpec.getPrivateKey();
        NTRUKEMExtractor nTRUKEMExtractor = new NTRUKEMExtractor(bCNTRUPrivateKey.getKeyParams());
        byte[] byArray = this.extSpec.getEncapsulation();
        byte[] byArray3 = nTRUKEMExtractor.extractSecret(byArray);
        byte[] byArray4 = Arrays.copyOfRange(byArray3, 0, (this.extSpec.getKeySize() + 7) / 8);
        Arrays.clear(byArray3);
        SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(byArray4, this.extSpec.getKeyAlgorithmName()), byArray);
        Arrays.clear(byArray4);
        return secretKeyWithEncapsulation;
    }
}

