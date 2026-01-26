/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertResponse;

public class CertRepMessage
extends ASN1Object {
    private final ASN1Sequence caPubs;
    private final ASN1Sequence response;

    private CertRepMessage(ASN1Sequence aSN1Sequence) {
        int n = 0;
        this.caPubs = aSN1Sequence.size() > 1 ? ASN1Sequence.getInstance((ASN1TaggedObject)aSN1Sequence.getObjectAt(n++), true) : null;
        this.response = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(n));
    }

    public CertRepMessage(CMPCertificate[] cMPCertificateArray, CertResponse[] certResponseArray) {
        if (certResponseArray == null) {
            throw new IllegalArgumentException("'response' cannot be null");
        }
        this.caPubs = cMPCertificateArray != null && cMPCertificateArray.length != 0 ? new DERSequence(cMPCertificateArray) : null;
        this.response = new DERSequence(certResponseArray);
    }

    public static CertRepMessage getInstance(Object object) {
        if (object instanceof CertRepMessage) {
            return (CertRepMessage)object;
        }
        if (object != null) {
            return new CertRepMessage(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public CMPCertificate[] getCaPubs() {
        if (this.caPubs == null) {
            return null;
        }
        CMPCertificate[] cMPCertificateArray = new CMPCertificate[this.caPubs.size()];
        for (int i = 0; i != cMPCertificateArray.length; ++i) {
            cMPCertificateArray[i] = CMPCertificate.getInstance(this.caPubs.getObjectAt(i));
        }
        return cMPCertificateArray;
    }

    public CertResponse[] getResponse() {
        CertResponse[] certResponseArray = new CertResponse[this.response.size()];
        for (int i = 0; i != certResponseArray.length; ++i) {
            certResponseArray[i] = CertResponse.getInstance(this.response.getObjectAt(i));
        }
        return certResponseArray;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        if (this.caPubs != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 1, (ASN1Encodable)this.caPubs));
        }
        aSN1EncodableVector.add(this.response);
        return new DERSequence(aSN1EncodableVector);
    }
}

