/*
 * Decompiled with CFR 0.152.
 */
package org.jline.jansi;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.jline.jansi.AnsiColors;
import org.jline.jansi.AnsiMode;
import org.jline.jansi.AnsiPrintStream;
import org.jline.jansi.AnsiType;
import org.jline.jansi.io.AnsiOutputStream;
import org.jline.jansi.io.AnsiProcessor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.impl.DumbTerminal;
import org.jline.terminal.spi.TerminalExt;
import org.jline.utils.OSUtils;

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
    public static final String JANSI_NORESET = "jansi.noreset";
    public static final String JANSI_GRACEFUL = "jansi.graceful";
    public static final String JANSI_PROVIDERS = "jansi.providers";
    public static final String JANSI_PROVIDER_JNI = "jni";
    public static final String JANSI_PROVIDER_FFM = "ffm";
    public static final String JANSI_PROVIDER_NATIVE_IMAGE = "native-image";
    private static final PrintStream system_out = System.out;
    private static PrintStream out;
    private static final PrintStream system_err;
    private static PrintStream err;
    static final boolean IS_WINDOWS;
    static final boolean IS_CYGWIN;
    static final boolean IS_MSYSTEM;
    static final boolean IS_CONEMU;
    static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
    static int STDOUT_FILENO;
    static int STDERR_FILENO;
    private static int installed;
    static Terminal terminal;

    public static int getTerminalWidth() {
        int w = AnsiConsole.out().getTerminalWidth();
        if (w <= 0) {
            w = AnsiConsole.err().getTerminalWidth();
        }
        return w;
    }

    private AnsiConsole() {
    }

    public static Terminal getTerminal() {
        return terminal;
    }

    public static void setTerminal(Terminal terminal) {
        AnsiConsole.terminal = terminal;
    }

    static synchronized void doInstall() {
        try {
            if (terminal == null) {
                TerminalBuilder builder = TerminalBuilder.builder().system(true).name("jansi").providers(System.getProperty(JANSI_PROVIDERS));
                String graceful = System.getProperty(JANSI_GRACEFUL);
                if (graceful != null) {
                    builder.dumb(Boolean.parseBoolean(graceful));
                }
                terminal = builder.build();
            }
            if (out == null) {
                out = AnsiConsole.ansiStream(true);
                err = AnsiConsole.ansiStream(false);
            }
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    static synchronized void doUninstall() {
        try {
            if (terminal != null) {
                terminal.close();
            }
        }
        catch (IOException e) {
            throw new IOError(e);
        }
        finally {
            terminal = null;
            out = null;
            err = null;
        }
    }

    private static AnsiPrintStream ansiStream(boolean stdout) throws IOException {
        String term;
        String colorterm;
        AnsiProcessor processor = null;
        AnsiOutputStream.IoRunnable installer = null;
        AnsiOutputStream.IoRunnable uninstaller = null;
        OutputStream out = terminal.output();
        AnsiOutputStream.WidthSupplier width = terminal::getWidth;
        AnsiType type = terminal instanceof DumbTerminal ? AnsiType.Unsupported : (((TerminalExt)terminal).getSystemStream() != null ? AnsiType.Native : AnsiType.Redirected);
        String jansiMode = System.getProperty(stdout ? JANSI_OUT_MODE : JANSI_ERR_MODE, System.getProperty(JANSI_MODE));
        AnsiMode mode = JANSI_MODE_FORCE.equals(jansiMode) ? AnsiMode.Force : (JANSI_MODE_STRIP.equals(jansiMode) ? AnsiMode.Strip : (type == AnsiType.Native ? AnsiMode.Default : AnsiMode.Strip));
        String jansiColors = System.getProperty(stdout ? JANSI_OUT_COLORS : JANSI_ERR_COLORS, System.getProperty(JANSI_COLORS));
        AnsiColors colors = JANSI_COLORS_TRUECOLOR.equals(jansiColors) ? AnsiColors.TrueColor : (JANSI_COLORS_256.equals(jansiColors) ? AnsiColors.Colors256 : (jansiColors != null ? AnsiColors.Colors16 : ((colorterm = System.getenv("COLORTERM")) != null && (colorterm.contains(JANSI_COLORS_TRUECOLOR) || colorterm.contains("24bit")) ? AnsiColors.TrueColor : ((term = System.getenv("TERM")) != null && term.contains("-direct") ? AnsiColors.TrueColor : (term != null && term.contains("-256color") ? AnsiColors.Colors256 : AnsiColors.Colors16)))));
        boolean resetAtUninstall = type != AnsiType.Unsupported && !AnsiConsole.getBoolean(JANSI_NORESET);
        return AnsiConsole.newPrintStream(new AnsiOutputStream(out, width, mode, processor, type, colors, terminal.encoding(), installer, uninstaller, resetAtUninstall), terminal.encoding().name());
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
        AnsiConsole.doInstall();
        return (AnsiPrintStream)out;
    }

    public static PrintStream sysOut() {
        return system_out;
    }

    public static AnsiPrintStream err() {
        AnsiConsole.doInstall();
        return (AnsiPrintStream)err;
    }

    public static PrintStream sysErr() {
        return system_err;
    }

    public static synchronized void systemInstall() {
        if (installed == 0) {
            AnsiConsole.doInstall();
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
            AnsiConsole.doUninstall();
            System.setOut(system_out);
            System.setErr(system_err);
        }
    }

    static {
        system_err = System.err;
        IS_WINDOWS = OSUtils.IS_WINDOWS;
        IS_CYGWIN = IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/");
        IS_MSYSTEM = IS_WINDOWS && System.getenv("MSYSTEM") != null && (System.getenv("MSYSTEM").startsWith("MINGW") || System.getenv("MSYSTEM").equals("MSYS"));
        IS_CONEMU = IS_WINDOWS && System.getenv("ConEmuPID") != null;
        STDOUT_FILENO = 1;
        STDERR_FILENO = 2;
    }
}

