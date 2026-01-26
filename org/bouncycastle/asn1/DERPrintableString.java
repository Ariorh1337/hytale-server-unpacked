/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1PrintableString;

public class DERPrintableString
extends ASN1PrintableString {
    public DERPrintableString(String string) {
        this(string, false);
    }

    public DERPrintableString(String string, boolean bl) {
        super(string, bl);
    }

    DERPrintableString(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }
}

