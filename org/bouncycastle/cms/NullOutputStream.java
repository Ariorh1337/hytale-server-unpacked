/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

class NullOutputStream
extends OutputStream {
    NullOutputStream() {
    }

    @Override
    public void write(byte[] byArray) throws IOException {
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
    }

    @Override
    public void write(int n) throws IOException {
    }
}

