/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util.io;

import com.hypixel.hytale.server.core.Options;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nonnull;

public abstract class BlockingDiskFile {
    protected final ReadWriteLock fileLock = new ReentrantReadWriteLock();
    protected final Path path;

    public BlockingDiskFile(Path path) {
        this.path = path;
    }

    protected abstract void read(BufferedReader var1) throws IOException;

    protected abstract void write(BufferedWriter var1) throws IOException;

    protected abstract void create(BufferedWriter var1) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void syncLoad() {
        this.fileLock.writeLock().lock();
        try {
            File file = this.toLocalFile();
            try {
                if (!file.exists()) {
                    if (Options.getOptionSet().has(Options.BARE)) {
                        byte[] bytes;
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                             BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(out));){
                            this.create(buf);
                            bytes = out.toByteArray();
                        }
                        try (BufferedReader buf = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));){
                            this.read(buf);
                            return;
                        }
                    }
                    try (BufferedWriter fileWriter = Files.newBufferedWriter(file.toPath(), new OpenOption[0]);){
                        this.create(fileWriter);
                    }
                }
                try (BufferedReader fileReader = Files.newBufferedReader(file.toPath());){
                    this.read(fileReader);
                    return;
                }
            }
            catch (Exception ex) {
                throw new RuntimeException("Failed to syncLoad() " + file.getAbsolutePath(), ex);
            }
        }
        finally {
            this.fileLock.writeLock().unlock();
        }
    }

    public void syncSave() {
        File file = null;
        this.fileLock.readLock().lock();
        try {
            file = this.toLocalFile();
            try (BufferedWriter fileWriter = Files.newBufferedWriter(file.toPath(), new OpenOption[0]);){
                this.write(fileWriter);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to syncSave() " + (file != null ? file.getAbsolutePath() : null), ex);
        }
        finally {
            this.fileLock.readLock().unlock();
        }
    }

    @Nonnull
    protected File toLocalFile() {
        return this.path.toFile();
    }
}

