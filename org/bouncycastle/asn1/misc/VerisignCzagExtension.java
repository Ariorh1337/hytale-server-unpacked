/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.DERIA5String;

public class VerisignCzagExtension
extends DERIA5String {
    public VerisignCzagExtension(ASN1IA5String aSN1IA5String) {
        super(aSN1IA5String.getString());
    }

    @Override
    public String toString() {
        return "VerisignCzagExtension: " + this.getString();
    }
}

