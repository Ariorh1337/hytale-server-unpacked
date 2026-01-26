/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

public class KeyUtil {
    public static byte[] getEncodedSubjectPublicKeyInfo(AlgorithmIdentifier algorithmIdentifier, ASN1Encodable aSN1Encodable) {
        try {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(algorithmIdentifier, aSN1Encodable));
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static byte[] getEncodedSubjectPublicKeyInfo(AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        try {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(algorithmIdentifier, byArray));
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static byte[] getEncodedSubjectPublicKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        try {
            return subjectPublicKeyInfo.getEncoded("DER");
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static byte[] getEncodedSubjectPublicKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter) {
        if (asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("private key found");
        }
        try {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter));
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static byte[] getEncodedPrivateKeyInfo(AlgorithmIdentifier algorithmIdentifier, ASN1Encodable aSN1Encodable) {
        try {
            PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, aSN1Encodable.toASN1Primitive());
            return KeyUtil.getEncodedPrivateKeyInfo(privateKeyInfo);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static byte[] getEncodedPrivateKeyInfo(PrivateKeyInfo privateKeyInfo) {
        try {
            return privateKeyInfo.getEncoded("DER");
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static byte[] getEncodedPrivateKeyInfo(AsymmetricKeyParameter asymmetricKeyParameter, ASN1Set aSN1Set) {
        if (!asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("public key found");
        }
        try {
            return KeyUtil.getEncodedPrivateKeyInfo(PrivateKeyInfoFactory.createPrivateKeyInfo(asymmetricKeyParameter, aSN1Set));
        }
        catch (Exception exception) {
            return null;
        }
    }
}

