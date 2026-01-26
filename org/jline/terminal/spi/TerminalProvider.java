/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.SystemStream;

public interface TerminalProvider {
    public String name();

    @Deprecated
    default public Terminal sysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused, SystemStream systemStream) throws IOException {
        return this.sysTerminal(name, type, ansiPassThrough, encoding, encoding, encoding, encoding, nativeSignals, signalHandler, paused, systemStream);
    }

    default public Terminal sysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, Charset inputEncoding, Charset outputEncoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused, SystemStream systemStream) throws IOException {
        return this.sysTerminal(name, type, ansiPassThrough, encoding, inputEncoding, outputEncoding, outputEncoding, nativeSignals, signalHandler, paused, systemStream);
    }

    @Deprecated
    public Terminal sysTerminal(String var1, String var2, boolean var3, Charset var4, Charset var5, Charset var6, Charset var7, boolean var8, Terminal.SignalHandler var9, boolean var10, SystemStream var11) throws IOException;

    @Deprecated
    default public Terminal newTerminal(String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding, Terminal.SignalHandler signalHandler, boolean paused, Attributes attributes, Size size) throws IOException {
        return this.newTerminal(name, type, masterInput, masterOutput, encoding, encoding, encoding, encoding, signalHandler, paused, attributes, size);
    }

    default public Terminal newTerminal(String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding, Charset inputEncoding, Charset outputEncoding, Terminal.SignalHandler signalHandler, boolean paused, Attributes attributes, Size size) throws IOException {
        return this.newTerminal(name, type, masterInput, masterOutput, encoding, inputEncoding, outputEncoding, outputEncoding, signalHandler, paused, attributes, size);
    }

    @Deprecated
    public Terminal newTerminal(String var1, String var2, InputStream var3, OutputStream var4, Charset var5, Charset var6, Charset var7, Charset var8, Terminal.SignalHandler var9, boolean var10, Attributes var11, Size var12) throws IOException;

    public boolean isSystemStream(SystemStream var1);

    public String systemStreamName(SystemStream var1);

    public int systemStreamWidth(SystemStream var1);

    public static TerminalProvider load(String name) throws IOException {
        InputStream is;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = TerminalProvider.class.getClassLoader();
        }
        if ((is = cl.getResourceAsStream("META-INF/services/org/jline/terminal/provider/" + name)) != null) {
            Properties props = new Properties();
            try {
                props.load(is);
                String className = props.getProperty("class");
                if (className == null) {
                    throw new IOException("No class defined in terminal provider file " + name);
                }
                Class<?> clazz = cl.loadClass(className);
                return (TerminalProvider)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new IOException("Unable to load terminal provider " + name + ": " + e.getMessage(), e);
            }
        }
        throw new IOException("Unable to find terminal provider " + name);
    }
}

