/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs.bc;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.io.MacOutputStream;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

class PKCS12PBEUtils {
    private static Map keySizes = new HashMap();
    private static Set noIvAlgs = new HashSet();
    private static Set desAlgs = new HashSet();

    PKCS12PBEUtils() {
    }

    static int getKeySize(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (Integer)keySizes.get(aSN1ObjectIdentifier);
    }

    static boolean hasNoIv(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return noIvAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isDesAlg(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return desAlgs.contains(aSN1ObjectIdentifier);
    }

    static PaddedBufferedBlockCipher getEngine(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        BlockCipher blockCipher;
        if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC)) {
            blockCipher = new DESedeEngine();
        } else if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC)) {
            blockCipher = new RC2Engine();
        } else {
            throw new IllegalStateException("unknown algorithm");
        }
        return new PaddedBufferedBlockCipher(new CBCBlockCipher(blockCipher), new PKCS7Padding());
    }

    static MacCalculator createMacCalculator(final ASN1ObjectIdentifier aSN1ObjectIdentifier, ExtendedDigest extendedDigest, final PKCS12PBEParams pKCS12PBEParams, final char[] cArray) {
        PKCS12ParametersGenerator pKCS12ParametersGenerator = new PKCS12ParametersGenerator(extendedDigest);
        pKCS12ParametersGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(cArray), pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue());
        KeyParameter keyParameter = (KeyParameter)pKCS12ParametersGenerator.generateDerivedMacParameters(extendedDigest.getDigestSize() * 8);
        final HMac hMac = new HMac(extendedDigest);
        hMac.init(keyParameter);
        return new MacCalculator(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(aSN1ObjectIdentifier, pKCS12PBEParams);
            }

            @Override
            public OutputStream getOutputStream() {
                return new MacOutputStream(hMac);
            }

            @Override
            public byte[] getMac() {
                byte[] byArray = new byte[hMac.getMacSize()];
                hMac.doFinal(byArray, 0);
                return byArray;
            }

            @Override
            public GenericKey getKey() {
                return new GenericKey(this.getAlgorithmIdentifier(), PKCS12ParametersGenerator.PKCS12PasswordToBytes(cArray));
            }
        };
    }

    static MacCalculator createPBMac1Calculator(final PBMAC1Params pBMAC1Params, PBKDF2Params pBKDF2Params, final char[] cArray) {
        final HMac hMac = new HMac(PKCS12PBEUtils.getPrf(pBMAC1Params.getMessageAuthScheme().getAlgorithm()));
        PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(PKCS12PBEUtils.getPrf(pBKDF2Params.getPrf().getAlgorithm()));
        pKCS5S2ParametersGenerator.init(Strings.toUTF8ByteArray(cArray), pBKDF2Params.getSalt(), BigIntegers.intValueExact(pBKDF2Params.getIterationCount()));
        CipherParameters cipherParameters = ((PBEParametersGenerator)pKCS5S2ParametersGenerator).generateDerivedParameters(BigIntegers.intValueExact(pBKDF2Params.getKeyLength()) * 8);
        hMac.init(cipherParameters);
        return new MacCalculator(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBMAC1, pBMAC1Params);
            }

            @Override
            public OutputStream getOutputStream() {
                return new MacOutputStream(hMac);
            }

            @Override
            public byte[] getMac() {
                byte[] byArray = new byte[hMac.getMacSize()];
                hMac.doFinal(byArray, 0);
                return byArray;
            }

            @Override
            public GenericKey getKey() {
                return new GenericKey(this.getAlgorithmIdentifier(), Strings.toUTF8ByteArray(cArray));
            }
        };
    }

    private static Digest getPrf(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (PKCSObjectIdentifiers.id_hmacWithSHA256.equals(aSN1ObjectIdentifier)) {
            return new SHA256Digest();
        }
        if (PKCSObjectIdentifiers.id_hmacWithSHA512.equals(aSN1ObjectIdentifier)) {
            return new SHA512Digest();
        }
        throw new IllegalArgumentException("unknown prf id " + aSN1ObjectIdentifier);
    }

    static CipherParameters createCipherParameters(ASN1ObjectIdentifier aSN1ObjectIdentifier, ExtendedDigest extendedDigest, int n, PKCS12PBEParams pKCS12PBEParams, char[] cArray) {
        CipherParameters cipherParameters;
        PKCS12ParametersGenerator pKCS12ParametersGenerator = new PKCS12ParametersGenerator(extendedDigest);
        pKCS12ParametersGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(cArray), pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue());
        if (PKCS12PBEUtils.hasNoIv(aSN1ObjectIdentifier)) {
            cipherParameters = pKCS12ParametersGenerator.generateDerivedParameters(PKCS12PBEUtils.getKeySize(aSN1ObjectIdentifier));
        } else {
            cipherParameters = pKCS12ParametersGenerator.generateDerivedParameters(PKCS12PBEUtils.getKeySize(aSN1ObjectIdentifier), n * 8);
            if (PKCS12PBEUtils.isDesAlg(aSN1ObjectIdentifier)) {
                DESedeParameters.setOddParity(((KeyParameter)((ParametersWithIV)cipherParameters).getParameters()).getKey());
            }
        }
        return cipherParameters;
    }

    static {
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, Integers.valueOf(128));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
        noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4);
        noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4);
        desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC);
        desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC);
    }
}

