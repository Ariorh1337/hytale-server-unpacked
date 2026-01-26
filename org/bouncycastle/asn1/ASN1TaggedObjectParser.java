/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.InMemoryRepresentable;

public interface ASN1TaggedObjectParser
extends ASN1Encodable,
InMemoryRepresentable {
    public int getTagClass();

    public int getTagNo();

    public boolean hasContextTag();

    public boolean hasContextTag(int var1);

    public boolean hasTag(int var1, int var2);

    public boolean hasTagClass(int var1);

    public ASN1Encodable parseBaseUniversal(boolean var1, int var2) throws IOException;

    public ASN1Encodable parseExplicitBaseObject() throws IOException;

    public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException;

    public ASN1TaggedObjectParser parseImplicitBaseTagged(int var1, int var2) throws IOException;
}

