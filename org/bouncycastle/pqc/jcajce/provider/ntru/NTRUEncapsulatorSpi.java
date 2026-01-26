/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.pqc.jcajce.provider.ntru.NTRUEncapsulatorSpi
 */
package org.bouncycastle.pqc.jcajce.provider.ntru;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.KEM;
import javax.crypto.KEMSpi;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMGenerator;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;

public class NTRUEncapsulatorSpi
implements KEMSpi.EncapsulatorSpi {
    private final BCNTRUPublicKey publicKey;
    private final KTSParameterSpec parameterSpec;
    private final NTRUKEMGenerator kemGen;

    public NTRUEncapsulatorSpi(BCNTRUPublicKey bCNTRUPublicKey, KTSParameterSpec kTSParameterSpec, SecureRandom secureRandom) {
        this.publicKey = bCNTRUPublicKey;
        this.parameterSpec = kTSParameterSpec;
        this.kemGen = new NTRUKEMGenerator(secureRandom);
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
            case "ntruhps2048509": {
                return 699;
            }
            case "ntruhps2048677": {
                return 930;
            }
            case "ntruhps4096821": {
                return 1230;
            }
            case "ntruhps40961229": {
                return 1843;
            }
            case "ntruhrss701": {
                return 1138;
            }
            case "ntruhrss1373": {
                return 2401;
            }
        }
        return -1;
    }
}

