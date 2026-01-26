/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class ImplicitCertificate
extends CertificateBase {
    public ImplicitCertificate(CertificateBase certificateBase) {
        this(certificateBase.getVersion(), certificateBase.getIssuer(), certificateBase.getToBeSigned(), certificateBase.getSignature());
    }

    public ImplicitCertificate(UINT8 uINT8, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(uINT8, CertificateType.implicit, issuerIdentifier, toBeSignedCertificate, signature);
    }

    private ImplicitCertificate(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        if (!this.getType().equals(CertificateType.implicit)) {
            throw new IllegalArgumentException("object was certificate base but the type was not implicit");
        }
    }

    public static ImplicitCertificate getInstance(Object object) {
        if (object instanceof ImplicitCertificate) {
            return (ImplicitCertificate)object;
        }
        if (object != null) {
            return new ImplicitCertificate(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

