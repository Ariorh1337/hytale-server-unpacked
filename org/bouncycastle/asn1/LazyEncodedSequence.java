/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.LazyConstructionEnumeration;

class LazyEncodedSequence
extends ASN1Sequence {
    private byte[] encoded;

    LazyEncodedSequence(byte[] byArray) throws IOException {
        if (null == byArray) {
            throw new NullPointerException("'encoded' cannot be null");
        }
        this.encoded = byArray;
    }

    @Override
    public ASN1Encodable getObjectAt(int n) {
        this.force();
        return super.getObjectAt(n);
    }

    @Override
    public Enumeration getObjects() {
        byte[] byArray = this.getContents();
        if (null != byArray) {
            return new LazyConstructionEnumeration(byArray);
        }
        return super.getObjects();
    }

    @Override
    public int hashCode() {
        this.force();
        return super.hashCode();
    }

    @Override
    public Iterator<ASN1Encodable> iterator() {
        this.force();
        return super.iterator();
    }

    @Override
    public int size() {
        this.force();
        return super.size();
    }

    @Override
    public ASN1Encodable[] toArray() {
        this.force();
        return super.toArray();
    }

    @Override
    ASN1Encodable[] toArrayInternal() {
        this.force();
        return super.toArrayInternal();
    }

    @Override
    int encodedLength(boolean bl) throws IOException {
        byte[] byArray = this.getContents();
        if (null != byArray) {
            return ASN1OutputStream.getLengthOfEncodingDL(bl, byArray.length);
        }
        return super.toDLObject().encodedLength(bl);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        byte[] byArray = this.getContents();
        if (null != byArray) {
            aSN1OutputStream.writeEncodingDL(bl, 48, byArray);
            return;
        }
        super.toDLObject().encode(aSN1OutputStream, bl);
    }

    @Override
    ASN1BitString toASN1BitString() {
        return ((ASN1Sequence)this.toDLObject()).toASN1BitString();
    }

    @Override
    ASN1External toASN1External() {
        return ((ASN1Sequence)this.toDLObject()).toASN1External();
    }

    @Override
    ASN1OctetString toASN1OctetString() {
        return ((ASN1Sequence)this.toDLObject()).toASN1OctetString();
    }

    @Override
    ASN1Set toASN1Set() {
        return ((ASN1Sequence)this.toDLObject()).toASN1Set();
    }

    @Override
    ASN1Primitive toDERObject() {
        this.force();
        return super.toDERObject();
    }

    @Override
    ASN1Primitive toDLObject() {
        this.force();
        return super.toDLObject();
    }

    private synchronized void force() {
        if (null != this.encoded) {
            ASN1InputStream aSN1InputStream = new ASN1InputStream(this.encoded, true);
            try {
                ASN1EncodableVector aSN1EncodableVector = aSN1InputStream.readVector();
                aSN1InputStream.close();
                this.elements = aSN1EncodableVector.takeElements();
                this.encoded = null;
            }
            catch (IOException iOException) {
                throw new ASN1ParsingException("malformed ASN.1: " + iOException, iOException);
            }
        }
    }

    private synchronized byte[] getContents() {
        return this.encoded;
    }
}

