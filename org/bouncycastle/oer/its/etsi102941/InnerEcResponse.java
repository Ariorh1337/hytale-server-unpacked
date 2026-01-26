/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi102941.EnrolmentResponseCode;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;
import org.bouncycastle.util.Arrays;

public class InnerEcResponse
extends ASN1Object {
    private final ASN1OctetString requestHash;
    private final EnrolmentResponseCode responseCode;
    private final EtsiTs103097Certificate certificate;

    public InnerEcResponse(ASN1OctetString aSN1OctetString, EnrolmentResponseCode enrolmentResponseCode, EtsiTs103097Certificate etsiTs103097Certificate) {
        this.requestHash = aSN1OctetString;
        this.responseCode = enrolmentResponseCode;
        this.certificate = etsiTs103097Certificate;
    }

    private InnerEcResponse(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.requestHash = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        this.responseCode = EnrolmentResponseCode.getInstance(aSN1Sequence.getObjectAt(1));
        this.certificate = OEROptional.getValue(EtsiTs103097Certificate.class, aSN1Sequence.getObjectAt(2));
    }

    public static InnerEcResponse getInstance(Object object) {
        if (object instanceof InnerEcResponse) {
            return (InnerEcResponse)object;
        }
        if (object != null) {
            return new InnerEcResponse(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ASN1OctetString getRequestHash() {
        return this.requestHash;
    }

    public EnrolmentResponseCode getResponseCode() {
        return this.responseCode;
    }

    public EtsiTs103097Certificate getCertificate() {
        return this.certificate;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.requestHash, this.responseCode, OEROptional.getInstance(this.certificate)});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString requestHash;
        private EnrolmentResponseCode responseCode;
        private EtsiTs103097Certificate certificate;

        public Builder setRequestHash(ASN1OctetString aSN1OctetString) {
            this.requestHash = aSN1OctetString;
            return this;
        }

        public Builder setRequestHash(byte[] byArray) {
            this.requestHash = new DEROctetString(Arrays.clone(byArray));
            return this;
        }

        public Builder setResponseCode(EnrolmentResponseCode enrolmentResponseCode) {
            this.responseCode = enrolmentResponseCode;
            return this;
        }

        public Builder setCertificate(EtsiTs103097Certificate etsiTs103097Certificate) {
            this.certificate = etsiTs103097Certificate;
            return this;
        }

        public InnerEcResponse createInnerEcResponse() {
            return new InnerEcResponse(this.requestHash, this.responseCode, this.certificate);
        }
    }
}

