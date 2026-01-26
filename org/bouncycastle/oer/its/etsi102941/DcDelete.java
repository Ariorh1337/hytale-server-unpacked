/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;

public class DcDelete
extends ASN1Object {
    private final String url;

    public DcDelete(String string) {
        this.url = string;
    }

    private DcDelete(ASN1IA5String aSN1IA5String) {
        this.url = aSN1IA5String.getString();
    }

    public static DcDelete getInstance(Object object) {
        if (object instanceof DcDelete) {
            return (DcDelete)object;
        }
        if (object != null) {
            return new DcDelete(ASN1IA5String.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERIA5String(this.url);
    }

    public String getUrl() {
        return this.url;
    }
}

