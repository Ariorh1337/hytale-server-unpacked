/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1VisibleString;

public class DERVisibleString
extends ASN1VisibleString {
    public DERVisibleString(String string) {
        super(string);
    }

    DERVisibleString(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }
}

