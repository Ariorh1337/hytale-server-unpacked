/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1GraphicString;

public class DERGraphicString
extends ASN1GraphicString {
    public DERGraphicString(byte[] byArray) {
        this(byArray, true);
    }

    DERGraphicString(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }
}

