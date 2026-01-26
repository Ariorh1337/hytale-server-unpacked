/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BitStringParser;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.BERBitString;
import org.bouncycastle.asn1.ConstructedBitStream;
import org.bouncycastle.util.io.Streams;

@Deprecated
public class BERBitStringParser
implements ASN1BitStringParser {
    private ASN1StreamParser _parser;
    private ConstructedBitStream _bitStream;

    BERBitStringParser(ASN1StreamParser aSN1StreamParser) {
        this._parser = aSN1StreamParser;
    }

    @Override
    public InputStream getOctetStream() throws IOException {
        this._bitStream = new ConstructedBitStream(this._parser, true);
        return this._bitStream;
    }

    @Override
    public InputStream getBitStream() throws IOException {
        this._bitStream = new ConstructedBitStream(this._parser, false);
        return this._bitStream;
    }

    @Override
    public int getPadBits() {
        return this._bitStream.getPadBits();
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return BERBitStringParser.parse(this._parser);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (IOException iOException) {
            throw new ASN1ParsingException("IOException converting stream to byte array: " + iOException.getMessage(), iOException);
        }
    }

    static BERBitString parse(ASN1StreamParser aSN1StreamParser) throws IOException {
        ConstructedBitStream constructedBitStream = new ConstructedBitStream(aSN1StreamParser, false);
        byte[] byArray = Streams.readAll(constructedBitStream);
        int n = constructedBitStream.getPadBits();
        return new BERBitString(byArray, n);
    }
}

