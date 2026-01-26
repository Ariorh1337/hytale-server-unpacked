/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;

public class DERDump
extends ASN1Dump {
    public static String dumpAsString(ASN1Primitive aSN1Primitive) {
        StringBuilder stringBuilder = new StringBuilder();
        DERDump._dumpAsString("", false, aSN1Primitive, stringBuilder);
        return stringBuilder.toString();
    }

    public static String dumpAsString(ASN1Encodable aSN1Encodable) {
        return DERDump.dumpAsString(aSN1Encodable.toASN1Primitive());
    }
}

