/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1GeneralString;

public class DERGeneralString
extends ASN1GeneralString {
    public DERGeneralString(String string) {
        super(string);
    }

    DERGeneralString(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }
}

