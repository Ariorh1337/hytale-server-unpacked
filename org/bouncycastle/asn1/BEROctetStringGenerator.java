/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.BERGenerator;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;

public class BEROctetStringGenerator
extends BERGenerator {
    public BEROctetStringGenerator(OutputStream outputStream) throws IOException {
        super(outputStream);
        this.writeBERHeader(36);
    }

    public BEROctetStringGenerator(OutputStream outputStream, int n, boolean bl) throws IOException {
        super(outputStream, n, bl);
        this.writeBERHeader(36);
    }

    public OutputStream getOctetOutputStream() {
        return this.getOctetOutputStream(new byte[1000]);
    }

    public OutputStream getOctetOutputStream(byte[] byArray) {
        return new BufferedBEROctetStream(byArray);
    }

    private class BufferedBEROctetStream
    extends OutputStream {
        private byte[] _buf;
        private int _off;
        private DEROutputStream _derOut;

        BufferedBEROctetStream(byte[] byArray) {
            this._buf = byArray;
            this._off = 0;
            this._derOut = new DEROutputStream(BEROctetStringGenerator.this._out);
        }

        @Override
        public void write(int n) throws IOException {
            this._buf[this._off++] = (byte)n;
            if (this._off == this._buf.length) {
                DEROctetString.encode(this._derOut, true, this._buf, 0, this._buf.length);
                this._off = 0;
            }
        }

        @Override
        public void write(byte[] byArray, int n, int n2) throws IOException {
            int n3;
            int n4 = this._buf.length;
            int n5 = n4 - this._off;
            if (n2 < n5) {
                System.arraycopy(byArray, n, this._buf, this._off, n2);
                this._off += n2;
                return;
            }
            int n6 = 0;
            if (this._off > 0) {
                System.arraycopy(byArray, n, this._buf, this._off, n5);
                n6 += n5;
                DEROctetString.encode(this._derOut, true, this._buf, 0, n4);
            }
            while ((n3 = n2 - n6) >= n4) {
                DEROctetString.encode(this._derOut, true, byArray, n + n6, n4);
                n6 += n4;
            }
            System.arraycopy(byArray, n + n6, this._buf, 0, n3);
            this._off = n3;
        }

        @Override
        public void close() throws IOException {
            if (this._off != 0) {
                DEROctetString.encode(this._derOut, true, this._buf, 0, this._off);
            }
            this._derOut.flushInternal();
            BEROctetStringGenerator.this.writeBEREnd();
        }
    }
}

