/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.OutputStream;
import org.bouncycastle.cms.CMSSecureReadable;

interface CMSSecureReadableWithAAD
extends CMSSecureReadable {
    public void setAADStream(OutputStream var1);

    public OutputStream getAADStream();

    public byte[] getMAC();
}

