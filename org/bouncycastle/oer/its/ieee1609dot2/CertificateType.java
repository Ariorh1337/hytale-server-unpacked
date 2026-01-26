/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

public class CertificateType
extends ASN1Enumerated {
    public static final CertificateType explicit = new CertificateType(BigInteger.ZERO);
    public static final CertificateType implicit = new CertificateType(BigInteger.ONE);

    public CertificateType(BigInteger bigInteger) {
        super(bigInteger);
        this.assertValues();
    }

    private CertificateType(ASN1Enumerated aSN1Enumerated) {
        this(aSN1Enumerated.getValue());
    }

    public static CertificateType getInstance(Object object) {
        if (object instanceof CertificateType) {
            return (CertificateType)object;
        }
        if (object != null) {
            return new CertificateType(ASN1Enumerated.getInstance(object));
        }
        return null;
    }

    protected void assertValues() {
        if (this.getValue().compareTo(BigInteger.ZERO) < 0 || this.getValue().compareTo(BigIntegers.ONE) > 0) {
            throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
        }
    }
}

