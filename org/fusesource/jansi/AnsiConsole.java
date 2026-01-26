/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiPrintStream;
import org.fusesource.jansi.AnsiType;
import org.fusesource.jansi.internal.CLibrary;
import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.MingwSupport;
import org.fusesource.jansi.io.AnsiOutputStream;
import org.fusesource.jansi.io.AnsiProcessor;
import org.fusesource.jansi.io.FastBufferedOutputStream;
import org.fusesource.jansi.io.WindowsAnsiProcessor;

public class AnsiConsole {
    public static final String JANSI_MODE = "jansi.mode";
    public static final String JANSI_OUT_MODE = "jansi.out.mode";
    public static final String JANSI_ERR_MODE = "jansi.err.mode";
    public static final String JANSI_MODE_STRIP = "strip";
    public static final String JANSI_MODE_FORCE = "force";
    public static final String JANSI_MODE_DEFAULT = "default";
    public static final String JANSI_COLORS = "jansi.colors";
    public static final String JANSI_OUT_COLORS = "jansi.out.colors";
    public static final String JANSI_ERR_COLORS = "jansi.err.colors";
    public static final String JANSI_COLORS_16 = "16";
    public static final String JANSI_COLORS_256 = "256";
    public static final String JANSI_COLORS_TRUECOLOR = "truecolor";
    @Deprecated
    public static final String JANSI_PASSTHROUGH = "jansi.passthrough";
    @Deprecated
    public static final String JANSI_STRIP = "jansi.strip";
    @Deprecated
    public static final String JANSI_FORCE = "jansi.force";
    @Deprecated
    public static final String JANSI_EAGER = "jansi.eager";
    public static final String JANSI_NORESET = "jansi.noreset";
    public static final String JANSI_GRACEFUL = "jansi.graceful";
    @Deprecated
    public static PrintStream system_out = System.out;
    @Deprecated
    public static PrintStream out;
    @Deprecated
    public static PrintStream system_err;
    @Deprecated
    public static PrintStream err;
    static final boolean IS_WINDOWS;
    static final boolean IS_CYGWIN;
    static final boolean IS_MSYSTEM;
    static final boolean IS_CONEMU;
    static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
    static int STDOUT_FILENO;
    static int STDERR_FILENO;
    private static boolean initialized;
    private static int installed;
    private static int virtualProcessing;

    public static int getTerminalWidth() {
        int w = AnsiConsole.out().getTerminalWidth();
        if (w <= 0) {
            w = AnsiConsole.err().getTerminalWidth();
        }
        return w;
    }

    private AnsiConsole() {
    }

    private static AnsiPrintStream ansiStream(boolean stdout) {
        String term;
        String colorterm;
        AnsiOutputStream.WidthSupplier width;
        AnsiOutputStream.IoRunnable installer;
        AnsiOutputStream.IoRunnable uninstaller;
        AnsiType type;
        AnsiProcessor processor;
        boolean withException;
        boolean isAtty;
        FileDescriptor descriptor = stdout ? FileDescriptor.out : FileDescriptor.err;
        FastBufferedOutputStream out = new FastBufferedOutputStream(new FileOutputStream(descriptor));
        String enc = System.getProperty(stdout ? "stdout.encoding" : "stderr.encoding");
        if (enc == null) {
            enc = System.getProperty(stdout ? "sun.stdout.encoding" : "sun.stderr.encoding");
        }
        final int fd = stdout ? STDOUT_FILENO : STDERR_FILENO;
        try {
            isAtty = CLibrary.isatty(fd) != 0;
            String term2 = System.getenv("TERM");
            String emacs = System.getenv("INSIDE_EMACS");
            if (isAtty && "dumb".equals(term2) && emacs != null && !emacs.contains("comint")) {
                isAtty = false;
            }
            withException = false;
        }
        catch (Throwable ignore) {
            isAtty = false;
            withException = true;
        }
        boolean isatty = isAtty;
        if (!isatty) {
            processor = null;
            type = withException ? AnsiType.Unsupported : AnsiType.Redirected;
            uninstaller = null;
            installer = null;
            width = new AnsiOutputStream.ZeroWidthSupplier();
        } else if (IS_WINDOWS) {
            final long console = Kernel32.GetStdHandle(stdout ? Kernel32.STD_OUTPUT_HANDLE : Kernel32.STD_ERROR_HANDLE);
            int[] mode = new int[1];
            boolean isConsole = Kernel32.GetConsoleMode(console, mode) != 0;
            AnsiOutputStream.WidthSupplier kernel32Width = new AnsiOutputStream.WidthSupplier(){

                @Override
                public int getTerminalWidth() {
                    Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
                    Kernel32.GetConsoleScreenBufferInfo(console, info);
                    return info.windowWidth();
                }
            };
            if (isConsole && Kernel32.SetConsoleMode(console, mode[0] | 4) != 0) {
                Kernel32.SetConsoleMode(console, mode[0]);
                processor = null;
                type = AnsiType.VirtualTerminal;
                installer = () -> {
                    Class<AnsiConsole> clazz = AnsiConsole.class;
                    synchronized (AnsiConsole.class) {
                        ++virtualProcessing;
                        Kernel32.SetConsoleMode(console, mode[0] | 4);
                        // ** MonitorExit[var3_2] (shouldn't be in output)
                        return;
                    }
                };
                uninstaller = () -> {
                    Class<AnsiConsole> clazz = AnsiConsole.class;
                    synchronized (AnsiConsole.class) {
                        if (--virtualProcessing == 0) {
                            Kernel32.SetConsoleMode(console, mode[0]);
                        }
                        // ** MonitorExit[var3_2] (shouldn't be in output)
                        return;
                    }
                };
                width = kernel32Width;
            } else if ((IS_CONEMU || IS_CYGWIN || IS_MSYSTEM) && !isConsole) {
                processor = null;
                type = AnsiType.Native;
                uninstaller = null;
                installer = null;
                MingwSupport mingw = new MingwSupport();
                String name = mingw.getConsoleName(stdout);
                width = name != null && !name.isEmpty() ? () -> mingw.getTerminalWidth(name) : () -> -1;
            } else {
                AnsiType ttype;
                AnsiProcessor proc;
                try {
                    proc = new WindowsAnsiProcessor((OutputStream)out, console);
                    ttype = AnsiType.Emulation;
                }
                catch (Throwable ignore) {
                    proc = new AnsiProcessor(out);
                    ttype = AnsiType.Unsupported;
                }
                processor = proc;
                type = ttype;
                uninstaller = null;
                installer = null;
                width = kernel32Width;
            }
        } else {
            processor = null;
            type = AnsiType.Native;
            uninstaller = null;
            installer = null;
            width = new AnsiOutputStream.WidthSupplier(){

                @Override
                public int getTerminalWidth() {
                    CLibrary.WinSize sz = new CLibrary.WinSize();
                    CLibrary.ioctl(fd, CLibrary.TIOCGWINSZ, sz);
                    return sz.ws_col;
                }
            };
        }
        String jansiMode = System.getProperty(stdout ? JANSI_OUT_MODE : JANSI_ERR_MODE, System.getProperty(JANSI_MODE));
        AnsiMode mode = JANSI_MODE_FORCE.equals(jansiMode) ? AnsiMode.Force : (JANSI_MODE_STRIP.equals(jansiMode) ? AnsiMode.Strip : (jansiMode != null ? (isatty ? AnsiMode.Default : AnsiMode.Strip) : (AnsiConsole.getBoolean(JANSI_PASSTHROUGH) ? AnsiMode.Force : (AnsiConsole.getBoolean(JANSI_STRIP) ? AnsiMode.Strip : (AnsiConsole.getBoolean(JANSI_FORCE) ? AnsiMode.Force : (isatty ? AnsiMode.Default : AnsiMode.Strip))))));
        String jansiColors = System.getProperty(stdout ? JANSI_OUT_COLORS : JANSI_ERR_COLORS, System.getProperty(JANSI_COLORS));
        AnsiColors colors = JANSI_COLORS_TRUECOLOR.equals(jansiColors) ? AnsiColors.TrueColor : (JANSI_COLORS_256.equals(jansiColors) ? AnsiColors.Colors256 : (jansiColors != null ? AnsiColors.Colors16 : ((colorterm = System.getenv("COLORTERM")) != null && (colorterm.contains(JANSI_COLORS_TRUECOLOR) || colorterm.contains("24bit")) ? AnsiColors.TrueColor : ((term = System.getenv("TERM")) != null && term.contains("-direct") ? AnsiColors.TrueColor : (term != null && term.contains("-256color") ? AnsiColors.Colors256 : AnsiColors.Colors16)))));
        boolean resetAtUninstall = type != AnsiType.Unsupported && !AnsiConsole.getBoolean(JANSI_NORESET);
        Charset cs = Charset.defaultCharset();
        if (enc != null) {
            try {
                cs = Charset.forName(enc);
            }
            catch (UnsupportedCharsetException unsupportedCharsetException) {
                // empty catch block
            }
        }
        return AnsiConsole.newPrintStream(new AnsiOutputStream(out, width, mode, processor, type, colors, cs, installer, uninstaller, resetAtUninstall), cs.name());
    }

    private static AnsiPrintStream newPrintStream(AnsiOutputStream out, String enc) {
        if (enc != null) {
            try {
                return new AnsiPrintStream(out, true, enc);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        return new AnsiPrintStream(out, true);
    }

    static boolean getBoolean(String name) {
        boolean result = false;
        try {
            String val = System.getProperty(name);
            result = val.isEmpty() || Boolean.parseBoolean(val);
        }
        catch (IllegalArgumentException | NullPointerException runtimeException) {
            // empty catch block
        }
        return result;
    }

    public static AnsiPrintStream out() {
        AnsiConsole.initStreams();
        return (AnsiPrintStream)out;
    }

    public static PrintStream sysOut() {
        return system_out;
    }

    public static AnsiPrintStream err() {
        AnsiConsole.initStreams();
        return (AnsiPrintStream)err;
    }

    public static PrintStream sysErr() {
        return system_err;
    }

    public static synchronized void systemInstall() {
        if (installed == 0) {
            AnsiConsole.initStreams();
            try {
                ((AnsiPrintStream)out).install();
                ((AnsiPrintStream)err).install();
            }
            catch (IOException e) {
                throw new IOError(e);
            }
            System.setOut(out);
            System.setErr(err);
        }
        ++installed;
    }

    public static synchronized boolean isInstalled() {
        return installed > 0;
    }

    public static synchronized void systemUninstall() {
        if (--installed == 0) {
            try {
                ((AnsiPrintStream)out).uninstall();
                ((AnsiPrintStream)err).uninstall();
            }
            catch (IOException e) {
                throw new IOError(e);
            }
            initialized = false;
            System.setOut(system_out);
            System.setErr(system_err);
        }
    }

    static synchronized void initStreams() {
        if (!initialized) {
            out = AnsiConsole.ansiStream(true);
            err = AnsiConsole.ansiStream(false);
            initialized = true;
        }
    }

    static {
        system_err = System.err;
        IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
        IS_CYGWIN = IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/");
        IS_MSYSTEM = IS_WINDOWS && System.getenv("MSYSTEM") != null && (System.getenv("MSYSTEM").startsWith("MINGW") || System.getenv("MSYSTEM").equals("MSYS"));
        IS_CONEMU = IS_WINDOWS && System.getenv("ConEmuPID") != null;
        STDOUT_FILENO = 1;
        STDERR_FILENO = 2;
        if (AnsiConsole.getBoolean(JANSI_EAGER)) {
            AnsiConsole.initStreams();
        }
    }
}

