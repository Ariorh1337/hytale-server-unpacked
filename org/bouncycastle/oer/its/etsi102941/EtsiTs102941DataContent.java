/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.etsi102941.AuthorizationValidationRequest;
import org.bouncycastle.oer.its.etsi102941.AuthorizationValidationResponse;
import org.bouncycastle.oer.its.etsi102941.CaCertificateRequest;
import org.bouncycastle.oer.its.etsi102941.InnerAtRequest;
import org.bouncycastle.oer.its.etsi102941.InnerAtResponse;
import org.bouncycastle.oer.its.etsi102941.InnerEcRequestSignedForPop;
import org.bouncycastle.oer.its.etsi102941.InnerEcResponse;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedRcaCtl;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedTlmCtl;

public class EtsiTs102941DataContent
extends ASN1Object
implements ASN1Choice {
    public static final int enrolmentRequest = 0;
    public static final int enrolmentResponse = 1;
    public static final int authorizationRequest = 2;
    public static final int authorizationResponse = 3;
    public static final int certificateRevocationList = 4;
    public static final int certificateTrustListTlm = 5;
    public static final int certificateTrustListRca = 6;
    public static final int authorizationValidationRequest = 7;
    public static final int authorizationValidationResponse = 8;
    public static final int caCertificateRequest = 9;
    public static final int linkCertificateTlm = 10;
    public static final int singleSignedLinkCertificateRca = 11;
    public static final int doubleSignedlinkCertificateRca = 12;
    private final int choice;
    private final ASN1Encodable etsiTs102941DataContent;

    public EtsiTs102941DataContent(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.etsiTs102941DataContent = aSN1Encodable;
    }

    private EtsiTs102941DataContent(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.etsiTs102941DataContent = InnerEcRequestSignedForPop.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 1: {
                this.etsiTs102941DataContent = InnerEcResponse.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 2: {
                this.etsiTs102941DataContent = InnerAtRequest.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 3: {
                this.etsiTs102941DataContent = InnerAtResponse.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 5: {
                this.etsiTs102941DataContent = ToBeSignedTlmCtl.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 6: {
                this.etsiTs102941DataContent = ToBeSignedRcaCtl.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 7: {
                this.etsiTs102941DataContent = AuthorizationValidationRequest.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 8: {
                this.etsiTs102941DataContent = AuthorizationValidationResponse.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 9: {
                this.etsiTs102941DataContent = CaCertificateRequest.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("choice not implemented " + this.choice);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEtsiTs102941DataContent() {
        return this.etsiTs102941DataContent;
    }

    public static EtsiTs102941DataContent getInstance(Object object) {
        if (object instanceof EtsiTs102941DataContent) {
            return (EtsiTs102941DataContent)object;
        }
        if (object != null) {
            return new EtsiTs102941DataContent(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.etsiTs102941DataContent);
    }
}

