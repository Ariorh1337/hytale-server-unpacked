/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class SubjectAssurance
extends DEROctetString {
    public SubjectAssurance(byte[] byArray) {
        super(byArray);
        if (byArray.length != 1) {
            throw new IllegalArgumentException("length is not 1");
        }
    }

    private SubjectAssurance(ASN1OctetString aSN1OctetString) {
        this(aSN1OctetString.getOctets());
    }

    public static SubjectAssurance getInstance(Object object) {
        if (object instanceof SubjectAssurance) {
            return (SubjectAssurance)object;
        }
        if (object != null) {
            return new SubjectAssurance(DEROctetString.getInstance(object));
        }
        return null;
    }
}

