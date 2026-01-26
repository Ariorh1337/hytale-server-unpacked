/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.crypto.KDFParameters
 *  javax.crypto.KDFSpi
 *  org.bouncycastle.jcajce.provider.kdf.pbepbkdf2.PBEPBKDF2Spi
 */
package org.bouncycastle.jcajce.provider.kdf.pbepbkdf2;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KDFParameters;
import javax.crypto.KDFSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/*
 * Exception performing whole class analysis ignored.
 */
class PBEPBKDF2Spi
extends KDFSpi {
    final PasswordConverter pwdConverter;
    final PKCS5S2ParametersGenerator generator;

    protected PBEPBKDF2Spi(KDFParameters kDFParameters) throws InvalidAlgorithmParameterException {
        this(kDFParameters, (Digest)new SHA1Digest(), PasswordConverter.UTF8);
    }

    protected PBEPBKDF2Spi(KDFParameters kDFParameters, Digest digest) throws InvalidAlgorithmParameterException {
        this(kDFParameters, digest, PasswordConverter.UTF8);
    }

    protected PBEPBKDF2Spi(KDFParameters kDFParameters, Digest digest, PasswordConverter passwordConverter) throws InvalidAlgorithmParameterException {
        super(PBEPBKDF2Spi.requireNull((KDFParameters)kDFParameters, (String)"PBEPBKDF2 does not support parameters"));
        this.pwdConverter = passwordConverter;
        this.generator = new PKCS5S2ParametersGenerator(digest);
    }

    protected KDFParameters engineGetParameters() {
        return null;
    }

    protected SecretKey engineDeriveKey(String string, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        byte[] byArray = this.engineDeriveData(algorithmParameterSpec);
        return new SecretKeySpec(byArray, string);
    }

    protected byte[] engineDeriveData(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof PBEKeySpec)) {
            throw new InvalidAlgorithmParameterException("Invalid AlgorithmParameterSpec provided");
        }
        PBEKeySpec pBEKeySpec = (PBEKeySpec)((Object)algorithmParameterSpec);
        char[] cArray = pBEKeySpec.getPassword();
        byte[] byArray = pBEKeySpec.getSalt();
        int n = pBEKeySpec.getIterationCount();
        int n2 = pBEKeySpec.getKeyLength();
        if (cArray == null || byArray == null) {
            throw new InvalidAlgorithmParameterException("Password and salt cannot be null");
        }
        this.generator.init(this.pwdConverter.convert(cArray), byArray, n);
        KeyParameter keyParameter = (KeyParameter)this.generator.generateDerivedParameters(n2);
        byte[] byArray2 = keyParameter.getKey();
        Arrays.fill(cArray, '\u0000');
        return byArray2;
    }

    private static KDFParameters requireNull(KDFParameters kDFParameters, String string) throws InvalidAlgorithmParameterException {
        if (kDFParameters != null) {
            throw new InvalidAlgorithmParameterException(string);
        }
        return null;
    }
}

