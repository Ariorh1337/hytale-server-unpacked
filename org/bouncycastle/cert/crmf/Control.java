/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface Control {
    public ASN1ObjectIdentifier getType();

    public ASN1Encodable getValue();
}

