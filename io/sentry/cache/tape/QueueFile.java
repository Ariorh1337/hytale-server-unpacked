/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.cache.tape;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class QueueFile
implements Closeable,
Iterable<byte[]> {
    private static final int VERSIONED_HEADER = -2147483647;
    static final int INITIAL_LENGTH = 4096;
    private static final byte[] ZEROES = new byte[4096];
    RandomAccessFile raf;
    final File file;
    final int headerLength = 32;
    long fileLength;
    int elementCount;
    Element first;
    private Element last;
    private final byte[] buffer = new byte[32];
    int modCount = 0;
    private final boolean zero;
    private final int maxElements;
    boolean closed;

    static RandomAccessFile initializeFromFile(File file) throws IOException {
        if (!file.exists()) {
            File tempFile = new File(file.getPath() + ".tmp");
            try (RandomAccessFile raf = QueueFile.open(tempFile);){
                raf.setLength(4096L);
                raf.seek(0L);
                raf.writeInt(-2147483647);
                raf.writeLong(4096L);
            }
            if (!tempFile.renameTo(file)) {
                throw new IOException("Rename failed!");
            }
        }
        return QueueFile.open(file);
    }

    private static RandomAccessFile open(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, "rwd");
    }

    QueueFile(File file, RandomAccessFile raf, boolean zero, int maxElements) throws IOException {
        this.file = file;
        this.raf = raf;
        this.zero = zero;
        this.maxElements = maxElements;
        this.readInitialData();
    }

    private void readInitialData() throws IOException {
        this.raf.seek(0L);
        this.raf.readFully(this.buffer);
        this.fileLength = QueueFile.readLong(this.buffer, 4);
        this.elementCount = QueueFile.readInt(this.buffer, 12);
        long firstOffset = QueueFile.readLong(this.buffer, 16);
        long lastOffset = QueueFile.readLong(this.buffer, 24);
        if (this.fileLength > this.raf.length()) {
            throw new IOException("File is truncated. Expected length: " + this.fileLength + ", Actual length: " + this.raf.length());
        }
        if (this.fileLength <= 32L) {
            throw new IOException("File is corrupt; length stored in header (" + this.fileLength + ") is invalid.");
        }
        this.first = this.readElement(firstOffset);
        this.last = this.readElement(lastOffset);
    }

    private void resetFile() throws IOException {
        this.raf.close();
        this.file.delete();
        this.raf = QueueFile.initializeFromFile(this.file);
        this.readInitialData();
    }

    private static void writeInt(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte)(value >> 24);
        buffer[offset + 1] = (byte)(value >> 16);
        buffer[offset + 2] = (byte)(value >> 8);
        buffer[offset + 3] = (byte)value;
    }

    private static int readInt(byte[] buffer, int offset) {
        return ((buffer[offset] & 0xFF) << 24) + ((buffer[offset + 1] & 0xFF) << 16) + ((buffer[offset + 2] & 0xFF) << 8) + (buffer[offset + 3] & 0xFF);
    }

    private static void writeLong(byte[] buffer, int offset, long value) {
        buffer[offset] = (byte)(value >> 56);
        buffer[offset + 1] = (byte)(value >> 48);
        buffer[offset + 2] = (byte)(value >> 40);
        buffer[offset + 3] = (byte)(value >> 32);
        buffer[offset + 4] = (byte)(value >> 24);
        buffer[offset + 5] = (byte)(value >> 16);
        buffer[offset + 6] = (byte)(value >> 8);
        buffer[offset + 7] = (byte)value;
    }

    private static long readLong(byte[] buffer, int offset) {
        return (((long)buffer[offset] & 0xFFL) << 56) + (((long)buffer[offset + 1] & 0xFFL) << 48) + (((long)buffer[offset + 2] & 0xFFL) << 40) + (((long)buffer[offset + 3] & 0xFFL) << 32) + (((long)buffer[offset + 4] & 0xFFL) << 24) + (((long)buffer[offset + 5] & 0xFFL) << 16) + (((long)buffer[offset + 6] & 0xFFL) << 8) + ((long)buffer[offset + 7] & 0xFFL);
    }

    private void writeHeader(long fileLength, int elementCount, long firstPosition, long lastPosition) throws IOException {
        this.raf.seek(0L);
        QueueFile.writeInt(this.buffer, 0, -2147483647);
        QueueFile.writeLong(this.buffer, 4, fileLength);
        QueueFile.writeInt(this.buffer, 12, elementCount);
        QueueFile.writeLong(this.buffer, 16, firstPosition);
        QueueFile.writeLong(this.buffer, 24, lastPosition);
        this.raf.write(this.buffer, 0, 32);
    }

    Element readElement(long position) throws IOException {
        if (position == 0L) {
            return Element.NULL;
        }
        boolean success = this.ringRead(position, this.buffer, 0, 4);
        if (!success) {
            return Element.NULL;
        }
        int length = QueueFile.readInt(this.buffer, 0);
        return new Element(position, length);
    }

    long wrapPosition(long position) {
        return position < this.fileLength ? position : 32L + position - this.fileLength;
    }

    private void ringWrite(long position, byte[] buffer, int offset, int count) throws IOException {
        if ((position = this.wrapPosition(position)) + (long)count <= this.fileLength) {
            this.raf.seek(position);
            this.raf.write(buffer, offset, count);
        } else {
            int beforeEof = (int)(this.fileLength - position);
            this.raf.seek(position);
            this.raf.write(buffer, offset, beforeEof);
            this.raf.seek(32L);
            this.raf.write(buffer, offset + beforeEof, count - beforeEof);
        }
    }

    private void ringErase(long position, long length) throws IOException {
        while (length > 0L) {
            int chunk = (int)Math.min(length, (long)ZEROES.length);
            this.ringWrite(position, ZEROES, 0, chunk);
            length -= (long)chunk;
            position += (long)chunk;
        }
    }

    boolean ringRead(long position, byte[] buffer, int offset, int count) throws IOException {
        try {
            position = this.wrapPosition(position);
            if (position + (long)count <= this.fileLength) {
                this.raf.seek(position);
                this.raf.readFully(buffer, offset, count);
            } else {
                int beforeEof = (int)(this.fileLength - position);
                this.raf.seek(position);
                this.raf.readFully(buffer, offset, beforeEof);
                this.raf.seek(32L);
                this.raf.readFully(buffer, offset + beforeEof, count - beforeEof);
            }
            return true;
        }
        catch (EOFException e) {
            this.resetFile();
        }
        catch (IOException e) {
            throw e;
        }
        catch (Throwable e) {
            this.resetFile();
        }
        return false;
    }

    public void add(byte[] data) throws IOException {
        this.add(data, 0, data.length);
    }

    public void add(byte[] data, int offset, int count) throws IOException {
        if (data == null) {
            throw new NullPointerException("data == null");
        }
        if ((offset | count) < 0 || count > data.length - offset) {
            throw new IndexOutOfBoundsException();
        }
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        if (this.isAtFullCapacity()) {
            this.remove();
        }
        this.expandIfNecessary(count);
        boolean wasEmpty = this.isEmpty();
        long position = wasEmpty ? 32L : this.wrapPosition(this.last.position + 4L + (long)this.last.length);
        Element newLast = new Element(position, count);
        QueueFile.writeInt(this.buffer, 0, count);
        this.ringWrite(newLast.position, this.buffer, 0, 4);
        this.ringWrite(newLast.position + 4L, data, offset, count);
        long firstPosition = wasEmpty ? newLast.position : this.first.position;
        this.writeHeader(this.fileLength, this.elementCount + 1, firstPosition, newLast.position);
        this.last = newLast;
        ++this.elementCount;
        ++this.modCount;
        if (wasEmpty) {
            this.first = this.last;
        }
    }

    private long usedBytes() {
        if (this.elementCount == 0) {
            return 32L;
        }
        if (this.last.position >= this.first.position) {
            return this.last.position - this.first.position + 4L + (long)this.last.length + 32L;
        }
        return this.last.position + 4L + (long)this.last.length + this.fileLength - this.first.position;
    }

    private long remainingBytes() {
        return this.fileLength - this.usedBytes();
    }

    public boolean isEmpty() {
        return this.elementCount == 0;
    }

    private void expandIfNecessary(long dataLength) throws IOException {
        long newLength;
        long elementLength = 4L + dataLength;
        long remainingBytes = this.remainingBytes();
        if (remainingBytes >= elementLength) {
            return;
        }
        long previousLength = this.fileLength;
        while ((remainingBytes += (previousLength = (newLength = previousLength << 1))) < elementLength) {
        }
        this.setLength(newLength);
        long endOfLastElement = this.wrapPosition(this.last.position + 4L + (long)this.last.length);
        long count = 0L;
        if (endOfLastElement <= this.first.position) {
            FileChannel channel = this.raf.getChannel();
            channel.position(this.fileLength);
            count = endOfLastElement - 32L;
            if (channel.transferTo(32L, count, channel) != count) {
                throw new AssertionError((Object)"Copied insufficient number of bytes!");
            }
        }
        if (this.last.position < this.first.position) {
            long newLastPosition = this.fileLength + this.last.position - 32L;
            this.writeHeader(newLength, this.elementCount, this.first.position, newLastPosition);
            this.last = new Element(newLastPosition, this.last.length);
        } else {
            this.writeHeader(newLength, this.elementCount, this.first.position, this.last.position);
        }
        this.fileLength = newLength;
        if (this.zero) {
            this.ringErase(32L, count);
        }
    }

    private void setLength(long newLength) throws IOException {
        this.raf.setLength(newLength);
        this.raf.getChannel().force(true);
    }

    @Nullable
    public byte[] peek() throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        if (this.isEmpty()) {
            return null;
        }
        int length = this.first.length;
        byte[] data = new byte[length];
        boolean success = this.ringRead(this.first.position + 4L, data, 0, length);
        return (byte[])(success ? data : null);
    }

    @Override
    public Iterator<byte[]> iterator() {
        return new ElementIterator();
    }

    public int size() {
        return this.elementCount;
    }

    public void remove() throws IOException {
        this.remove(1);
    }

    public void remove(int n) throws IOException {
        if (n < 0) {
            throw new IllegalArgumentException("Cannot remove negative (" + n + ") number of elements.");
        }
        if (n == 0) {
            return;
        }
        if (n == this.elementCount) {
            this.clear();
            return;
        }
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        if (n > this.elementCount) {
            throw new IllegalArgumentException("Cannot remove more elements (" + n + ") than present in queue (" + this.elementCount + ").");
        }
        long eraseStartPosition = this.first.position;
        long eraseTotalLength = 0L;
        long newFirstPosition = this.first.position;
        int newFirstLength = this.first.length;
        for (int i = 0; i < n; ++i) {
            eraseTotalLength += (long)(4 + newFirstLength);
            boolean success = this.ringRead(newFirstPosition = this.wrapPosition(newFirstPosition + 4L + (long)newFirstLength), this.buffer, 0, 4);
            if (!success) {
                return;
            }
            newFirstLength = QueueFile.readInt(this.buffer, 0);
        }
        this.writeHeader(this.fileLength, this.elementCount - n, newFirstPosition, this.last.position);
        this.elementCount -= n;
        ++this.modCount;
        this.first = new Element(newFirstPosition, newFirstLength);
        if (this.zero) {
            this.ringErase(eraseStartPosition, eraseTotalLength);
        }
    }

    public void clear() throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        this.writeHeader(4096L, 0, 0L, 0L);
        if (this.zero) {
            this.raf.seek(32L);
            this.raf.write(ZEROES, 0, 4064);
        }
        this.elementCount = 0;
        this.first = Element.NULL;
        this.last = Element.NULL;
        if (this.fileLength > 4096L) {
            this.setLength(4096L);
        }
        this.fileLength = 4096L;
        ++this.modCount;
    }

    public boolean isAtFullCapacity() {
        if (this.maxElements == -1) {
            return false;
        }
        return this.size() == this.maxElements;
    }

    public File file() {
        return this.file;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        this.raf.close();
    }

    public String toString() {
        return "QueueFile{file=" + this.file + ", zero=" + this.zero + ", length=" + this.fileLength + ", size=" + this.elementCount + ", first=" + this.first + ", last=" + this.last + '}';
    }

    static <T extends Throwable> T getSneakyThrowable(Throwable t) throws T {
        throw t;
    }

    static final class Element {
        static final Element NULL = new Element(0L, 0);
        static final int HEADER_LENGTH = 4;
        final long position;
        final int length;

        Element(long position, int length) {
            this.position = position;
            this.length = length;
        }

        public String toString() {
            return this.getClass().getSimpleName() + "[position=" + this.position + ", length=" + this.length + "]";
        }
    }

    private final class ElementIterator
    implements Iterator<byte[]> {
        int nextElementIndex = 0;
        private long nextElementPosition;
        int expectedModCount;

        ElementIterator() {
            this.nextElementPosition = QueueFile.this.first.position;
            this.expectedModCount = QueueFile.this.modCount;
        }

        private void checkForComodification() {
            if (QueueFile.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public boolean hasNext() {
            if (QueueFile.this.closed) {
                throw new IllegalStateException("closed");
            }
            this.checkForComodification();
            return this.nextElementIndex != QueueFile.this.elementCount;
        }

        @Override
        public byte[] next() {
            if (QueueFile.this.closed) {
                throw new IllegalStateException("closed");
            }
            this.checkForComodification();
            if (QueueFile.this.isEmpty()) {
                throw new NoSuchElementException();
            }
            if (this.nextElementIndex >= QueueFile.this.elementCount) {
                throw new NoSuchElementException();
            }
            try {
                Element current = QueueFile.this.readElement(this.nextElementPosition);
                byte[] buffer = new byte[current.length];
                this.nextElementPosition = QueueFile.this.wrapPosition(current.position + 4L);
                boolean success = QueueFile.this.ringRead(this.nextElementPosition, buffer, 0, current.length);
                if (!success) {
                    this.nextElementIndex = QueueFile.this.elementCount;
                    return ZEROES;
                }
                this.nextElementPosition = QueueFile.this.wrapPosition(current.position + 4L + (long)current.length);
                ++this.nextElementIndex;
                return buffer;
            }
            catch (IOException e) {
                throw (Error)QueueFile.getSneakyThrowable(e);
            }
            catch (OutOfMemoryError e) {
                try {
                    QueueFile.this.resetFile();
                    this.nextElementIndex = QueueFile.this.elementCount;
                }
                catch (IOException ex) {
                    throw (Error)QueueFile.getSneakyThrowable(ex);
                }
                return ZEROES;
            }
        }

        @Override
        public void remove() {
            this.checkForComodification();
            if (QueueFile.this.isEmpty()) {
                throw new NoSuchElementException();
            }
            if (this.nextElementIndex != 1) {
                throw new UnsupportedOperationException("Removal is only permitted from the head.");
            }
            try {
                QueueFile.this.remove();
            }
            catch (IOException e) {
                throw (Error)QueueFile.getSneakyThrowable(e);
            }
            this.expectedModCount = QueueFile.this.modCount;
            --this.nextElementIndex;
        }
    }

    public static final class Builder {
        final File file;
        boolean zero = true;
        int size = -1;

        public Builder(File file) {
            if (file == null) {
                throw new NullPointerException("file == null");
            }
            this.file = file;
        }

        public Builder zero(boolean zero) {
            this.zero = zero;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public QueueFile build() throws IOException {
            RandomAccessFile raf = QueueFile.initializeFromFile(this.file);
            QueueFile qf = null;
            try {
                QueueFile queueFile = qf = new QueueFile(this.file, raf, this.zero, this.size);
                return queueFile;
            }
            finally {
                if (qf == null) {
                    raf.close();
                }
            }
        }
    }
}

