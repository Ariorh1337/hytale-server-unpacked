/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class LinkageSeed
extends ASN1Object {
    private final byte[] linkageSeed;

    public LinkageSeed(byte[] byArray) {
        if (byArray.length != 16) {
            throw new IllegalArgumentException("linkage seed not 16 bytes");
        }
        this.linkageSeed = Arrays.clone(byArray);
    }

    private LinkageSeed(ASN1OctetString aSN1OctetString) {
        this(aSN1OctetString.getOctets());
    }

    public static LinkageSeed getInstance(Object object) {
        if (object instanceof LinkageSeed) {
            return (LinkageSeed)object;
        }
        if (object != null) {
            return new LinkageSeed(DEROctetString.getInstance(object));
        }
        return null;
    }

    public byte[] getLinkageSeed() {
        return this.linkageSeed;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.linkageSeed);
    }
}

