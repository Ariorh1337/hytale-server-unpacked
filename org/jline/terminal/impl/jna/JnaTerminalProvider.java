/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl.jna;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.PosixPtyTerminal;
import org.jline.terminal.impl.PosixSysTerminal;
import org.jline.terminal.impl.jna.JnaNativePty;
import org.jline.terminal.impl.jna.win.JnaWinSysTerminal;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.OSUtils;

public class JnaTerminalProvider
implements TerminalProvider {
    public JnaTerminalProvider() {
        this.checkSystemStream(SystemStream.Output);
    }

    @Override
    public String name() {
        return "jna";
    }

    public Pty current(SystemStream systemStream) throws IOException {
        return JnaNativePty.current(this, systemStream);
    }

    public Pty open(Attributes attributes, Size size) throws IOException {
        return JnaNativePty.open(this, attributes, size);
    }

    @Override
    public Terminal sysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, Charset inputEncoding, Charset outputEncoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused, SystemStream systemStream) throws IOException {
        if (OSUtils.IS_WINDOWS) {
            return this.winSysTerminal(name, type, ansiPassThrough, encoding, inputEncoding, outputEncoding, outputEncoding, nativeSignals, signalHandler, paused, systemStream);
        }
        return this.posixSysTerminal(name, type, ansiPassThrough, encoding, inputEncoding, outputEncoding, outputEncoding, nativeSignals, signalHandler, paused, systemStream);
    }

    @Override
    @Deprecated
    public Terminal sysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, Charset stdinEncoding, Charset stdoutEncoding, Charset stderrEncoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused, SystemStream systemStream) throws IOException {
        if (OSUtils.IS_WINDOWS) {
            return this.winSysTerminal(name, type, ansiPassThrough, encoding, stdinEncoding, stdoutEncoding, stderrEncoding, nativeSignals, signalHandler, paused, systemStream);
        }
        return this.posixSysTerminal(name, type, ansiPassThrough, encoding, stdinEncoding, stdoutEncoding, stderrEncoding, nativeSignals, signalHandler, paused, systemStream);
    }

    public Terminal winSysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused, SystemStream systemStream) throws IOException {
        return this.winSysTerminal(name, type, ansiPassThrough, encoding, encoding, encoding, encoding, nativeSignals, signalHandler, paused, systemStream);
    }

    public Terminal winSysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, Charset stdinEncoding, Charset stdoutEncoding, Charset stderrEncoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused, SystemStream systemStream) throws IOException {
        return JnaWinSysTerminal.createTerminal(this, systemStream, name, type, ansiPassThrough, encoding, stdinEncoding, stdoutEncoding, stderrEncoding, nativeSignals, signalHandler, paused);
    }

    public Terminal posixSysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused, SystemStream systemStream) throws IOException {
        return this.posixSysTerminal(name, type, ansiPassThrough, encoding, encoding, encoding, encoding, nativeSignals, signalHandler, paused, systemStream);
    }

    public Terminal posixSysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, Charset stdinEncoding, Charset stdoutEncoding, Charset stderrEncoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused, SystemStream systemStream) throws IOException {
        Pty pty = this.current(systemStream);
        Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
        return new PosixSysTerminal(name, type, pty, encoding, stdinEncoding, outputEncoding, nativeSignals, signalHandler);
    }

    @Override
    public Terminal newTerminal(String name, String type, InputStream in, OutputStream out, Charset encoding, Charset inputEncoding, Charset outputEncoding, Terminal.SignalHandler signalHandler, boolean paused, Attributes attributes, Size size) throws IOException {
        Pty pty = this.open(attributes, size);
        return new PosixPtyTerminal(name, type, pty, in, out, encoding, inputEncoding, outputEncoding, signalHandler, paused);
    }

    @Override
    @Deprecated
    public Terminal newTerminal(String name, String type, InputStream in, OutputStream out, Charset encoding, Charset stdinEncoding, Charset stdoutEncoding, Charset stderrEncoding, Terminal.SignalHandler signalHandler, boolean paused, Attributes attributes, Size size) throws IOException {
        Pty pty = this.open(attributes, size);
        return new PosixPtyTerminal(name, type, pty, in, out, encoding, stdinEncoding, stdoutEncoding, signalHandler, paused);
    }

    @Override
    public boolean isSystemStream(SystemStream stream) {
        try {
            return this.checkSystemStream(stream);
        }
        catch (Throwable t) {
            return false;
        }
    }

    private boolean checkSystemStream(SystemStream stream) {
        if (OSUtils.IS_WINDOWS) {
            return JnaWinSysTerminal.isWindowsSystemStream(stream);
        }
        return JnaNativePty.isPosixSystemStream(stream);
    }

    @Override
    public String systemStreamName(SystemStream stream) {
        if (OSUtils.IS_WINDOWS) {
            return null;
        }
        return JnaNativePty.posixSystemStreamName(stream);
    }

    @Override
    public int systemStreamWidth(SystemStream stream) {
        int n;
        block8: {
            Pty pty = this.current(stream);
            try {
                n = pty.getSize().getColumns();
                if (pty == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (pty != null) {
                        try {
                            pty.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Throwable t) {
                    return -1;
                }
            }
            pty.close();
        }
        return n;
    }

    public String toString() {
        return "TerminalProvider[" + this.name() + "]";
    }
}

