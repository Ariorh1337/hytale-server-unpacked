/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1NumericString;

public class DERNumericString
extends ASN1NumericString {
    public DERNumericString(String string) {
        this(string, false);
    }

    public DERNumericString(String string, boolean bl) {
        super(string, bl);
    }

    DERNumericString(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }
}

