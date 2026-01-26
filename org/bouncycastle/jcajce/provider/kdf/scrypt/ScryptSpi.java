/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.crypto.KDFParameters
 *  javax.crypto.KDFSpi
 *  org.bouncycastle.jcajce.provider.kdf.scrypt.ScryptSpi
 *  org.bouncycastle.jcajce.spec.ScryptParameterSpec
 */
package org.bouncycastle.jcajce.provider.kdf.scrypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KDFParameters;
import javax.crypto.KDFSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.jcajce.spec.ScryptParameterSpec;
import org.bouncycastle.util.Arrays;

/*
 * Exception performing whole class analysis ignored.
 */
class ScryptSpi
extends KDFSpi {
    protected ScryptSpi(KDFParameters kDFParameters) throws InvalidAlgorithmParameterException {
        super(ScryptSpi.requireNull((KDFParameters)kDFParameters, (String)"Scrypt does not support parameters"));
    }

    protected KDFParameters engineGetParameters() {
        return null;
    }

    protected SecretKey engineDeriveKey(String string, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        byte[] byArray = this.engineDeriveData(algorithmParameterSpec);
        return new SecretKeySpec(byArray, string);
    }

    protected byte[] engineDeriveData(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof ScryptParameterSpec)) {
            throw new InvalidAlgorithmParameterException("SCrypt requires an SCryptParameterSpec as derivation parameters");
        }
        ScryptKeySpec scryptKeySpec = (ScryptKeySpec)((Object)algorithmParameterSpec);
        char[] cArray = scryptKeySpec.getPassword();
        byte[] byArray = scryptKeySpec.getSalt();
        int n = scryptKeySpec.getCostParameter();
        int n2 = scryptKeySpec.getBlockSize();
        int n3 = scryptKeySpec.getParallelizationParameter();
        int n4 = scryptKeySpec.getKeyLength();
        if (byArray == null) {
            throw new InvalidAlgorithmParameterException("Salt S must be provided.");
        }
        if (n <= 1) {
            throw new InvalidAlgorithmParameterException("Cost parameter N must be > 1.");
        }
        if (n4 <= 0) {
            throw new InvalidAlgorithmParameterException("positive key length required: " + n4);
        }
        byte[] byArray2 = SCrypt.generate(PasswordConverter.UTF8.convert(cArray), byArray, n, n2, n3, n4 / 8);
        Arrays.clear(cArray);
        return byArray2;
    }

    private static KDFParameters requireNull(KDFParameters kDFParameters, String string) throws InvalidAlgorithmParameterException {
        if (kDFParameters != null) {
            throw new InvalidAlgorithmParameterException(string);
        }
        return null;
    }
}

