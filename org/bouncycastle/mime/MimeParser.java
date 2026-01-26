/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime;

import java.io.IOException;
import org.bouncycastle.mime.MimeParserListener;

public interface MimeParser {
    public void parse(MimeParserListener var1) throws IOException;
}

