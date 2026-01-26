/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;

abstract class BufferBaseDigest
implements ExtendedDigest {
    protected int DigestSize;
    protected int BlockSize;
    protected byte[] m_buf;
    protected int m_bufPos;
    protected String algorithmName;
    protected ProcessingBuffer processor;

    protected BufferBaseDigest(ProcessingBufferType processingBufferType, int n) {
        this.BlockSize = n;
        this.m_buf = new byte[n];
        switch (processingBufferType.ord) {
            case 0: {
                this.processor = new BufferedProcessor();
                break;
            }
            case 1: {
                this.processor = new ImmediateProcessor();
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return this.algorithmName;
    }

    @Override
    public int getDigestSize() {
        return this.DigestSize;
    }

    @Override
    public int getByteLength() {
        return this.BlockSize;
    }

    @Override
    public void update(byte by) {
        this.processor.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.ensureSufficientInputBuffer(byArray, n, n2);
        int n3 = this.BlockSize - this.m_bufPos;
        if (this.processor.isLengthWithinAvailableSpace(n2, n3)) {
            System.arraycopy(byArray, n, this.m_buf, this.m_bufPos, n2);
            this.m_bufPos += n2;
            return;
        }
        if (this.m_bufPos > 0) {
            System.arraycopy(byArray, n, this.m_buf, this.m_bufPos, n3);
            n += n3;
            n2 -= n3;
            this.processBytes(this.m_buf, 0);
        }
        while (this.processor.isLengthExceedingBlockSize(n2, this.BlockSize)) {
            this.processBytes(byArray, n);
            n += this.BlockSize;
            n2 -= this.BlockSize;
        }
        System.arraycopy(byArray, n, this.m_buf, 0, n2);
        this.m_bufPos = n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        this.ensureSufficientOutputBuffer(byArray, n);
        this.finish(byArray, n);
        this.reset();
        return this.DigestSize;
    }

    @Override
    public void reset() {
        Arrays.clear(this.m_buf);
        this.m_bufPos = 0;
    }

    protected void ensureSufficientInputBuffer(byte[] byArray, int n, int n2) {
        if (n + n2 > byArray.length) {
            throw new DataLengthException("input buffer too short");
        }
    }

    protected void ensureSufficientOutputBuffer(byte[] byArray, int n) {
        if (this.DigestSize + n > byArray.length) {
            throw new OutputLengthException("output buffer is too short");
        }
    }

    protected abstract void processBytes(byte[] var1, int var2);

    protected abstract void finish(byte[] var1, int var2);

    private class BufferedProcessor
    implements ProcessingBuffer {
        private BufferedProcessor() {
        }

        @Override
        public void update(byte by) {
            if (BufferBaseDigest.this.m_bufPos == BufferBaseDigest.this.BlockSize) {
                BufferBaseDigest.this.processBytes(BufferBaseDigest.this.m_buf, 0);
                BufferBaseDigest.this.m_bufPos = 0;
            }
            BufferBaseDigest.this.m_buf[BufferBaseDigest.this.m_bufPos++] = by;
        }

        @Override
        public boolean isLengthWithinAvailableSpace(int n, int n2) {
            return n <= n2;
        }

        @Override
        public boolean isLengthExceedingBlockSize(int n, int n2) {
            return n > n2;
        }
    }

    private class ImmediateProcessor
    implements ProcessingBuffer {
        private ImmediateProcessor() {
        }

        @Override
        public void update(byte by) {
            BufferBaseDigest.this.m_buf[BufferBaseDigest.this.m_bufPos] = by;
            if (++BufferBaseDigest.this.m_bufPos == BufferBaseDigest.this.BlockSize) {
                BufferBaseDigest.this.processBytes(BufferBaseDigest.this.m_buf, 0);
                BufferBaseDigest.this.m_bufPos = 0;
            }
        }

        @Override
        public boolean isLengthWithinAvailableSpace(int n, int n2) {
            return n < n2;
        }

        @Override
        public boolean isLengthExceedingBlockSize(int n, int n2) {
            return n >= n2;
        }
    }

    protected static interface ProcessingBuffer {
        public void update(byte var1);

        public boolean isLengthWithinAvailableSpace(int var1, int var2);

        public boolean isLengthExceedingBlockSize(int var1, int var2);
    }

    protected static class ProcessingBufferType {
        public static final int BUFFERED = 0;
        public static final int IMMEDIATE = 1;
        public static final ProcessingBufferType Buffered = new ProcessingBufferType(0);
        public static final ProcessingBufferType Immediate = new ProcessingBufferType(1);
        private final int ord;

        ProcessingBufferType(int n) {
            this.ord = n;
        }
    }
}

