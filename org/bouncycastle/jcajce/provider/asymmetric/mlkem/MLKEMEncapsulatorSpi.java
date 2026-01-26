/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMEncapsulatorSpi
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.KEM;
import javax.crypto.KEMSpi;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPublicKey;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;

public class MLKEMEncapsulatorSpi
implements KEMSpi.EncapsulatorSpi {
    private final BCMLKEMPublicKey publicKey;
    private final KTSParameterSpec parameterSpec;
    private final MLKEMGenerator kemGen;

    public MLKEMEncapsulatorSpi(BCMLKEMPublicKey bCMLKEMPublicKey, KTSParameterSpec kTSParameterSpec, SecureRandom secureRandom) {
        this.publicKey = bCMLKEMPublicKey;
        this.parameterSpec = kTSParameterSpec;
        this.kemGen = new MLKEMGenerator(secureRandom);
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
        boolean bl = this.parameterSpec.getKdfAlgorithm() != null;
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
        switch (this.publicKey.getKeyParams().getParameters().getName()) {
            case "ML-KEM-512": {
                return 768;
            }
            case "ML-KEM-768": {
                return 1088;
            }
            case "ML-KEM-1024": {
                return 1568;
            }
        }
        return -1;
    }
}

