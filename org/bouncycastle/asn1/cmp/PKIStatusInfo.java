/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIStatus;

public class PKIStatusInfo
extends ASN1Object {
    ASN1Integer status;
    PKIFreeText statusString;
    ASN1BitString failInfo;

    private PKIStatusInfo(ASN1Sequence aSN1Sequence) {
        this.status = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        this.statusString = null;
        this.failInfo = null;
        if (aSN1Sequence.size() > 2) {
            this.statusString = PKIFreeText.getInstance(aSN1Sequence.getObjectAt(1));
            this.failInfo = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(2));
        } else if (aSN1Sequence.size() > 1) {
            ASN1Encodable aSN1Encodable = aSN1Sequence.getObjectAt(1);
            if (aSN1Encodable instanceof ASN1BitString) {
                this.failInfo = ASN1BitString.getInstance(aSN1Encodable);
            } else {
                this.statusString = PKIFreeText.getInstance(aSN1Encodable);
            }
        }
    }

    public PKIStatusInfo(PKIStatus pKIStatus) {
        this.status = ASN1Integer.getInstance(pKIStatus.toASN1Primitive());
    }

    public PKIStatusInfo(PKIStatus pKIStatus, PKIFreeText pKIFreeText) {
        this.status = ASN1Integer.getInstance(pKIStatus.toASN1Primitive());
        this.statusString = pKIFreeText;
    }

    public PKIStatusInfo(PKIStatus pKIStatus, PKIFreeText pKIFreeText, PKIFailureInfo pKIFailureInfo) {
        this.status = ASN1Integer.getInstance(pKIStatus.toASN1Primitive());
        this.statusString = pKIFreeText;
        this.failInfo = pKIFailureInfo;
    }

    public static PKIStatusInfo getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return PKIStatusInfo.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    public static PKIStatusInfo getInstance(Object object) {
        if (object instanceof PKIStatusInfo) {
            return (PKIStatusInfo)object;
        }
        if (object != null) {
            return new PKIStatusInfo(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public BigInteger getStatus() {
        return this.status.getValue();
    }

    public ASN1Integer getStatusObject() {
        return this.status;
    }

    public PKIFreeText getStatusString() {
        return this.statusString;
    }

    public ASN1BitString getFailInfo() {
        return this.failInfo;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.status);
        if (this.statusString != null) {
            aSN1EncodableVector.add(this.statusString);
        }
        if (this.failInfo != null) {
            aSN1EncodableVector.add(this.failInfo);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

