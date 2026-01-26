/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.BigIntegers;

public class CertificateFormat
extends ASN1Object {
    private final int format;

    public CertificateFormat(int n) {
        this.format = n;
    }

    public CertificateFormat(BigInteger bigInteger) {
        this.format = BigIntegers.intValueExact(bigInteger);
    }

    private CertificateFormat(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public int getFormat() {
        return this.format;
    }

    public static CertificateFormat getInstance(Object object) {
        if (object instanceof CertificateFormat) {
            return (CertificateFormat)object;
        }
        if (object != null) {
            return new CertificateFormat(ASN1Integer.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.format);
    }
}

