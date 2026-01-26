/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.Timeout;

public class NonBlockingReaderImpl
extends NonBlockingReader {
    public static final int READ_EXPIRED = -2;
    private Reader in;
    private int ch = -2;
    private String name;
    private boolean threadIsReading = false;
    private IOException exception = null;
    private long threadDelay = 60000L;
    private Thread thread;

    public NonBlockingReaderImpl(String name, Reader in) {
        this.in = in;
        this.name = name;
    }

    private synchronized void startReadingThreadIfNeeded() {
        if (this.thread == null) {
            this.thread = new Thread(this::run);
            this.thread.setName(this.name + " non blocking reader thread");
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    @Override
    public synchronized void shutdown() {
        if (this.thread != null) {
            this.notify();
        }
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.shutdown();
    }

    @Override
    public synchronized boolean ready() throws IOException {
        return this.ch >= 0 || this.in.ready();
    }

    @Override
    public int readBuffered(char[] b, int off, int len, long timeout) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len < b.length) {
            throw new IllegalArgumentException();
        }
        if (len == 0) {
            return 0;
        }
        if (this.exception != null) {
            assert (this.ch == -2);
            IOException toBeThrown = this.exception;
            this.exception = null;
            throw toBeThrown;
        }
        if (this.ch >= -1) {
            b[0] = (char)this.ch;
            this.ch = -2;
            return 1;
        }
        if (!this.threadIsReading && timeout <= 0L) {
            return this.in.read(b, off, len);
        }
        int c = this.read(timeout, false);
        if (c >= 0) {
            b[off] = (char)c;
            return 1;
        }
        return c;
    }

    @Override
    protected synchronized int read(long timeout, boolean isPeek) throws IOException {
        if (this.exception != null) {
            assert (this.ch == -2);
            IOException toBeThrown = this.exception;
            if (!isPeek) {
                this.exception = null;
            }
            throw toBeThrown;
        }
        if (this.ch >= -1) {
            assert (this.exception == null);
        } else if (!isPeek && timeout <= 0L && !this.threadIsReading) {
            this.ch = this.in.read();
        } else {
            if (!this.threadIsReading) {
                this.threadIsReading = true;
                this.startReadingThreadIfNeeded();
                this.notifyAll();
            }
            Timeout t = new Timeout(timeout);
            while (!t.elapsed()) {
                try {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    this.wait(t.timeout());
                }
                catch (InterruptedException e) {
                    this.exception = (IOException)new InterruptedIOException().initCause(e);
                }
                if (this.exception != null) {
                    assert (this.ch == -2);
                    IOException toBeThrown = this.exception;
                    if (!isPeek) {
                        this.exception = null;
                    }
                    throw toBeThrown;
                }
                if (this.ch < -1) continue;
                assert (this.exception == null);
                break;
            }
        }
        int ret = this.ch;
        if (!isPeek) {
            this.ch = -2;
        }
        return ret;
    }

    /*
     * Exception decompiling
     */
    private void run() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [25[UNCONDITIONALDOLOOP]], but top level block is 4[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public synchronized void clear() throws IOException {
        while (this.ready()) {
            this.read();
        }
    }
}

