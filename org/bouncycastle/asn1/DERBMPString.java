/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1BMPString;

public class DERBMPString
extends ASN1BMPString {
    public DERBMPString(String string) {
        super(string);
    }

    DERBMPString(byte[] byArray) {
        super(byArray);
    }

    DERBMPString(char[] cArray) {
        super(cArray);
    }
}

