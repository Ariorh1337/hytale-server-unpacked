/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl;

import java.io.FileDescriptor;
import java.io.FilterInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.Field;
import org.jline.nativ.JLineLibrary;
import org.jline.nativ.JLineNativeLoader;
import org.jline.terminal.Attributes;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.NonBlockingInputStream;

public abstract class AbstractPty
implements Pty {
    protected final TerminalProvider provider;
    protected final SystemStream systemStream;
    private Attributes current;
    private boolean skipNextLf;
    private static FileDescriptorCreator fileDescriptorCreator;

    public AbstractPty(TerminalProvider provider, SystemStream systemStream) {
        this.provider = provider;
        this.systemStream = systemStream;
    }

    @Override
    public void setAttr(Attributes attr) throws IOException {
        this.current = new Attributes(attr);
        this.doSetAttr(attr);
    }

    @Override
    public InputStream getSlaveInput() throws IOException {
        InputStream si = this.doGetSlaveInput();
        FilterInputStream nsi = new FilterInputStream(si){

            @Override
            public int read() throws IOException {
                int c;
                block4: {
                    while (true) {
                        c = super.read();
                        if (!AbstractPty.this.current.getInputFlag(Attributes.InputFlag.INORMEOL)) break block4;
                        if (c == 13) {
                            AbstractPty.this.skipNextLf = true;
                            c = 10;
                        } else {
                            if (c != 10) break;
                            if (AbstractPty.this.skipNextLf) {
                                AbstractPty.this.skipNextLf = false;
                                continue;
                            }
                        }
                        break block4;
                        break;
                    }
                    AbstractPty.this.skipNextLf = false;
                }
                return c;
            }
        };
        if (Boolean.parseBoolean(System.getProperty("org.jline.terminal.pty.nonBlockingReads", "true"))) {
            return new PtyInputStream(nsi);
        }
        return nsi;
    }

    protected abstract void doSetAttr(Attributes var1) throws IOException;

    protected abstract InputStream doGetSlaveInput() throws IOException;

    protected void checkInterrupted() throws InterruptedIOException {
        if (Thread.interrupted()) {
            throw new InterruptedIOException();
        }
    }

    @Override
    public TerminalProvider getProvider() {
        return this.provider;
    }

    @Override
    public SystemStream getSystemStream() {
        return this.systemStream;
    }

    protected static FileDescriptor newDescriptor(int fd) {
        if (fileDescriptorCreator == null) {
            String str = System.getProperty("org.jline.terminal.pty.fileDescriptorCreationMode", TerminalBuilder.PROP_FILE_DESCRIPTOR_CREATION_MODE_DEFAULT);
            String[] modes = str.split(",");
            IllegalStateException ise = new IllegalStateException("Unable to create FileDescriptor");
            for (String mode : modes) {
                try {
                    switch (mode) {
                        case "native": {
                            fileDescriptorCreator = new NativeFileDescriptorCreator();
                            break;
                        }
                        case "reflection": {
                            fileDescriptorCreator = new ReflectionFileDescriptorCreator();
                        }
                    }
                }
                catch (Throwable t) {
                    ise.addSuppressed(t);
                }
                if (fileDescriptorCreator != null) break;
            }
            if (fileDescriptorCreator == null) {
                throw ise;
            }
        }
        return fileDescriptorCreator.newDescriptor(fd);
    }

    class PtyInputStream
    extends NonBlockingInputStream {
        final InputStream in;
        int c = 0;

        PtyInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public int read(long timeout, boolean isPeek) throws IOException {
            long cur;
            AbstractPty.this.checkInterrupted();
            if (this.c != 0) {
                int r = this.c;
                if (!isPeek) {
                    this.c = 0;
                }
                return r;
            }
            this.setNonBlocking();
            long start = System.currentTimeMillis();
            do {
                int r;
                if ((r = this.in.read()) >= 0) {
                    if (isPeek) {
                        this.c = r;
                    }
                    return r;
                }
                AbstractPty.this.checkInterrupted();
                cur = System.currentTimeMillis();
            } while (timeout <= 0L || cur - start <= timeout);
            return -2;
        }

        private void setNonBlocking() {
            if (AbstractPty.this.current == null || AbstractPty.this.current.getControlChar(Attributes.ControlChar.VMIN) != 0 || AbstractPty.this.current.getControlChar(Attributes.ControlChar.VTIME) != 1) {
                try {
                    Attributes attr = AbstractPty.this.getAttr();
                    attr.setControlChar(Attributes.ControlChar.VMIN, 0);
                    attr.setControlChar(Attributes.ControlChar.VTIME, 1);
                    AbstractPty.this.setAttr(attr);
                }
                catch (IOException e) {
                    throw new IOError(e);
                }
            }
        }
    }

    static interface FileDescriptorCreator {
        public FileDescriptor newDescriptor(int var1);
    }

    static class NativeFileDescriptorCreator
    implements FileDescriptorCreator {
        NativeFileDescriptorCreator() {
            JLineNativeLoader.initialize();
        }

        @Override
        public FileDescriptor newDescriptor(int fd) {
            return JLineLibrary.newFileDescriptor(fd);
        }
    }

    static class ReflectionFileDescriptorCreator
    implements FileDescriptorCreator {
        private final Field fileDescriptorField;

        ReflectionFileDescriptorCreator() throws Exception {
            Field field = FileDescriptor.class.getDeclaredField("fd");
            field.setAccessible(true);
            this.fileDescriptorField = field;
        }

        @Override
        public FileDescriptor newDescriptor(int fd) {
            FileDescriptor descriptor = new FileDescriptor();
            try {
                this.fileDescriptorField.set(descriptor, fd);
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            return descriptor;
        }
    }
}

