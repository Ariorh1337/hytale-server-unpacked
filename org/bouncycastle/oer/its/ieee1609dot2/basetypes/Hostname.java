/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERUTF8String;

public class Hostname
extends ASN1Object {
    private final String hostName;

    public Hostname(String string) {
        this.hostName = string;
    }

    private Hostname(ASN1String aSN1String) {
        this.hostName = aSN1String.getString();
    }

    public static Hostname getInstance(Object object) {
        if (object instanceof Hostname) {
            return (Hostname)object;
        }
        if (object != null) {
            return new Hostname(ASN1UTF8String.getInstance(object));
        }
        return null;
    }

    public String getHostName() {
        return this.hostName;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERUTF8String(this.hostName);
    }
}

