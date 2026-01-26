/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1UniversalString;

public class DERUniversalString
extends ASN1UniversalString {
    public DERUniversalString(byte[] byArray) {
        this(byArray, true);
    }

    DERUniversalString(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }
}

