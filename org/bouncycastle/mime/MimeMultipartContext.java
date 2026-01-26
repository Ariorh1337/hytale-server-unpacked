/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime;

import java.io.IOException;
import org.bouncycastle.mime.MimeContext;

public interface MimeMultipartContext
extends MimeContext {
    public MimeContext createContext(int var1) throws IOException;
}

