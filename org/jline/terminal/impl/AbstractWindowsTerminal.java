/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractTerminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingPumpReader;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.ShutdownHooks;
import org.jline.utils.Signals;
import org.jline.utils.WriterOutputStream;

public abstract class AbstractWindowsTerminal<Console>
extends AbstractTerminal {
    public static final String TYPE_WINDOWS = "windows";
    public static final String TYPE_WINDOWS_256_COLOR = "windows-256color";
    protected static final int FOREGROUND_BLUE = 1;
    protected static final int FOREGROUND_GREEN = 2;
    protected static final int FOREGROUND_RED = 4;
    protected static final int FOREGROUND_INTENSITY = 8;
    protected static final int BACKGROUND_BLUE = 16;
    protected static final int BACKGROUND_GREEN = 32;
    protected static final int BACKGROUND_RED = 64;
    protected static final int BACKGROUND_INTENSITY = 128;
    public static final String TYPE_WINDOWS_CONEMU = "windows-conemu";
    public static final String TYPE_WINDOWS_VTP = "windows-vtp";
    public static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
    private static final int UTF8_CODE_PAGE = 65001;
    protected static final int ENABLE_PROCESSED_INPUT = 1;
    protected static final int ENABLE_LINE_INPUT = 2;
    protected static final int ENABLE_ECHO_INPUT = 4;
    protected static final int ENABLE_WINDOW_INPUT = 8;
    protected static final int ENABLE_MOUSE_INPUT = 16;
    protected static final int ENABLE_INSERT_MODE = 32;
    protected static final int ENABLE_QUICK_EDIT_MODE = 64;
    protected static final int ENABLE_EXTENDED_FLAGS = 128;
    protected final Writer slaveInputPipe;
    protected final NonBlockingInputStream input;
    protected final OutputStream output;
    protected final NonBlockingReader reader;
    protected final PrintWriter writer;
    protected final Map<Terminal.Signal, Object> nativeHandlers = new HashMap<Terminal.Signal, Object>();
    protected final ShutdownHooks.Task closer;
    protected final Attributes attributes = new Attributes();
    protected final Console inConsole;
    protected final Console outConsole;
    protected final int originalInConsoleMode;
    protected final int originalOutConsoleMode;
    private final TerminalProvider provider;
    private final SystemStream systemStream;
    protected final Object lock = new Object();
    protected boolean paused = true;
    protected Thread pump;
    protected Terminal.MouseTracking tracking = Terminal.MouseTracking.Off;
    protected boolean focusTracking = false;
    private volatile boolean closing;
    protected boolean skipNextLf;
    static final int SHIFT_FLAG = 1;
    static final int ALT_FLAG = 2;
    static final int CTRL_FLAG = 4;
    static final int RIGHT_ALT_PRESSED = 1;
    static final int LEFT_ALT_PRESSED = 2;
    static final int RIGHT_CTRL_PRESSED = 4;
    static final int LEFT_CTRL_PRESSED = 8;
    static final int SHIFT_PRESSED = 16;
    static final int NUMLOCK_ON = 32;
    static final int SCROLLLOCK_ON = 64;
    static final int CAPSLOCK_ON = 128;
    protected static final int[] ANSI_COLORS = new int[]{0, 0xCD0000, 52480, 0xCDCD00, 238, 0xCD00CD, 52685, 0xE5E5E5, 0x7F7F7F, 0xFF0000, 65280, 0xFFFF00, 0x5C5CFF, 0xFF00FF, 65535, 0xFFFFFF};

    public AbstractWindowsTerminal(TerminalProvider provider, SystemStream systemStream, Writer writer, String name, String type, Charset encoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, Console inConsole, int inConsoleMode, Console outConsole, int outConsoleMode) throws IOException {
        this(provider, systemStream, writer, name, type, encoding, encoding, encoding, nativeSignals, signalHandler, inConsole, inConsoleMode, outConsole, outConsoleMode);
    }

    public AbstractWindowsTerminal(TerminalProvider provider, SystemStream systemStream, Writer writer, String name, String type, Charset encoding, Charset inputEncoding, Charset outputEncoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, Console inConsole, int inConsoleMode, Console outConsole, int outConsoleMode) throws IOException {
        super(name, type, encoding, inputEncoding, outputEncoding, signalHandler);
        this.provider = provider;
        this.systemStream = systemStream;
        NonBlockingPumpReader reader = NonBlocking.nonBlockingPumpReader();
        this.slaveInputPipe = reader.getWriter();
        this.reader = reader;
        this.input = NonBlocking.nonBlockingStream(reader, this.inputEncoding());
        this.writer = new PrintWriter(writer);
        this.output = new WriterOutputStream(writer, this.outputEncoding());
        this.inConsole = inConsole;
        this.outConsole = outConsole;
        this.parseInfoCmp();
        this.originalInConsoleMode = inConsoleMode;
        this.originalOutConsoleMode = outConsoleMode;
        this.attributes.setLocalFlag(Attributes.LocalFlag.ISIG, true);
        this.attributes.setControlChar(Attributes.ControlChar.VINTR, this.ctrl('C'));
        this.attributes.setControlChar(Attributes.ControlChar.VEOF, this.ctrl('D'));
        this.attributes.setControlChar(Attributes.ControlChar.VSUSP, this.ctrl('Z'));
        if (nativeSignals) {
            for (Terminal.Signal signal : Terminal.Signal.values()) {
                if (signalHandler == Terminal.SignalHandler.SIG_DFL) {
                    this.nativeHandlers.put(signal, Signals.registerDefault(signal.name()));
                    continue;
                }
                this.nativeHandlers.put(signal, Signals.register(signal.name(), () -> this.raise(signal)));
            }
        }
        this.closer = this::close;
        ShutdownHooks.add(this.closer);
        if (TYPE_WINDOWS_CONEMU.equals(this.getType()) && !Boolean.getBoolean("org.jline.terminal.conemu.disable-activate")) {
            writer.write("\u001b[9999E");
            writer.flush();
        }
    }

    @Override
    public Terminal.SignalHandler handle(Terminal.Signal signal, Terminal.SignalHandler handler) {
        Terminal.SignalHandler prev = super.handle(signal, handler);
        if (prev != handler) {
            if (handler == Terminal.SignalHandler.SIG_DFL) {
                Signals.registerDefault(signal.name());
            } else {
                Signals.register(signal.name(), () -> this.raise(signal));
            }
        }
        return prev;
    }

    @Override
    public NonBlockingReader reader() {
        return this.reader;
    }

    @Override
    public PrintWriter writer() {
        return this.writer;
    }

    @Override
    public InputStream input() {
        return this.input;
    }

    @Override
    public OutputStream output() {
        return this.output;
    }

    @Override
    public Attributes getAttributes() {
        int mode = this.getConsoleMode(this.inConsole);
        if ((mode & 4) != 0) {
            this.attributes.setLocalFlag(Attributes.LocalFlag.ECHO, true);
        }
        if ((mode & 2) != 0) {
            this.attributes.setLocalFlag(Attributes.LocalFlag.ICANON, true);
        }
        return new Attributes(this.attributes);
    }

    @Override
    public void setAttributes(Attributes attr) {
        this.attributes.copy(attr);
        this.updateConsoleMode();
    }

    protected void updateConsoleMode() {
        int mode = 8;
        if (this.attributes.getLocalFlag(Attributes.LocalFlag.ISIG)) {
            mode |= 1;
        }
        if (this.attributes.getLocalFlag(Attributes.LocalFlag.ECHO)) {
            mode |= 4;
        }
        if (this.attributes.getLocalFlag(Attributes.LocalFlag.ICANON)) {
            mode |= 2;
        }
        if (this.tracking != Terminal.MouseTracking.Off) {
            mode |= 0x10;
            mode |= 0x80;
        }
        this.setConsoleMode(this.inConsole, mode);
    }

    protected int ctrl(char key) {
        return Character.toUpperCase(key) & 0x1F;
    }

    @Override
    public void setSize(Size size) {
        throw new UnsupportedOperationException("Can not resize windows terminal");
    }

    @Override
    protected void doClose() throws IOException {
        super.doClose();
        this.closing = true;
        if (this.pump != null) {
            this.pump.interrupt();
        }
        ShutdownHooks.remove(this.closer);
        for (Map.Entry<Terminal.Signal, Object> entry : this.nativeHandlers.entrySet()) {
            Signals.unregister(entry.getKey().name(), entry.getValue());
        }
        this.reader.close();
        this.writer.close();
        this.setConsoleMode(this.inConsole, this.originalInConsoleMode);
        this.setConsoleMode(this.outConsole, this.originalOutConsoleMode);
    }

    protected void processKeyEvent(boolean isKeyDown, short virtualKeyCode, char ch, int controlKeyState) throws IOException {
        boolean isShift;
        boolean isCtrl = (controlKeyState & 0xC) > 0;
        boolean isAlt = (controlKeyState & 3) > 0;
        boolean bl = isShift = (controlKeyState & 0x10) > 0;
        if (isKeyDown && ch != '\u0003') {
            if (ch != '\u0000' && (controlKeyState & 0xF) == 9) {
                this.processInputChar(ch);
            } else {
                String keySeq = this.getEscapeSequence(virtualKeyCode, (isCtrl ? 4 : 0) + (isAlt ? 2 : 0) + (isShift ? 1 : 0));
                if (keySeq != null) {
                    for (char c : keySeq.toCharArray()) {
                        this.processInputChar(c);
                    }
                    return;
                }
                if (ch > '\u0000') {
                    if (isAlt) {
                        this.processInputChar('\u001b');
                    }
                    if (isCtrl && ch != '\n' && ch != '\u007f') {
                        this.processInputChar((char)(ch == '?' ? 127 : Character.toUpperCase(ch) & 0x1F));
                    } else {
                        this.processInputChar(ch);
                    }
                } else if (isCtrl) {
                    if (virtualKeyCode >= 65 && virtualKeyCode <= 90) {
                        ch = (char)(virtualKeyCode - 64);
                    } else if (virtualKeyCode == 191) {
                        ch = (char)127;
                    }
                    if (ch > '\u0000') {
                        if (isAlt) {
                            this.processInputChar('\u001b');
                        }
                        this.processInputChar(ch);
                    }
                }
            }
        } else if (isKeyDown && ch == '\u0003') {
            this.processInputChar('\u0003');
        } else if (virtualKeyCode == 18 && ch > '\u0000') {
            this.processInputChar(ch);
        }
    }

    protected String getEscapeSequence(short keyCode, int keyState) {
        String escapeSequence = null;
        switch (keyCode) {
            case 8: {
                escapeSequence = (keyState & 2) > 0 ? "\\E^H" : this.getRawSequence(InfoCmp.Capability.key_backspace);
                break;
            }
            case 9: {
                escapeSequence = (keyState & 1) > 0 ? this.getRawSequence(InfoCmp.Capability.key_btab) : null;
                break;
            }
            case 33: {
                escapeSequence = this.getRawSequence(InfoCmp.Capability.key_ppage);
                break;
            }
            case 34: {
                escapeSequence = this.getRawSequence(InfoCmp.Capability.key_npage);
                break;
            }
            case 35: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dF" : this.getRawSequence(InfoCmp.Capability.key_end);
                break;
            }
            case 36: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dH" : this.getRawSequence(InfoCmp.Capability.key_home);
                break;
            }
            case 37: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dD" : this.getRawSequence(InfoCmp.Capability.key_left);
                break;
            }
            case 38: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dA" : this.getRawSequence(InfoCmp.Capability.key_up);
                break;
            }
            case 39: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dC" : this.getRawSequence(InfoCmp.Capability.key_right);
                break;
            }
            case 40: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dB" : this.getRawSequence(InfoCmp.Capability.key_down);
                break;
            }
            case 45: {
                escapeSequence = this.getRawSequence(InfoCmp.Capability.key_ic);
                break;
            }
            case 46: {
                escapeSequence = this.getRawSequence(InfoCmp.Capability.key_dc);
                break;
            }
            case 112: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dP" : this.getRawSequence(InfoCmp.Capability.key_f1);
                break;
            }
            case 113: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dQ" : this.getRawSequence(InfoCmp.Capability.key_f2);
                break;
            }
            case 114: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dR" : this.getRawSequence(InfoCmp.Capability.key_f3);
                break;
            }
            case 115: {
                escapeSequence = keyState > 0 ? "\\E[1;%p1%dS" : this.getRawSequence(InfoCmp.Capability.key_f4);
                break;
            }
            case 116: {
                escapeSequence = keyState > 0 ? "\\E[15;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f5);
                break;
            }
            case 117: {
                escapeSequence = keyState > 0 ? "\\E[17;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f6);
                break;
            }
            case 118: {
                escapeSequence = keyState > 0 ? "\\E[18;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f7);
                break;
            }
            case 119: {
                escapeSequence = keyState > 0 ? "\\E[19;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f8);
                break;
            }
            case 120: {
                escapeSequence = keyState > 0 ? "\\E[20;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f9);
                break;
            }
            case 121: {
                escapeSequence = keyState > 0 ? "\\E[21;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f10);
                break;
            }
            case 122: {
                escapeSequence = keyState > 0 ? "\\E[23;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f11);
                break;
            }
            case 123: {
                escapeSequence = keyState > 0 ? "\\E[24;%p1%d~" : this.getRawSequence(InfoCmp.Capability.key_f12);
                break;
            }
            default: {
                return null;
            }
        }
        return Curses.tputs(escapeSequence, keyState + 1);
    }

    protected String getRawSequence(InfoCmp.Capability cap) {
        return (String)this.strings.get((Object)cap);
    }

    @Override
    public boolean hasFocusSupport() {
        return true;
    }

    @Override
    public boolean trackFocus(boolean tracking) {
        this.focusTracking = tracking;
        return true;
    }

    @Override
    public abstract int getDefaultForegroundColor();

    @Override
    public abstract int getDefaultBackgroundColor();

    protected int convertAttributeToRgb(int attribute, boolean foreground) {
        int index = 0;
        if (foreground) {
            if ((attribute & 4) != 0) {
                index |= 1;
            }
            if ((attribute & 2) != 0) {
                index |= 2;
            }
            if ((attribute & 1) != 0) {
                index |= 4;
            }
            if ((attribute & 8) != 0) {
                index |= 8;
            }
        } else {
            if ((attribute & 0x40) != 0) {
                index |= 1;
            }
            if ((attribute & 0x20) != 0) {
                index |= 2;
            }
            if ((attribute & 0x10) != 0) {
                index |= 4;
            }
            if ((attribute & 0x80) != 0) {
                index |= 8;
            }
        }
        return ANSI_COLORS[index];
    }

    @Override
    public boolean canPauseResume() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void pause() {
        Object object = this.lock;
        synchronized (object) {
            this.paused = true;
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
            p = this.pump;
        }
        if (p != null) {
            p.interrupt();
            p.join();
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
            if (this.pump == null) {
                this.pump = new Thread(this::pump, "WindowsStreamPump");
                this.pump.setDaemon(true);
                this.pump.start();
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
     */
    protected void pump() {
        try {
            while (!this.closing) {
                Object object = this.lock;
                synchronized (object) {
                    if (this.paused) {
                        this.pump = null;
                        break;
                    }
                }
                if (!this.processConsoleInput()) continue;
                this.slaveInputPipe.flush();
            }
        }
        catch (IOException e) {
            if (!this.closing) {
                Log.warn("Error in WindowsStreamPump", e);
                try {
                    this.close();
                }
                catch (IOException e1) {
                    Log.warn("Error closing terminal", e);
                }
            }
        }
        finally {
            Object object = this.lock;
            synchronized (object) {
                this.pump = null;
            }
        }
    }

    public void processInputChar(char c) throws IOException {
        if (this.attributes.getLocalFlag(Attributes.LocalFlag.ISIG)) {
            if (c == this.attributes.getControlChar(Attributes.ControlChar.VINTR)) {
                this.raise(Terminal.Signal.INT);
                return;
            }
            if (c == this.attributes.getControlChar(Attributes.ControlChar.VQUIT)) {
                this.raise(Terminal.Signal.QUIT);
                return;
            }
            if (c == this.attributes.getControlChar(Attributes.ControlChar.VSUSP)) {
                this.raise(Terminal.Signal.TSTP);
                return;
            }
            if (c == this.attributes.getControlChar(Attributes.ControlChar.VSTATUS)) {
                this.raise(Terminal.Signal.INFO);
            }
        }
        if (this.attributes.getInputFlag(Attributes.InputFlag.INORMEOL)) {
            if (c == '\r') {
                this.skipNextLf = true;
                c = (char)10;
            } else if (c == '\n') {
                if (this.skipNextLf) {
                    this.skipNextLf = false;
                    return;
                }
            } else {
                this.skipNextLf = false;
            }
        } else if (c == '\r') {
            if (this.attributes.getInputFlag(Attributes.InputFlag.IGNCR)) {
                return;
            }
            if (this.attributes.getInputFlag(Attributes.InputFlag.ICRNL)) {
                c = (char)10;
            }
        } else if (c == '\n' && this.attributes.getInputFlag(Attributes.InputFlag.INLCR)) {
            c = (char)13;
        }
        this.slaveInputPipe.write(c);
    }

    @Override
    public boolean trackMouse(Terminal.MouseTracking tracking) {
        this.tracking = tracking;
        this.updateConsoleMode();
        return true;
    }

    protected abstract int getConsoleMode(Console var1);

    protected abstract void setConsoleMode(Console var1, int var2);

    protected abstract boolean processConsoleInput() throws IOException;

    @Override
    public TerminalProvider getProvider() {
        return this.provider;
    }

    @Override
    public SystemStream getSystemStream() {
        return this.systemStream;
    }
}

