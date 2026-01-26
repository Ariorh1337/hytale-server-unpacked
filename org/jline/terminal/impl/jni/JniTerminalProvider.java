/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl.jni;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.jline.nativ.JLineNativeLoader;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.PosixPtyTerminal;
import org.jline.terminal.impl.PosixSysTerminal;
import org.jline.terminal.impl.jni.JniNativePty;
import org.jline.terminal.impl.jni.freebsd.FreeBsdNativePty;
import org.jline.terminal.impl.jni.linux.LinuxNativePty;
import org.jline.terminal.impl.jni.osx.OsXNativePty;
import org.jline.terminal.impl.jni.solaris.SolarisNativePty;
import org.jline.terminal.impl.jni.win.NativeWinSysTerminal;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.Log;
import org.jline.utils.OSUtils;

public class JniTerminalProvider
implements TerminalProvider {
    public JniTerminalProvider() {
        try {
            JLineNativeLoader.initialize();
        }
        catch (Exception e) {
            Log.debug("Failed to load JLine native library: " + e.getMessage(), e);
        }
    }

    @Override
    public String name() {
        return "jni";
    }

    public Pty current(SystemStream systemStream) throws IOException {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Linux")) {
            return LinuxNativePty.current(this, systemStream);
        }
        if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            return OsXNativePty.current(this, systemStream);
        }
        if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
            return SolarisNativePty.current(this, systemStream);
        }
        if (osName.startsWith("FreeBSD")) {
            return FreeBsdNativePty.current(this, systemStream);
        }
        throw new UnsupportedOperationException();
    }

    public Pty open(Attributes attributes, Size size) throws IOException {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Linux")) {
            return LinuxNativePty.open(this, attributes, size);
        }
        if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            return OsXNativePty.open(this, attributes, size);
        }
        if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
            return SolarisNativePty.open(this, attributes, size);
        }
        if (osName.startsWith("FreeBSD")) {
            return FreeBsdNativePty.open(this, attributes, size);
        }
        throw new UnsupportedOperationException();
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
        return NativeWinSysTerminal.createTerminal(this, systemStream, name, type, ansiPassThrough, encoding, stdinEncoding, stdoutEncoding, stderrEncoding, nativeSignals, signalHandler, paused);
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
            if (OSUtils.IS_WINDOWS) {
                return this.isWindowsSystemStream(stream);
            }
            return this.isPosixSystemStream(stream);
        }
        catch (Throwable t) {
            Log.debug("Exception while checking system stream (this may disable the JNI provider)", t);
            return false;
        }
    }

    public boolean isWindowsSystemStream(SystemStream stream) {
        return NativeWinSysTerminal.isWindowsSystemStream(stream);
    }

    public boolean isPosixSystemStream(SystemStream stream) {
        return JniNativePty.isPosixSystemStream(stream);
    }

    @Override
    public String systemStreamName(SystemStream stream) {
        return JniNativePty.posixSystemStreamName(stream);
    }

    @Override
    public int systemStreamWidth(SystemStream stream) {
        return JniNativePty.systemStreamWidth(stream);
    }

    public String toString() {
        return "TerminalProvider[" + this.name() + "]";
    }
}

