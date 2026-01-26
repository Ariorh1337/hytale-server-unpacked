/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;

interface MACProvider {
    public byte[] getMAC();

    public void init() throws IOException;
}

