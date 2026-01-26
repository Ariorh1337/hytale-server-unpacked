/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;

public class OOBCert
extends CMPCertificate {
    public OOBCert(AttributeCertificate attributeCertificate) {
        super(1, attributeCertificate);
    }

    public OOBCert(int n, ASN1Object aSN1Object) {
        super(n, aSN1Object);
    }

    public OOBCert(Certificate certificate) {
        super(certificate);
    }

    public static OOBCert getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        if (aSN1TaggedObject != null) {
            if (bl) {
                return OOBCert.getInstance(aSN1TaggedObject.getExplicitBaseObject());
            }
            throw new IllegalArgumentException("tag must be explicit");
        }
        return null;
    }

    public static OOBCert getInstance(Object object) {
        if (object == null || object instanceof OOBCert) {
            return (OOBCert)object;
        }
        if (object instanceof CMPCertificate) {
            try {
                return OOBCert.getInstance(((CMPCertificate)object).getEncoded());
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException(iOException.getMessage(), iOException);
            }
        }
        if (object instanceof byte[]) {
            try {
                object = ASN1Primitive.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("Invalid encoding in OOBCert");
            }
        }
        if (object instanceof ASN1Sequence) {
            return new OOBCert(Certificate.getInstance(object));
        }
        if (object instanceof ASN1TaggedObject) {
            ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(object, 128);
            return new OOBCert(aSN1TaggedObject.getTagNo(), aSN1TaggedObject.getExplicitBaseObject());
        }
        throw new IllegalArgumentException("Invalid object: " + object.getClass().getName());
    }
}

