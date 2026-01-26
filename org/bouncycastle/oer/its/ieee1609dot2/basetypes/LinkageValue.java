/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class LinkageValue
extends DEROctetString {
    public LinkageValue(byte[] byArray) {
        super(byArray);
    }

    public LinkageValue(ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1Encodable);
    }

    public static LinkageValue getInstance(Object object) {
        if (object instanceof LinkageValue) {
            return (LinkageValue)object;
        }
        if (object != null) {
            return new LinkageValue(ASN1OctetString.getInstance(object).getOctets());
        }
        return null;
    }
}

