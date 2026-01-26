/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.cms.CMSTypedData;

public class CMSAbsentContent
implements CMSTypedData,
CMSReadable {
    private final ASN1ObjectIdentifier type;

    public CMSAbsentContent() {
        this(CMSObjectIdentifiers.data);
    }

    public CMSAbsentContent(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.type = aSN1ObjectIdentifier;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, CMSException {
    }

    @Override
    public Object getContent() {
        return null;
    }

    @Override
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}

