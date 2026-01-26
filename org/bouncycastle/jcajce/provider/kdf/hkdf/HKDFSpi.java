/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.crypto.KDFParameters
 *  javax.crypto.KDFSpi
 *  javax.crypto.spec.HKDFParameterSpec
 *  javax.crypto.spec.HKDFParameterSpec$Extract
 *  javax.crypto.spec.HKDFParameterSpec$ExtractThenExpand
 *  org.bouncycastle.jcajce.provider.kdf.hkdf.HKDFSpi
 */
package org.bouncycastle.jcajce.provider.kdf.hkdf;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import javax.crypto.KDFParameters;
import javax.crypto.KDFSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.HKDFParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.jcajce.spec.HKDFParameterSpec;

/*
 * Exception performing whole class analysis ignored.
 */
class HKDFSpi
extends KDFSpi {
    protected HKDFBytesGenerator hkdf;

    public HKDFSpi(KDFParameters kDFParameters, Digest digest) throws InvalidAlgorithmParameterException {
        super(HKDFSpi.requireNull((KDFParameters)kDFParameters, (String)"HKDF does not support parameters"));
        this.hkdf = new HKDFBytesGenerator(digest);
    }

    protected KDFParameters engineGetParameters() {
        return null;
    }

    protected SecretKey engineDeriveKey(String string, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        byte[] byArray = this.engineDeriveData(algorithmParameterSpec);
        return new SecretKeySpec(byArray, string);
    }

    protected byte[] engineDeriveData(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof HKDFParameterSpec) && !(algorithmParameterSpec instanceof javax.crypto.spec.HKDFParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Invalid AlgorithmParameterSpec provided");
        }
        HKDFParameters hKDFParameters = null;
        int n = 0;
        if (algorithmParameterSpec instanceof HKDFParameterSpec.ExtractThenExpand) {
            HKDFParameterSpec.ExtractThenExpand extractThenExpand = (HKDFParameterSpec.ExtractThenExpand)algorithmParameterSpec;
            List list = extractThenExpand.ikms();
            List list2 = extractThenExpand.salts();
            hKDFParameters = new HKDFParameters(((SecretKey)list.get(0)).getEncoded(), ((SecretKey)list2.get(0)).getEncoded(), extractThenExpand.info());
            n = extractThenExpand.length();
            this.hkdf.init(hKDFParameters);
            byte[] byArray = new byte[n];
            this.hkdf.generateBytes(byArray, 0, n);
            return byArray;
        }
        if (algorithmParameterSpec instanceof HKDFParameterSpec.Extract) {
            HKDFParameterSpec.Extract extract = (HKDFParameterSpec.Extract)algorithmParameterSpec;
            List list = extract.ikms();
            List list3 = extract.salts();
            return this.hkdf.extractPRK(((SecretKey)list3.get(0)).getEncoded(), ((SecretKey)list.get(0)).getEncoded());
        }
        if (algorithmParameterSpec instanceof HKDFParameterSpec) {
            HKDFParameterSpec hKDFParameterSpec = (HKDFParameterSpec)algorithmParameterSpec;
            hKDFParameters = new HKDFParameters(hKDFParameterSpec.getIKM(), hKDFParameterSpec.getSalt(), hKDFParameterSpec.getInfo());
            n = hKDFParameterSpec.getOutputLength();
            this.hkdf.init(hKDFParameters);
            byte[] byArray = new byte[n];
            this.hkdf.generateBytes(byArray, 0, n);
            return byArray;
        }
        throw new InvalidAlgorithmParameterException("invalid HKDFParameterSpec provided");
    }

    private static KDFParameters requireNull(KDFParameters kDFParameters, String string) throws InvalidAlgorithmParameterException {
        if (kDFParameters != null) {
            throw new InvalidAlgorithmParameterException(string);
        }
        return null;
    }
}

