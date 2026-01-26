/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.logger.backend;

import com.hypixel.hytale.logger.backend.HytaleLogFormatter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HytaleFileHandler
extends Thread {
    public static final DateTimeFormatter LOG_FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    public static final HytaleFileHandler INSTANCE = new HytaleFileHandler();
    private final BlockingQueue<LogRecord> logRecords = new LinkedBlockingQueue<LogRecord>();
    @Nullable
    private FileHandler fileHandler;

    public HytaleFileHandler() {
        super("HytaleLogger");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        if (this.fileHandler == null) {
            throw new IllegalStateException("Thread should not be started when no file handler exists!");
        }
        try {
            while (!this.isInterrupted()) {
                this.fileHandler.publish(this.logRecords.take());
            }
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @Nullable
    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    public void enable() {
        if (this.fileHandler != null) {
            throw new IllegalStateException("Already enabled!");
        }
        try {
            String fileNamePart;
            String fileName;
            Path logsDirectory = Paths.get("logs/", new String[0]);
            if (!Files.isDirectory(logsDirectory, new LinkOption[0])) {
                Files.createDirectory(logsDirectory, new FileAttribute[0]);
            }
            if (Files.exists(Paths.get(fileName = (fileNamePart = "logs/" + LOG_FILE_DATE_FORMAT.format(LocalDateTime.now())) + "_server.log", new String[0]), new LinkOption[0])) {
                fileName = fileNamePart + "%u_server.log";
            }
            this.fileHandler = new FileHandler(fileName);
            this.fileHandler.setEncoding("UTF-8");
            this.fileHandler.setLevel(Level.ALL);
            this.fileHandler.setFormatter(new HytaleLogFormatter(() -> false));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create file handler!", e);
        }
        this.start();
    }

    public void log(@Nonnull LogRecord logRecord) {
        if (!this.isAlive()) {
            if (this.fileHandler != null) {
                this.fileHandler.publish(logRecord);
            }
            return;
        }
        this.logRecords.add(logRecord);
    }

    public void shutdown() {
        if (this.fileHandler != null) {
            this.interrupt();
            try {
                this.join();
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            ObjectArrayList list = new ObjectArrayList();
            this.logRecords.drainTo(list);
            list.forEach(this.fileHandler::publish);
            this.fileHandler.flush();
            this.fileHandler.close();
            this.fileHandler = null;
        }
    }
}

