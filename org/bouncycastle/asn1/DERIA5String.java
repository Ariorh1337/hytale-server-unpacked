/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1IA5String;

public class DERIA5String
extends ASN1IA5String {
    public DERIA5String(String string) {
        this(string, false);
    }

    public DERIA5String(String string, boolean bl) {
        super(string, bl);
    }

    DERIA5String(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }
}

