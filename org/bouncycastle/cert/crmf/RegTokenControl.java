/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.cert.crmf.Control;

public class RegTokenControl
implements Control {
    private static final ASN1ObjectIdentifier type = CRMFObjectIdentifiers.id_regCtrl_regToken;
    private final ASN1UTF8String token;

    public RegTokenControl(ASN1UTF8String aSN1UTF8String) {
        this.token = aSN1UTF8String;
    }

    public RegTokenControl(String string) {
        this.token = new DERUTF8String(string);
    }

    @Override
    public ASN1ObjectIdentifier getType() {
        return type;
    }

    @Override
    public ASN1Encodable getValue() {
        return this.token;
    }
}

