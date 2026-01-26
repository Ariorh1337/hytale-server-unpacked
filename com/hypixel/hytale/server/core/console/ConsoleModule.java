/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.console;

import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.backend.HytaleConsole;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.console.command.SayCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import java.io.IOError;
import java.io.IOException;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class ConsoleModule
extends JavaPlugin {
    public static final PluginManifest MANIFEST = PluginManifest.corePlugin(ConsoleModule.class).build();
    private static ConsoleModule instance;
    private Terminal terminal;
    private ConsoleRunnable consoleRunnable;

    public static ConsoleModule get() {
        return instance;
    }

    public ConsoleModule(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        this.getCommandRegistry().registerCommand(new SayCommand());
        try {
            TerminalBuilder builder = TerminalBuilder.builder();
            if (Constants.SINGLEPLAYER) {
                builder.dumb(true);
            } else {
                builder.color(true);
            }
            this.terminal = builder.build();
            HytaleConsole.INSTANCE.setTerminal(this.terminal.getType());
            LineReader lineReader = LineReaderBuilder.builder().terminal(this.terminal).build();
            this.consoleRunnable = new ConsoleRunnable(lineReader, ConsoleSender.INSTANCE);
            this.getLogger().at(Level.INFO).log("Setup console with type: %s", this.terminal.getType());
        }
        catch (IOException e) {
            ((HytaleLogger.Api)this.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to start console reader");
        }
    }

    @Override
    protected void shutdown() {
        this.getLogger().at(Level.INFO).log("Restoring terminal...");
        try {
            this.terminal.close();
        }
        catch (IOException e) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to restore terminal!");
        }
        this.consoleRunnable.interrupt();
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    private static class ConsoleRunnable
    implements Runnable {
        private final LineReader lineReader;
        private final ConsoleSender consoleSender;
        @Nonnull
        private final Thread consoleThread;

        public ConsoleRunnable(LineReader lineReader, ConsoleSender consoleSender) {
            this.lineReader = lineReader;
            this.consoleSender = consoleSender;
            this.consoleThread = new Thread((Runnable)this, "ConsoleThread");
            this.consoleThread.setDaemon(true);
            this.consoleThread.start();
        }

        @Override
        public void run() {
            try {
                String command;
                boolean isDumb;
                String terminalType = this.lineReader.getTerminal().getType();
                boolean bl = isDumb = "dumb".equals(terminalType) || "dumb-color".equals(terminalType);
                while (!this.consoleThread.isInterrupted() && (command = this.lineReader.readLine(isDumb ? null : "> ")) != null) {
                    if (!(command = command.trim()).isEmpty() && command.charAt(0) == '/') {
                        command = command.substring(1);
                    }
                    CommandManager.get().handleCommand(this.consoleSender, command);
                }
            }
            catch (UserInterruptException e) {
                HytaleServer.get().shutdownServer(ShutdownReason.SIGINT);
            }
            catch (IOError | EndOfFileException throwable) {
                // empty catch block
            }
        }

        public void interrupt() {
            this.consoleThread.interrupt();
        }
    }
}

