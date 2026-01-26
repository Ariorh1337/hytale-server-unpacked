/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.LastErrorException
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.IntByReference
 */
package org.jline.terminal.impl.jna.win;

import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.io.IOException;
import org.jline.terminal.impl.AbstractWindowsConsoleWriter;
import org.jline.terminal.impl.jna.win.Kernel32;

class JnaWinConsoleWriter
extends AbstractWindowsConsoleWriter {
    private final Pointer console;
    private final IntByReference writtenChars = new IntByReference();

    JnaWinConsoleWriter(Pointer console) {
        this.console = console;
    }

    @Override
    protected void writeConsole(char[] text, int len) throws IOException {
        try {
            Kernel32.INSTANCE.WriteConsoleW(this.console, text, len, this.writtenChars, null);
        }
        catch (LastErrorException e) {
            throw new IOException("Failed to write to console", e);
        }
    }
}

