/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.pqc.jcajce.provider.hqc.HQCEncapsulatorSpi
 */
package org.bouncycastle.pqc.jcajce.provider.hqc;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.KEM;
import javax.crypto.KEMSpi;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.crypto.hqc.HQCKEMGenerator;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;

public class HQCEncapsulatorSpi
implements KEMSpi.EncapsulatorSpi {
    private final BCHQCPublicKey publicKey;
    private final KTSParameterSpec parameterSpec;
    private final HQCKEMGenerator kemGen;

    public HQCEncapsulatorSpi(BCHQCPublicKey bCHQCPublicKey, KTSParameterSpec kTSParameterSpec, SecureRandom secureRandom) {
        this.publicKey = bCHQCPublicKey;
        this.parameterSpec = kTSParameterSpec;
        this.kemGen = new HQCKEMGenerator(secureRandom);
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
            case "HQC-128": {
                return 128;
            }
            case "HQC-192": {
                return 192;
            }
            case "HQC-256": {
                return 256;
            }
        }
        return -1;
    }
}

