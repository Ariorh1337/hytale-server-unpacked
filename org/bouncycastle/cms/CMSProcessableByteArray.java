/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.util.Arrays;

public class CMSProcessableByteArray
implements CMSTypedData,
CMSReadable {
    private final ASN1ObjectIdentifier type;
    private final byte[] bytes;

    public CMSProcessableByteArray(byte[] byArray) {
        this(CMSObjectIdentifiers.data, byArray);
    }

    public CMSProcessableByteArray(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] byArray) {
        this.type = aSN1ObjectIdentifier;
        this.bytes = byArray;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, CMSException {
        outputStream.write(this.bytes);
    }

    @Override
    public Object getContent() {
        return Arrays.clone(this.bytes);
    }

    @Override
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}

