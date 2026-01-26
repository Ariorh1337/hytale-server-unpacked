/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.cms.CMSException;

public interface CMSProcessable {
    public void write(OutputStream var1) throws IOException, CMSException;

    public Object getContent();
}

