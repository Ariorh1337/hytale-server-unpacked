/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.ExplicitCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class EtsiTs103097Certificate
extends ExplicitCertificate {
    public EtsiTs103097Certificate(UINT8 uINT8, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(uINT8, issuerIdentifier, toBeSignedCertificate, signature);
    }

    protected EtsiTs103097Certificate(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static EtsiTs103097Certificate getInstance(Object object) {
        if (object instanceof EtsiTs103097Certificate) {
            return (EtsiTs103097Certificate)object;
        }
        if (object != null) {
            return new EtsiTs103097Certificate(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

