/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.util.io.Streams;

public class CMSProcessableFile
implements CMSTypedData,
CMSReadable {
    private static final int DEFAULT_BUF_SIZE = 32768;
    private final ASN1ObjectIdentifier type;
    private final File file;
    private final int bufSize;

    public CMSProcessableFile(File file) {
        this(file, 32768);
    }

    public CMSProcessableFile(File file, int n) {
        this(CMSObjectIdentifiers.data, file, n);
    }

    public CMSProcessableFile(ASN1ObjectIdentifier aSN1ObjectIdentifier, File file, int n) {
        this.type = aSN1ObjectIdentifier;
        this.file = file;
        this.bufSize = n;
    }

    @Override
    public InputStream getInputStream() throws IOException, CMSException {
        return new BufferedInputStream(new FileInputStream(this.file), this.bufSize);
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, CMSException {
        FileInputStream fileInputStream = new FileInputStream(this.file);
        Streams.pipeAll(fileInputStream, outputStream, this.bufSize);
        fileInputStream.close();
    }

    @Override
    public Object getContent() {
        return this.file;
    }

    @Override
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}

