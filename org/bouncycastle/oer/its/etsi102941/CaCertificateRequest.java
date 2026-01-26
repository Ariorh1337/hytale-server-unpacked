/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateSubjectAttributes;
import org.bouncycastle.oer.its.etsi102941.basetypes.PublicKeys;

public class CaCertificateRequest
extends ASN1Object {
    private final PublicKeys publicKeys;
    private final CertificateSubjectAttributes requestedSubjectAttributes;

    public CaCertificateRequest(PublicKeys publicKeys, CertificateSubjectAttributes certificateSubjectAttributes) {
        this.publicKeys = publicKeys;
        this.requestedSubjectAttributes = certificateSubjectAttributes;
    }

    private CaCertificateRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.publicKeys = PublicKeys.getInstance(aSN1Sequence.getObjectAt(0));
        this.requestedSubjectAttributes = CertificateSubjectAttributes.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static CaCertificateRequest getInstance(Object object) {
        if (object instanceof CaCertificateRequest) {
            return (CaCertificateRequest)object;
        }
        if (object != null) {
            return new CaCertificateRequest(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public PublicKeys getPublicKeys() {
        return this.publicKeys;
    }

    public CertificateSubjectAttributes getRequestedSubjectAttributes() {
        return this.requestedSubjectAttributes;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.publicKeys, this.requestedSubjectAttributes});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PublicKeys publicKeys;
        private CertificateSubjectAttributes requestedSubjectAttributes;

        public Builder setPublicKeys(PublicKeys publicKeys) {
            this.publicKeys = publicKeys;
            return this;
        }

        public Builder setRequestedSubjectAttributes(CertificateSubjectAttributes certificateSubjectAttributes) {
            this.requestedSubjectAttributes = certificateSubjectAttributes;
            return this;
        }

        public CaCertificateRequest createCaCertificateRequest() {
            return new CaCertificateRequest(this.publicKeys, this.requestedSubjectAttributes);
        }
    }
}

