/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.pkcs.DeltaCertificateRequestAttributeValue;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;

public class DeltaCertAttributeUtils {
    public static boolean isDeltaRequestSignatureValid(PKCS10CertificationRequest pKCS10CertificationRequest, ContentVerifierProvider contentVerifierProvider) throws PKCSException {
        Object object;
        Attribute[] attributeArray = pKCS10CertificationRequest.getAttributes(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.2"));
        DeltaCertificateRequestAttributeValue deltaCertificateRequestAttributeValue = new DeltaCertificateRequestAttributeValue(attributeArray[0]);
        attributeArray = pKCS10CertificationRequest.getAttributes(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.3"));
        CertificationRequest certificationRequest = pKCS10CertificationRequest.toASN1Structure();
        CertificationRequestInfo certificationRequestInfo = certificationRequest.getCertificationRequestInfo();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(certificationRequestInfo.getVersion());
        aSN1EncodableVector.add(certificationRequestInfo.getSubject());
        aSN1EncodableVector.add(certificationRequestInfo.getSubjectPublicKeyInfo());
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        Object object2 = certificationRequestInfo.getAttributes().getObjects();
        while (object2.hasMoreElements()) {
            object = Attribute.getInstance(object2.nextElement());
            if (((Attribute)object).getAttrType().equals(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.3"))) continue;
            aSN1EncodableVector2.add((ASN1Encodable)object);
        }
        aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable)new DERSet(aSN1EncodableVector2)));
        object2 = new ASN1EncodableVector();
        ((ASN1EncodableVector)object2).add(new DERSequence(aSN1EncodableVector));
        ((ASN1EncodableVector)object2).add(deltaCertificateRequestAttributeValue.getSignatureAlgorithm());
        ((ASN1EncodableVector)object2).add(attributeArray[0].getAttributeValues()[0]);
        object = new PKCS10CertificationRequest(CertificationRequest.getInstance(new DERSequence((ASN1EncodableVector)object2)));
        return ((PKCS10CertificationRequest)object).isSignatureValid(contentVerifierProvider);
    }
}

