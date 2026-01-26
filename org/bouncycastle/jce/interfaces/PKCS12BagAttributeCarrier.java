/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.interfaces;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface PKCS12BagAttributeCarrier {
    public void setBagAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2);

    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier var1);

    public Enumeration getBagAttributeKeys();

    public boolean hasFriendlyName();

    public void setFriendlyName(String var1);
}

