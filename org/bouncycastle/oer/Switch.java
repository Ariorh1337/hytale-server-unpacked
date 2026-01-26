/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.SwitchIndexer;

public interface Switch {
    public Element result(SwitchIndexer var1);

    public ASN1Encodable[] keys();
}

