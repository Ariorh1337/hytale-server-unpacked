/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSRuntimeException;
import org.bouncycastle.cms.CMSTypedStream;

public class PKCS7TypedStream
extends CMSTypedStream {
    private final ASN1Encodable content;

    public PKCS7TypedStream(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1ObjectIdentifier);
        this.content = aSN1Encodable;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }

    @Override
    public InputStream getContentStream() {
        try {
            return this.getContentStream(this.content);
        }
        catch (IOException iOException) {
            throw new CMSRuntimeException("unable to convert content to stream: " + iOException.getMessage(), iOException);
        }
    }

    @Override
    public void drain() throws IOException {
        this.content.toASN1Primitive();
    }

    private InputStream getContentStream(ASN1Encodable aSN1Encodable) throws IOException {
        byte by;
        byte[] byArray = aSN1Encodable.toASN1Primitive().getEncoded("DER");
        int n = 0;
        if ((byArray[n++] & 0x1F) == 31) {
            while ((byArray[n++] & 0x80) != 0) {
            }
        }
        if (((by = byArray[n++]) & 0x80) != 0) {
            n += by & 0x7F;
        }
        return new ByteArrayInputStream(byArray, n, byArray.length - n);
    }
}

