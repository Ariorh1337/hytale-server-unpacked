/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.InMemoryRepresentable;

public interface ASN1BitStringParser
extends ASN1Encodable,
InMemoryRepresentable {
    public InputStream getBitStream() throws IOException;

    public InputStream getOctetStream() throws IOException;

    public int getPadBits();
}

