/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.Timeout;

public class NonBlockingInputStreamImpl
extends NonBlockingInputStream {
    private InputStream in;
    private int b = -2;
    private String name;
    private boolean threadIsReading = false;
    private IOException exception = null;
    private long threadDelay = 60000L;
    private Thread thread;

    public NonBlockingInputStreamImpl(String name, InputStream in) {
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
    public synchronized int read(long timeout, boolean isPeek) throws IOException {
        if (this.exception != null) {
            assert (this.b == -2);
            IOException toBeThrown = this.exception;
            if (!isPeek) {
                this.exception = null;
            }
            throw toBeThrown;
        }
        if (this.b >= -1) {
            assert (this.exception == null);
        } else if (!isPeek && timeout <= 0L && !this.threadIsReading) {
            this.b = this.in.read();
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
                    assert (this.b == -2);
                    IOException toBeThrown = this.exception;
                    if (!isPeek) {
                        this.exception = null;
                    }
                    throw toBeThrown;
                }
                if (this.b < -1) continue;
                assert (this.exception == null);
                break;
            }
        }
        int ret = this.b;
        if (!isPeek) {
            this.b = -2;
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
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [29[UNCONDITIONALDOLOOP]], but top level block is 9[TRYBLOCK]
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
}

