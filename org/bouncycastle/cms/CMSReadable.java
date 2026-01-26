/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.cms.CMSException;

interface CMSReadable {
    public InputStream getInputStream() throws IOException, CMSException;
}

