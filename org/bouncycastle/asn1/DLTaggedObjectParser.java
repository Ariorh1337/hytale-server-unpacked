/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.BERTaggedObjectParser;

class DLTaggedObjectParser
extends BERTaggedObjectParser {
    private final boolean _constructed;

    DLTaggedObjectParser(int n, int n2, boolean bl, ASN1StreamParser aSN1StreamParser) {
        super(n, n2, aSN1StreamParser);
        this._constructed = bl;
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return this._parser.loadTaggedDL(this._tagClass, this._tagNo, this._constructed);
    }

    @Override
    public ASN1Encodable parseBaseUniversal(boolean bl, int n) throws IOException {
        if (bl) {
            return this.checkConstructed().parseObject(n);
        }
        return this._constructed ? this._parser.parseImplicitConstructedDL(n) : this._parser.parseImplicitPrimitive(n);
    }

    @Override
    public ASN1Encodable parseExplicitBaseObject() throws IOException {
        return this.checkConstructed().readObject();
    }

    @Override
    public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException {
        return this.checkConstructed().parseTaggedObject();
    }

    @Override
    public ASN1TaggedObjectParser parseImplicitBaseTagged(int n, int n2) throws IOException {
        return new DLTaggedObjectParser(n, n2, this._constructed, this._parser);
    }

    private ASN1StreamParser checkConstructed() throws IOException {
        if (!this._constructed) {
            throw new IOException("Explicit tags must be constructed (see X.690 8.14.2)");
        }
        return this._parser;
    }
}

