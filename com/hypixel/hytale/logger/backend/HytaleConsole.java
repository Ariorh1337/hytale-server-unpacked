/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.logger.backend;

import com.hypixel.hytale.logger.backend.HytaleLogFormatter;
import com.hypixel.hytale.logger.backend.HytaleLoggerBackend;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HytaleConsole
extends Thread {
    public static final String TYPE_DUMB = "dumb";
    public static final HytaleConsole INSTANCE = new HytaleConsole();
    private final BlockingQueue<LogRecord> logRecords = new LinkedBlockingQueue<LogRecord>();
    private final HytaleLogFormatter formatter = new HytaleLogFormatter(this::shouldPrintAnsi);
    @Nullable
    private OutputStreamWriter soutwriter = new OutputStreamWriter((OutputStream)HytaleLoggerBackend.REAL_SOUT, StandardCharsets.UTF_8);
    @Nullable
    private OutputStreamWriter serrwriter = new OutputStreamWriter((OutputStream)HytaleLoggerBackend.REAL_SERR, StandardCharsets.UTF_8);
    private String terminalType = "dumb";

    private HytaleConsole() {
        super("HytaleConsole");
        this.setDaemon(true);
        this.start();
    }

    public void publish(@Nonnull LogRecord logRecord) {
        if (!this.isAlive()) {
            this.publish0(logRecord);
            return;
        }
        this.logRecords.offer(logRecord);
    }

    @Override
    public void run() {
        try {
            while (!this.isInterrupted()) {
                this.publish0(this.logRecords.take());
            }
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        this.interrupt();
        try {
            this.join();
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        ObjectArrayList list = new ObjectArrayList();
        this.logRecords.drainTo(list);
        list.forEach(this::publish0);
        if (this.soutwriter != null) {
            try {
                this.soutwriter.flush();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.soutwriter = null;
        }
        if (this.serrwriter != null) {
            try {
                this.serrwriter.flush();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.serrwriter = null;
        }
    }

    private void publish0(@Nonnull LogRecord record) {
        String msg;
        try {
            msg = this.formatter.format(record);
        }
        catch (Exception ex) {
            if (this.serrwriter != null) {
                ex.printStackTrace(new PrintWriter(this.serrwriter));
            } else {
                ex.printStackTrace(System.err);
            }
            return;
        }
        try {
            if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
                if (this.serrwriter != null) {
                    this.serrwriter.write(msg);
                    try {
                        this.serrwriter.flush();
                    }
                    catch (Exception exception) {}
                } else {
                    HytaleLoggerBackend.REAL_SERR.print(msg);
                }
            } else if (this.soutwriter != null) {
                this.soutwriter.write(msg);
                try {
                    this.soutwriter.flush();
                }
                catch (Exception exception) {}
            } else {
                HytaleLoggerBackend.REAL_SOUT.print(msg);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setTerminal(String type) {
        this.terminalType = type;
    }

    private boolean shouldPrintAnsi() {
        return !TYPE_DUMB.equals(this.terminalType);
    }

    public HytaleLogFormatter getFormatter() {
        return this.formatter;
    }
}

