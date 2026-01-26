/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.CursorSupport;
import org.jline.terminal.impl.LineDisciplineTerminal;
import org.jline.terminal.spi.TerminalProvider;

public class ExternalTerminal
extends LineDisciplineTerminal {
    private final TerminalProvider provider;
    protected final AtomicBoolean closed = new AtomicBoolean();
    protected final InputStream masterInput;
    protected final Object lock = new Object();
    protected boolean paused = true;
    protected Thread pumpThread;

    public ExternalTerminal(String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding) throws IOException {
        this(null, name, type, masterInput, masterOutput, encoding, encoding, encoding, encoding, Terminal.SignalHandler.SIG_DFL);
    }

    public ExternalTerminal(TerminalProvider provider, String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
        this(provider, name, type, masterInput, masterOutput, encoding, encoding, encoding, signalHandler, false);
    }

    public ExternalTerminal(TerminalProvider provider, String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding, Charset stdinEncoding, Charset stdoutEncoding, Charset stderrEncoding, Terminal.SignalHandler signalHandler) throws IOException {
        this(provider, name, type, masterInput, masterOutput, encoding, stdinEncoding, stdoutEncoding, signalHandler, false);
    }

    public ExternalTerminal(TerminalProvider provider, String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding, Terminal.SignalHandler signalHandler, boolean paused) throws IOException {
        this(provider, name, type, masterInput, masterOutput, encoding, encoding, encoding, signalHandler, paused, null, null);
    }

    public ExternalTerminal(TerminalProvider provider, String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding, Charset inputEncoding, Charset outputEncoding, Terminal.SignalHandler signalHandler, boolean paused) throws IOException {
        this(provider, name, type, masterInput, masterOutput, encoding, inputEncoding, outputEncoding, signalHandler, paused, null, null);
    }

    public ExternalTerminal(TerminalProvider provider, String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding, Terminal.SignalHandler signalHandler, boolean paused, Attributes attributes, Size size) throws IOException {
        this(provider, name, type, masterInput, masterOutput, encoding, encoding, encoding, signalHandler, paused, attributes, size);
    }

    public ExternalTerminal(TerminalProvider provider, String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding, Charset inputEncoding, Charset outputEncoding, Terminal.SignalHandler signalHandler, boolean paused, Attributes attributes, Size size) throws IOException {
        super(name, type, masterOutput, encoding, inputEncoding, outputEncoding, signalHandler);
        this.provider = provider;
        this.masterInput = masterInput;
        if (attributes != null) {
            this.setAttributes(attributes);
        }
        if (size != null) {
            this.setSize(size);
        }
        if (!paused) {
            this.resume();
        }
    }

    @Override
    protected void doClose() throws IOException {
        if (this.closed.compareAndSet(false, true)) {
            this.pause();
            super.doClose();
        }
    }

    @Override
    public boolean canPauseResume() {
        return true;
    }

    @Override
    public void pause() {
        try {
            this.pause(false);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void pause(boolean wait) throws InterruptedException {
        Thread p;
        Object object = this.lock;
        synchronized (object) {
            this.paused = true;
            p = this.pumpThread;
        }
        if (p != null) {
            p.interrupt();
            if (wait) {
                p.join();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void resume() {
        Object object = this.lock;
        synchronized (object) {
            this.paused = false;
            if (this.pumpThread == null) {
                this.pumpThread = new Thread(this::pump, this.toString() + " input pump thread");
                this.pumpThread.setDaemon(true);
                this.pumpThread.start();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean paused() {
        Object object = this.lock;
        synchronized (object) {
            return this.paused;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void pump() {
        block23: {
            block22: {
                try {
                    buf /* !! */  = new byte[1024];
                    while (true) {
                        if ((c = this.masterInput.read(buf /* !! */ )) >= 0) {
                            this.processInputBytes(buf /* !! */ , 0, c);
                        }
                        if (c < 0 || this.closed.get()) break;
                        var3_6 = this.lock;
                        synchronized (var3_6) {
                            if (this.paused) {
                                this.pumpThread = null;
                                // MONITOREXIT @DISABLED, blocks:[0, 19, 21, 13] lbl11 : MonitorExitStatement: MONITOREXIT : var3_6
                                var4_7 = this.lock;
                                break block22;
                            }
                        }
                    }
                    v0 = this.lock;
                    break block23;
                }
                catch (IOException e) {
                    this.processIOException(e);
                    ** break block24
                }
            }
            synchronized (var4_7) {
                this.pumpThread = null;
                return;
            }
        }
        buf /* !! */  = (byte[])v0;
        synchronized (v0) {
            this.pumpThread = null;
            // ** MonitorExit[buf /* !! */ ] (shouldn't be in output)
            ** break block24
        }
        finally {
            var1_3 = this.lock;
            synchronized (var1_3) {
                this.pumpThread = null;
            }
        }
lbl-1000:
        // 2 sources

        {
            try {
                this.slaveInput.close();
                return;
            }
            catch (IOException var1_4) {
                // empty catch block
            }
            return;
        }
    }

    @Override
    public Cursor getCursorPosition(IntConsumer discarded) {
        return CursorSupport.getCursorPosition(this, discarded);
    }

    @Override
    public TerminalProvider getProvider() {
        return this.provider;
    }
}

