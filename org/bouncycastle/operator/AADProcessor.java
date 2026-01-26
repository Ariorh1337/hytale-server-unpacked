/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.OutputStream;

public interface AADProcessor {
    public OutputStream getAADStream();

    public byte[] getMAC();
}

