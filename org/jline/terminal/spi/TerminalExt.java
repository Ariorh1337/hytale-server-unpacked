/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.spi;

import org.jline.terminal.Terminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

public interface TerminalExt
extends Terminal {
    public TerminalProvider getProvider();

    public SystemStream getSystemStream();
}

