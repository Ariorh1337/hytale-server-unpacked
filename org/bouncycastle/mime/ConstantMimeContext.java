/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeMultipartContext;

public class ConstantMimeContext
implements MimeContext,
MimeMultipartContext {
    public static final ConstantMimeContext Instance = new ConstantMimeContext();

    @Override
    public InputStream applyContext(Headers headers, InputStream inputStream) throws IOException {
        return inputStream;
    }

    @Override
    public MimeContext createContext(int n) throws IOException {
        return this;
    }
}

