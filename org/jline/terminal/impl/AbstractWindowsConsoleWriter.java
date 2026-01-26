/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl;

import java.io.IOException;
import java.io.Writer;

public abstract class AbstractWindowsConsoleWriter
extends Writer {
    protected abstract void writeConsole(char[] var1, int var2) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] text = cbuf;
        if (off != 0) {
            text = new char[len];
            System.arraycopy(cbuf, off, text, 0, len);
        }
        Object object = this.lock;
        synchronized (object) {
            this.writeConsole(text, len);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}

