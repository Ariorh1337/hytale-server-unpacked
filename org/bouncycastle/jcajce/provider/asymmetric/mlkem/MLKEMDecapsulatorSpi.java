/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMDecapsulatorSpi
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.util.Arrays;
import java.util.Objects;
import javax.crypto.DecapsulateException;
import javax.crypto.KEMSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPrivateKey;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;

public class MLKEMDecapsulatorSpi
implements KEMSpi.DecapsulatorSpi {
    BCMLKEMPrivateKey privateKey;
    KTSParameterSpec parameterSpec;
    MLKEMExtractor kemExt;

    public MLKEMDecapsulatorSpi(BCMLKEMPrivateKey bCMLKEMPrivateKey, KTSParameterSpec kTSParameterSpec) {
        this.privateKey = bCMLKEMPrivateKey;
        this.parameterSpec = kTSParameterSpec;
        this.kemExt = new MLKEMExtractor(bCMLKEMPrivateKey.getKeyParams());
    }

    @Override
    public SecretKey engineDecapsulate(byte[] byArray, int n, int n2, String string) throws DecapsulateException {
        Objects.checkFromToIndex(n, n2, this.engineSecretSize());
        Objects.requireNonNull(string, "null algorithm");
        Objects.requireNonNull(byArray, "null encapsulation");
        if (byArray.length != this.engineEncapsulationSize()) {
            throw new DecapsulateException("incorrect encapsulation size");
        }
        if (!this.parameterSpec.getKeyAlgorithmName().equals("Generic") && string.equals("Generic")) {
            string = this.parameterSpec.getKeyAlgorithmName();
        }
        if (!this.parameterSpec.getKeyAlgorithmName().equals("Generic") && !this.parameterSpec.getKeyAlgorithmName().equals(string)) {
            throw new UnsupportedOperationException(this.parameterSpec.getKeyAlgorithmName() + " does not match " + string);
        }
        boolean bl = this.parameterSpec.getKdfAlgorithm() != null;
        byte[] byArray2 = this.kemExt.extractSecret(byArray);
        byte[] byArray3 = Arrays.copyOfRange(KdfUtil.makeKeyBytes(this.parameterSpec, byArray2), n, n2);
        return new SecretKeySpec(byArray3, string);
    }

    @Override
    public int engineSecretSize() {
        return this.parameterSpec.getKeySize() / 8;
    }

    @Override
    public int engineEncapsulationSize() {
        return this.kemExt.getEncapsulationLength();
    }
}

