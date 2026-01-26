/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractPosixTerminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.OSUtils;

public class Diag {
    private final PrintStream out;
    private final boolean verbose;

    public static void main(String[] args) {
        Diag.diag(System.out, Arrays.asList(args).contains("--verbose"));
    }

    public static void diag(PrintStream out) {
        Diag.diag(out, true);
    }

    public static void diag(PrintStream out, boolean verbose) {
        new Diag(out, verbose).run();
    }

    public Diag(PrintStream out, boolean verbose) {
        this.out = out;
        this.verbose = verbose;
    }

    public void run() {
        TerminalProvider provider;
        this.out.println("System properties");
        this.out.println("=================");
        this.out.println("os.name =         " + System.getProperty("os.name"));
        this.out.println("OSTYPE =          " + System.getenv("OSTYPE"));
        this.out.println("MSYSTEM =         " + System.getenv("MSYSTEM"));
        this.out.println("PWD =             " + System.getenv("PWD"));
        this.out.println("ConEmuPID =       " + System.getenv("ConEmuPID"));
        this.out.println("WSL_DISTRO_NAME = " + System.getenv("WSL_DISTRO_NAME"));
        this.out.println("WSL_INTEROP =     " + System.getenv("WSL_INTEROP"));
        this.out.println();
        this.out.println("OSUtils");
        this.out.println("=================");
        this.out.println("IS_WINDOWS = " + OSUtils.IS_WINDOWS);
        this.out.println("IS_CYGWIN =  " + OSUtils.IS_CYGWIN);
        this.out.println("IS_MSYSTEM = " + OSUtils.IS_MSYSTEM);
        this.out.println("IS_WSL =     " + OSUtils.IS_WSL);
        this.out.println("IS_WSL1 =    " + OSUtils.IS_WSL1);
        this.out.println("IS_WSL2 =    " + OSUtils.IS_WSL2);
        this.out.println("IS_CONEMU =  " + OSUtils.IS_CONEMU);
        this.out.println("IS_OSX =     " + OSUtils.IS_OSX);
        this.out.println();
        this.out.println("FFM Support");
        this.out.println("=================");
        try {
            provider = TerminalProvider.load("ffm");
            this.testProvider(provider);
        }
        catch (Throwable t) {
            this.error("FFM support not available", t);
        }
        this.out.println();
        this.out.println("JnaSupport");
        this.out.println("=================");
        try {
            provider = TerminalProvider.load("jna");
            this.testProvider(provider);
        }
        catch (Throwable t) {
            this.error("JNA support not available", t);
        }
        this.out.println();
        this.out.println("Jansi2Support");
        this.out.println("=================");
        try {
            provider = TerminalProvider.load("jansi");
            this.testProvider(provider);
        }
        catch (Throwable t) {
            this.error("Jansi 2 support not available", t);
        }
        this.out.println();
        this.out.println("JniSupport");
        this.out.println("=================");
        try {
            provider = TerminalProvider.load("jni");
            this.testProvider(provider);
        }
        catch (Throwable t) {
            this.error("JNI support not available", t);
        }
        this.out.println();
        this.out.println("Exec Support");
        this.out.println("=================");
        try {
            provider = TerminalProvider.load("exec");
            this.testProvider(provider);
        }
        catch (Throwable t) {
            this.error("Exec support not available", t);
        }
        if (!this.verbose) {
            this.out.println();
            this.out.println("Run with --verbose argument to print stack traces");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void testProvider(TerminalProvider provider) {
        block23: {
            try {
                this.out.println("StdIn stream =    " + provider.isSystemStream(SystemStream.Input));
                this.out.println("StdOut stream =   " + provider.isSystemStream(SystemStream.Output));
                this.out.println("StdErr stream =   " + provider.isSystemStream(SystemStream.Error));
            }
            catch (Throwable t) {
                this.error("Unable to check stream", t);
            }
            try {
                this.out.println("StdIn stream name =     " + provider.systemStreamName(SystemStream.Input));
                this.out.println("StdOut stream name =    " + provider.systemStreamName(SystemStream.Output));
                this.out.println("StdErr stream name =    " + provider.systemStreamName(SystemStream.Error));
            }
            catch (Throwable t) {
                this.error("Unable to check stream names", t);
            }
            try (Terminal terminal = provider.sysTerminal("diag", "xterm", false, StandardCharsets.UTF_8, StandardCharsets.UTF_8, StandardCharsets.UTF_8, false, Terminal.SignalHandler.SIG_DFL, false, SystemStream.Output);){
                if (terminal != null) {
                    Attributes attr = terminal.enterRawMode();
                    try {
                        this.out.println("Terminal size: " + terminal.getSize());
                        ForkJoinPool forkJoinPool = new ForkJoinPool(1);
                        try {
                            Future t = forkJoinPool.submit(() -> terminal.reader().read(1L));
                            ((ForkJoinTask)t).get(1000L, TimeUnit.MILLISECONDS);
                        }
                        finally {
                            forkJoinPool.shutdown();
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append("The terminal seems to work: ");
                        sb.append("terminal ").append(terminal.getClass().getName());
                        if (terminal instanceof AbstractPosixTerminal) {
                            sb.append(" with pty ").append(((AbstractPosixTerminal)terminal).getPty().getClass().getName());
                        }
                        this.out.println(sb);
                        terminal.setAttributes(attr);
                        break block23;
                    }
                    catch (Throwable t2) {
                        try {
                            this.error("Unable to read from terminal", t2);
                            break block23;
                        }
                        catch (Throwable throwable) {
                            throw throwable;
                        }
                        finally {
                            terminal.setAttributes(attr);
                        }
                    }
                }
                this.out.println("Not supported by provider");
            }
            catch (Throwable t) {
                this.error("Unable to open terminal", t);
            }
        }
    }

    private void error(String message, Throwable cause) {
        if (this.verbose) {
            this.out.println(message);
            cause.printStackTrace(this.out);
        } else {
            this.out.println(message + ": " + cause);
        }
    }
}

