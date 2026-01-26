/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeEncapsulatorSpi
 */
package org.bouncycastle.pqc.jcajce.provider.ntruprime;

import java.security.SecureRandom;
import java.util.Objects;
import javax.crypto.KEM;
import javax.crypto.KEMSpi;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKEMGenerator;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.BCSNTRUPrimePublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;
import org.bouncycastle.util.Arrays;

class SNTRUPrimeEncapsulatorSpi
implements KEMSpi.EncapsulatorSpi {
    private final BCSNTRUPrimePublicKey publicKey;
    private final KTSParameterSpec parameterSpec;
    private final SNTRUPrimeKEMGenerator kemGen;

    public SNTRUPrimeEncapsulatorSpi(BCSNTRUPrimePublicKey bCSNTRUPrimePublicKey, KTSParameterSpec kTSParameterSpec, SecureRandom secureRandom) {
        this.publicKey = bCSNTRUPrimePublicKey;
        this.parameterSpec = kTSParameterSpec;
        this.kemGen = new SNTRUPrimeKEMGenerator(secureRandom);
    }

    @Override
    public KEM.Encapsulated engineEncapsulate(int n, int n2, String string) {
        Objects.checkFromToIndex(n, n2, this.engineSecretSize());
        Objects.requireNonNull(string, "null algorithm");
        if (!this.parameterSpec.getKeyAlgorithmName().equals("Generic") && string.equals("Generic")) {
            string = this.parameterSpec.getKeyAlgorithmName();
        }
        if (!this.parameterSpec.getKeyAlgorithmName().equals("Generic") && !this.parameterSpec.getKeyAlgorithmName().equals(string)) {
            throw new UnsupportedOperationException(this.parameterSpec.getKeyAlgorithmName() + " does not match " + string);
        }
        SecretWithEncapsulation secretWithEncapsulation = this.kemGen.generateEncapsulated(this.publicKey.getKeyParams());
        byte[] byArray = secretWithEncapsulation.getEncapsulation();
        byte[] byArray2 = secretWithEncapsulation.getSecret();
        byte[] byArray3 = Arrays.copyOfRange(KdfUtil.makeKeyBytes(this.parameterSpec, byArray2), n, n2);
        return new KEM.Encapsulated(new SecretKeySpec(byArray3, string), byArray, null);
    }

    @Override
    public int engineSecretSize() {
        return this.parameterSpec.getKeySize() / 8;
    }

    @Override
    public int engineEncapsulationSize() {
        return this.publicKey.getKeyParams().getParameters().getRoundedPolynomialBytes() + 32;
    }
}

