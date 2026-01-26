/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.bson.json.JsonBuffer;
import org.bson.json.JsonParseException;

class JsonStreamBuffer
implements JsonBuffer {
    private final Reader reader;
    private final List<Integer> markedPositions = new ArrayList<Integer>();
    private final int initialBufferSize;
    private int position;
    private int lastChar;
    private boolean reuseLastChar;
    private boolean eof;
    private char[] buffer;
    private int bufferStartPos;
    private int bufferCount;

    JsonStreamBuffer(Reader reader) {
        this(reader, 16);
    }

    JsonStreamBuffer(Reader reader, int initialBufferSize) {
        this.initialBufferSize = initialBufferSize;
        this.reader = reader;
        this.resetBuffer();
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public int read() {
        if (this.eof) {
            throw new JsonParseException("Trying to read past EOF.");
        }
        if (this.reuseLastChar) {
            this.reuseLastChar = false;
            int reusedChar = this.lastChar;
            this.lastChar = -1;
            ++this.position;
            return reusedChar;
        }
        if (this.position - this.bufferStartPos < this.bufferCount) {
            int currChar;
            this.lastChar = currChar = this.buffer[this.position - this.bufferStartPos];
            ++this.position;
            return currChar;
        }
        if (this.markedPositions.isEmpty()) {
            this.resetBuffer();
        }
        try {
            int nextChar = this.reader.read();
            if (nextChar != -1) {
                this.lastChar = nextChar;
                this.addToBuffer((char)nextChar);
            }
            ++this.position;
            if (nextChar == -1) {
                this.eof = true;
            }
            return nextChar;
        }
        catch (IOException e) {
            throw new JsonParseException(e);
        }
    }

    private void resetBuffer() {
        this.bufferStartPos = -1;
        this.bufferCount = 0;
        this.buffer = new char[this.initialBufferSize];
    }

    @Override
    public void unread(int c) {
        this.eof = false;
        if (c != -1 && this.lastChar == c) {
            this.reuseLastChar = true;
            --this.position;
        }
    }

    @Override
    public int mark() {
        if (this.bufferCount == 0) {
            this.bufferStartPos = this.position;
        }
        if (!this.markedPositions.contains(this.position)) {
            this.markedPositions.add(this.position);
        }
        return this.position;
    }

    @Override
    public void reset(int markPos) {
        if (markPos > this.position) {
            throw new IllegalStateException("mark cannot reset ahead of position, only back");
        }
        int idx = this.markedPositions.indexOf(markPos);
        if (idx == -1) {
            throw new IllegalArgumentException("mark invalidated");
        }
        if (markPos != this.position) {
            this.reuseLastChar = false;
        }
        this.markedPositions.subList(idx, this.markedPositions.size()).clear();
        this.position = markPos;
    }

    @Override
    public void discard(int markPos) {
        int idx = this.markedPositions.indexOf(markPos);
        if (idx == -1) {
            return;
        }
        this.markedPositions.subList(idx, this.markedPositions.size()).clear();
    }

    private void addToBuffer(char curChar) {
        if (!this.markedPositions.isEmpty()) {
            if (this.bufferCount == this.buffer.length) {
                char[] newBuffer = new char[this.buffer.length * 2];
                System.arraycopy(this.buffer, 0, newBuffer, 0, this.bufferCount);
                this.buffer = newBuffer;
            }
            this.buffer[this.bufferCount] = curChar;
            ++this.bufferCount;
        }
    }
}

