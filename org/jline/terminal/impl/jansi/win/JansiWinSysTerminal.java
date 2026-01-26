/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl.jansi.win;

import java.io.BufferedWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.IntConsumer;
import org.fusesource.jansi.internal.Kernel32;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractWindowsTerminal;
import org.jline.terminal.impl.jansi.win.JansiWinConsoleWriter;
import org.jline.terminal.impl.jansi.win.WindowsAnsiWriter;
import org.jline.terminal.impl.jansi.win.WindowsSupport;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.InfoCmp;
import org.jline.utils.OSUtils;

public class JansiWinSysTerminal
extends AbstractWindowsTerminal<Long> {
    private static final long consoleIn = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
    private static final long consoleOut = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
    private static final long consoleErr = Kernel32.GetStdHandle(Kernel32.STD_ERROR_HANDLE);
    private char[] focus = new char[]{'\u001b', '[', ' '};
    private char[] mouse = new char[]{'\u001b', '[', 'M', ' ', ' ', ' '};

    public static JansiWinSysTerminal createTerminal(TerminalProvider provider, SystemStream systemStream, String name, String type, boolean ansiPassThrough, Charset encoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused) throws IOException {
        return JansiWinSysTerminal.createTerminal(provider, systemStream, name, type, ansiPassThrough, encoding, encoding, encoding, encoding, nativeSignals, signalHandler, paused);
    }

    public static JansiWinSysTerminal createTerminal(TerminalProvider provider, SystemStream systemStream, String name, String type, boolean ansiPassThrough, Charset encoding, Charset stdinEncoding, Charset stdoutEncoding, Charset stderrEncoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused) throws IOException {
        Writer writer;
        int[] outMode;
        int[] inMode = new int[1];
        if (Kernel32.GetConsoleMode(consoleIn, inMode) == 0) {
            throw new IOException("Failed to get console mode: " + WindowsSupport.getLastErrorMessage());
        }
        long console = JansiWinSysTerminal.getConsole(systemStream);
        if (Kernel32.GetConsoleMode(console, outMode = new int[1]) == 0) {
            throw new IOException("Failed to get console mode: " + WindowsSupport.getLastErrorMessage());
        }
        if (ansiPassThrough) {
            type = type != null ? type : (OSUtils.IS_CONEMU ? "windows-conemu" : "windows");
            writer = JansiWinSysTerminal.newConsoleWriter(console);
        } else if (JansiWinSysTerminal.enableVtp(console, outMode[0])) {
            type = type != null ? type : "windows-vtp";
            writer = JansiWinSysTerminal.newConsoleWriter(console);
        } else if (OSUtils.IS_CONEMU) {
            type = type != null ? type : "windows-conemu";
            writer = JansiWinSysTerminal.newConsoleWriter(console);
        } else {
            type = type != null ? type : "windows";
            writer = new WindowsAnsiWriter(new BufferedWriter(JansiWinSysTerminal.newConsoleWriter(console)));
        }
        Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
        JansiWinSysTerminal terminal = new JansiWinSysTerminal(provider, systemStream, writer, name, type, encoding, stdinEncoding, outputEncoding, nativeSignals, signalHandler, consoleIn, inMode[0], console, outMode[0]);
        if (!paused) {
            terminal.resume();
        }
        return terminal;
    }

    public static long getConsole(SystemStream systemStream) {
        long console;
        switch (systemStream) {
            case Output: {
                console = consoleOut;
                break;
            }
            case Error: {
                console = consoleErr;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported stream for console: " + (Object)((Object)systemStream));
            }
        }
        return console;
    }

    private static boolean enableVtp(long console, int outMode) {
        return Kernel32.SetConsoleMode(console, outMode | 4) != 0;
    }

    private static Writer newConsoleWriter(long console) {
        return new JansiWinConsoleWriter(console);
    }

    public static boolean isWindowsSystemStream(SystemStream stream) {
        long console;
        int[] mode = new int[1];
        switch (stream) {
            case Input: {
                console = consoleIn;
                break;
            }
            case Output: {
                console = consoleOut;
                break;
            }
            case Error: {
                console = consoleErr;
                break;
            }
            default: {
                return false;
            }
        }
        return Kernel32.GetConsoleMode(console, mode) != 0;
    }

    JansiWinSysTerminal(TerminalProvider provider, SystemStream systemStream, Writer writer, String name, String type, Charset encoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, long inConsole, int inMode, long outConsole, int outMode) throws IOException {
        this(provider, systemStream, writer, name, type, encoding, encoding, encoding, nativeSignals, signalHandler, inConsole, inMode, outConsole, outMode);
    }

    JansiWinSysTerminal(TerminalProvider provider, SystemStream systemStream, Writer writer, String name, String type, Charset encoding, Charset inputEncoding, Charset outputEncoding, boolean nativeSignals, Terminal.SignalHandler signalHandler, long inConsole, int inMode, long outConsole, int outMode) throws IOException {
        super(provider, systemStream, writer, name, type, encoding, inputEncoding, outputEncoding, nativeSignals, signalHandler, inConsole, inMode, outConsole, outMode);
    }

    @Override
    protected int getConsoleMode(Long console) {
        int[] mode = new int[1];
        if (Kernel32.GetConsoleMode(console, mode) == 0) {
            return -1;
        }
        return mode[0];
    }

    @Override
    protected void setConsoleMode(Long console, int mode) {
        Kernel32.SetConsoleMode(console, mode);
    }

    @Override
    public Size getSize() {
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
        Kernel32.GetConsoleScreenBufferInfo((Long)this.outConsole, info);
        return new Size(info.windowWidth(), info.windowHeight());
    }

    @Override
    public Size getBufferSize() {
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
        Kernel32.GetConsoleScreenBufferInfo((Long)this.outConsole, info);
        return new Size(info.size.x, info.size.y);
    }

    @Override
    protected boolean processConsoleInput() throws IOException {
        if ((Long)this.inConsole == (long)Kernel32.INVALID_HANDLE_VALUE || Kernel32.WaitForSingleObject((Long)this.inConsole, 100) != 0) {
            return false;
        }
        Kernel32.INPUT_RECORD[] events = Kernel32.readConsoleInputHelper((Long)this.inConsole, 1, false);
        boolean flush = false;
        for (Kernel32.INPUT_RECORD event : events) {
            if (event.eventType == Kernel32.INPUT_RECORD.KEY_EVENT) {
                Kernel32.KEY_EVENT_RECORD keyEvent = event.keyEvent;
                this.processKeyEvent(keyEvent.keyDown, keyEvent.keyCode, keyEvent.uchar, keyEvent.controlKeyState);
                flush = true;
                continue;
            }
            if (event.eventType == Kernel32.INPUT_RECORD.WINDOW_BUFFER_SIZE_EVENT) {
                this.raise(Terminal.Signal.WINCH);
                continue;
            }
            if (event.eventType == Kernel32.INPUT_RECORD.MOUSE_EVENT) {
                this.processMouseEvent(event.mouseEvent);
                flush = true;
                continue;
            }
            if (event.eventType != Kernel32.INPUT_RECORD.FOCUS_EVENT) continue;
            this.processFocusEvent(event.focusEvent.setFocus);
        }
        return flush;
    }

    private void processFocusEvent(boolean hasFocus) throws IOException {
        if (this.focusTracking) {
            this.focus[2] = hasFocus ? 73 : 79;
            this.slaveInputPipe.write(this.focus);
        }
    }

    private void processMouseEvent(Kernel32.MOUSE_EVENT_RECORD mouseEvent) throws IOException {
        int dwEventFlags = mouseEvent.eventFlags;
        int dwButtonState = mouseEvent.buttonState;
        if (this.tracking == Terminal.MouseTracking.Off || this.tracking == Terminal.MouseTracking.Normal && dwEventFlags == Kernel32.MOUSE_EVENT_RECORD.MOUSE_MOVED || this.tracking == Terminal.MouseTracking.Button && dwEventFlags == Kernel32.MOUSE_EVENT_RECORD.MOUSE_MOVED && dwButtonState == 0) {
            return;
        }
        int cb = 0;
        if ((dwEventFlags &= ~Kernel32.MOUSE_EVENT_RECORD.DOUBLE_CLICK) == Kernel32.MOUSE_EVENT_RECORD.MOUSE_WHEELED) {
            cb |= 0x40;
            if (dwButtonState >> 16 < 0) {
                cb |= 1;
            }
        } else {
            if (dwEventFlags == Kernel32.MOUSE_EVENT_RECORD.MOUSE_HWHEELED) {
                return;
            }
            cb = (dwButtonState & Kernel32.MOUSE_EVENT_RECORD.FROM_LEFT_1ST_BUTTON_PRESSED) != 0 ? (cb |= 0) : ((dwButtonState & Kernel32.MOUSE_EVENT_RECORD.RIGHTMOST_BUTTON_PRESSED) != 0 ? (cb |= 1) : ((dwButtonState & Kernel32.MOUSE_EVENT_RECORD.FROM_LEFT_2ND_BUTTON_PRESSED) != 0 ? (cb |= 2) : (cb |= 3)));
        }
        short cx = mouseEvent.mousePosition.x;
        short cy = mouseEvent.mousePosition.y;
        this.mouse[3] = (char)(32 + cb);
        this.mouse[4] = (char)(32 + cx + 1);
        this.mouse[5] = (char)(32 + cy + 1);
        this.slaveInputPipe.write(this.mouse);
    }

    @Override
    public Cursor getCursorPosition(IntConsumer discarded) {
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
        if (Kernel32.GetConsoleScreenBufferInfo((Long)this.outConsole, info) == 0) {
            throw new IOError(new IOException("Could not get the cursor position: " + WindowsSupport.getLastErrorMessage()));
        }
        return new Cursor(info.cursorPosition.x, info.cursorPosition.y);
    }

    public void disableScrolling() {
        this.strings.remove((Object)InfoCmp.Capability.insert_line);
        this.strings.remove((Object)InfoCmp.Capability.parm_insert_line);
        this.strings.remove((Object)InfoCmp.Capability.delete_line);
        this.strings.remove((Object)InfoCmp.Capability.parm_delete_line);
    }

    @Override
    public int getDefaultForegroundColor() {
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
        if (Kernel32.GetConsoleScreenBufferInfo((Long)this.outConsole, info) == 0) {
            return -1;
        }
        return this.convertAttributeToRgb(info.attributes & 0xF, true);
    }

    @Override
    public int getDefaultBackgroundColor() {
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
        if (Kernel32.GetConsoleScreenBufferInfo((Long)this.outConsole, info) == 0) {
            return -1;
        }
        return this.convertAttributeToRgb((info.attributes & 0xF0) >> 4, false);
    }
}

