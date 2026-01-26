/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.spi;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

public interface Pty
extends Closeable {
    public InputStream getMasterInput() throws IOException;

    public OutputStream getMasterOutput() throws IOException;

    public InputStream getSlaveInput() throws IOException;

    public OutputStream getSlaveOutput() throws IOException;

    public Attributes getAttr() throws IOException;

    public void setAttr(Attributes var1) throws IOException;

    public Size getSize() throws IOException;

    public void setSize(Size var1) throws IOException;

    public SystemStream getSystemStream();

    public TerminalProvider getProvider();
}

