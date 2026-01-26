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

public class ExplicitCertificate
extends CertificateBase {
    public ExplicitCertificate(CertificateBase certificateBase) {
        this(certificateBase.getVersion(), certificateBase.getIssuer(), certificateBase.getToBeSigned(), certificateBase.getSignature());
    }

    public ExplicitCertificate(UINT8 uINT8, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(uINT8, CertificateType.explicit, issuerIdentifier, toBeSignedCertificate, signature);
    }

    protected ExplicitCertificate(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        if (!this.getType().equals(CertificateType.explicit)) {
            throw new IllegalArgumentException("object was certificate base but the type was not explicit");
        }
    }

    public static ExplicitCertificate getInstance(Object object) {
        if (object instanceof ExplicitCertificate) {
            return (ExplicitCertificate)object;
        }
        if (object != null) {
            return new ExplicitCertificate(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

