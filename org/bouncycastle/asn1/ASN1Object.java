/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Encodable;

public abstract class ASN1Object
implements ASN1Encodable,
Encodable {
    public void encodeTo(OutputStream outputStream) throws IOException {
        this.toASN1Primitive().encodeTo(outputStream);
    }

    public void encodeTo(OutputStream outputStream, String string) throws IOException {
        this.toASN1Primitive().encodeTo(outputStream, string);
    }

    @Override
    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.toASN1Primitive().encodeTo(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getEncoded(String string) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.toASN1Primitive().encodeTo(byteArrayOutputStream, string);
        return byteArrayOutputStream.toByteArray();
    }

    public int hashCode() {
        return this.toASN1Primitive().hashCode();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ASN1Encodable)) {
            return false;
        }
        ASN1Encodable aSN1Encodable = (ASN1Encodable)object;
        return this.toASN1Primitive().equals(aSN1Encodable.toASN1Primitive());
    }

    protected static boolean hasEncodedTagValue(Object object, int n) {
        return object instanceof byte[] && ((byte[])object)[0] == n;
    }

    @Override
    public abstract ASN1Primitive toASN1Primitive();
}

