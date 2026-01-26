/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.util.io.Streams;

class CMSProcessableInputStream
implements CMSProcessable,
CMSReadable {
    private InputStream input;
    private boolean used = false;

    public CMSProcessableInputStream(InputStream inputStream) {
        this.input = inputStream;
    }

    @Override
    public InputStream getInputStream() {
        this.checkSingleUsage();
        return this.input;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, CMSException {
        this.checkSingleUsage();
        Streams.pipeAll(this.input, outputStream);
        this.input.close();
    }

    @Override
    public Object getContent() {
        return this.getInputStream();
    }

    private synchronized void checkSingleUsage() {
        if (this.used) {
            throw new IllegalStateException("CMSProcessableInputStream can only be used once");
        }
        this.used = true;
    }
}

