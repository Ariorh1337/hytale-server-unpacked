/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1VideotexString;

public class DERVideotexString
extends ASN1VideotexString {
    public DERVideotexString(byte[] byArray) {
        this(byArray, true);
    }

    DERVideotexString(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }
}

