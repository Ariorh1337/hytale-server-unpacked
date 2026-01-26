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

public class Certificate
extends CertificateBase {
    public Certificate(UINT8 uINT8, CertificateType certificateType, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(uINT8, certificateType, issuerIdentifier, toBeSignedCertificate, signature);
    }

    public Certificate(CertificateBase certificateBase) {
        this(certificateBase.getVersion(), certificateBase.getType(), certificateBase.getIssuer(), certificateBase.getToBeSigned(), certificateBase.getSignature());
    }

    protected Certificate(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static Certificate getInstance(Object object) {
        if (object instanceof Certificate) {
            return (Certificate)object;
        }
        if (object != null) {
            return new Certificate(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

