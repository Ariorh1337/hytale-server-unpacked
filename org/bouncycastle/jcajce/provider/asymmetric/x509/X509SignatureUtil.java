/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Objects;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.encoders.Hex;

class X509SignatureUtil {
    private static final Map<ASN1ObjectIdentifier, String> algNames = new HashMap<ASN1ObjectIdentifier, String>();

    X509SignatureUtil() {
    }

    static boolean areEquivalentAlgorithms(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        if (!algorithmIdentifier.getAlgorithm().equals(algorithmIdentifier2.getAlgorithm())) {
            return false;
        }
        if (Properties.isOverrideSet("org.bouncycastle.x509.allow_absent_equiv_NULL") && X509SignatureUtil.isAbsentOrEmptyParameters(algorithmIdentifier.getParameters()) && X509SignatureUtil.isAbsentOrEmptyParameters(algorithmIdentifier2.getParameters())) {
            return true;
        }
        return Objects.areEqual(algorithmIdentifier.getParameters(), algorithmIdentifier2.getParameters());
    }

    static byte[] getExtensionValue(Extensions extensions, String string) {
        ASN1OctetString aSN1OctetString;
        ASN1ObjectIdentifier aSN1ObjectIdentifier;
        if (string != null && (aSN1ObjectIdentifier = ASN1ObjectIdentifier.tryFromID(string)) != null && null != (aSN1OctetString = Extensions.getExtensionValue(extensions, aSN1ObjectIdentifier))) {
            try {
                return aSN1OctetString.getEncoded();
            }
            catch (Exception exception) {
                throw Exceptions.illegalStateException("error parsing " + exception.getMessage(), exception);
            }
        }
        return null;
    }

    private static boolean isAbsentOrEmptyParameters(ASN1Encodable aSN1Encodable) {
        return aSN1Encodable == null || DERNull.INSTANCE.equals(aSN1Encodable);
    }

    static boolean isCompositeAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
        return MiscObjectIdentifiers.id_alg_composite.equals(algorithmIdentifier.getAlgorithm());
    }

    static void setSignatureParameters(Signature signature, ASN1Encodable aSN1Encodable) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (!X509SignatureUtil.isAbsentOrEmptyParameters(aSN1Encodable)) {
            String string = signature.getAlgorithm();
            AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(string, signature.getProvider());
            try {
                algorithmParameters.init(aSN1Encodable.toASN1Primitive().getEncoded());
            }
            catch (IOException iOException) {
                throw new SignatureException("IOException decoding parameters: " + iOException.getMessage());
            }
            if (string.endsWith("MGF1")) {
                try {
                    signature.setParameter(algorithmParameters.getParameterSpec(PSSParameterSpec.class));
                }
                catch (GeneralSecurityException generalSecurityException) {
                    throw new SignatureException("Exception extracting parameters: " + generalSecurityException.getMessage());
                }
            }
        }
    }

    static String getSignatureName(AlgorithmIdentifier algorithmIdentifier) {
        String string;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
        ASN1Encodable aSN1Encodable = algorithmIdentifier.getParameters();
        if (!X509SignatureUtil.isAbsentOrEmptyParameters(aSN1Encodable)) {
            if (PKCSObjectIdentifiers.id_RSASSA_PSS.equals(aSN1ObjectIdentifier)) {
                RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(aSN1Encodable);
                return X509SignatureUtil.getDigestAlgName(rSASSAPSSparams.getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
            }
            if (X9ObjectIdentifiers.ecdsa_with_SHA2.equals(aSN1ObjectIdentifier)) {
                AlgorithmIdentifier algorithmIdentifier2 = AlgorithmIdentifier.getInstance(aSN1Encodable);
                return X509SignatureUtil.getDigestAlgName(algorithmIdentifier2.getAlgorithm()) + "withECDSA";
            }
        }
        if ((string = algNames.get(aSN1ObjectIdentifier)) != null) {
            return string;
        }
        return X509SignatureUtil.findAlgName(aSN1ObjectIdentifier);
    }

    private static String getDigestAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = MessageDigestUtils.getDigestName(aSN1ObjectIdentifier);
        int n = string.indexOf(45);
        if (n > 0 && !string.startsWith("SHA3")) {
            return string.substring(0, n) + string.substring(n + 1);
        }
        return string;
    }

    private static String findAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        Object object;
        Provider provider = Security.getProvider("BC");
        if (provider != null && (object = X509SignatureUtil.lookupAlg(provider, aSN1ObjectIdentifier)) != null) {
            return object;
        }
        object = Security.getProviders();
        for (int i = 0; i != ((Provider[])object).length; ++i) {
            String string;
            if (provider == object[i] || (string = X509SignatureUtil.lookupAlg(object[i], aSN1ObjectIdentifier)) == null) continue;
            return string;
        }
        return aSN1ObjectIdentifier.getId();
    }

    private static String lookupAlg(Provider provider, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = provider.getProperty("Alg.Alias.Signature." + aSN1ObjectIdentifier);
        if (string != null) {
            return string;
        }
        string = provider.getProperty("Alg.Alias.Signature.OID." + aSN1ObjectIdentifier);
        if (string != null) {
            return string;
        }
        return null;
    }

    static void prettyPrintSignature(byte[] byArray, StringBuilder stringBuilder, String string) {
        if (byArray.length > 20) {
            stringBuilder.append("            Signature: ").append(Hex.toHexString(byArray, 0, 20)).append(string);
            for (int i = 20; i < byArray.length; i += 20) {
                if (i < byArray.length - 20) {
                    stringBuilder.append("                       ").append(Hex.toHexString(byArray, i, 20)).append(string);
                    continue;
                }
                stringBuilder.append("                       ").append(Hex.toHexString(byArray, i, byArray.length - i)).append(string);
            }
        } else {
            stringBuilder.append("            Signature: ").append(Hex.toHexString(byArray)).append(string);
        }
    }

    static {
        algNames.put(EdECObjectIdentifiers.id_Ed25519, "Ed25519");
        algNames.put(EdECObjectIdentifiers.id_Ed448, "Ed448");
        algNames.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1withDSA");
        algNames.put(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1withDSA");
    }
}

