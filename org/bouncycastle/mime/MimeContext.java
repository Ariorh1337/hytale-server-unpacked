/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.mime.Headers;

public interface MimeContext {
    public InputStream applyContext(Headers var1, InputStream var2) throws IOException;
}

