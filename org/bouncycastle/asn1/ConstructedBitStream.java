/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BitStringParser;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1StreamParser;

class ConstructedBitStream
extends InputStream {
    private final ASN1StreamParser _parser;
    private final boolean _octetAligned;
    private boolean _first = true;
    private int _padBits = 0;
    private ASN1BitStringParser _currentParser;
    private InputStream _currentStream;

    ConstructedBitStream(ASN1StreamParser aSN1StreamParser, boolean bl) {
        this._parser = aSN1StreamParser;
        this._octetAligned = bl;
    }

    int getPadBits() {
        return this._padBits;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = this._currentParser.getBitStream();
        }
        int n3 = 0;
        while (true) {
            int n4;
            if ((n4 = this._currentStream.read(byArray, n + n3, n2 - n3)) >= 0) {
                if ((n3 += n4) != n2) continue;
                return n3;
            }
            this._padBits = this._currentParser.getPadBits();
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
                this._currentStream = null;
                return n3 < 1 ? -1 : n3;
            }
            this._currentStream = this._currentParser.getBitStream();
        }
    }

    @Override
    public int read() throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = this._currentParser.getBitStream();
        }
        int n;
        while ((n = this._currentStream.read()) < 0) {
            this._padBits = this._currentParser.getPadBits();
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = this._currentParser.getBitStream();
        }
        return n;
    }

    private ASN1BitStringParser getNextParser() throws IOException {
        ASN1Encodable aSN1Encodable = this._parser.readObject();
        if (aSN1Encodable == null) {
            if (this._octetAligned && this._padBits != 0) {
                throw new IOException("expected octet-aligned bitstring, but found padBits: " + this._padBits);
            }
            return null;
        }
        if (aSN1Encodable instanceof ASN1BitStringParser) {
            if (this._padBits != 0) {
                throw new IOException("only the last nested bitstring can have padding");
            }
            return (ASN1BitStringParser)aSN1Encodable;
        }
        throw new IOException("unknown object encountered: " + aSN1Encodable.getClass());
    }
}

