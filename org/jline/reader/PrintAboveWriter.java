/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import java.io.StringWriter;
import org.jline.reader.LineReader;

public class PrintAboveWriter
extends StringWriter {
    private final LineReader reader;

    public PrintAboveWriter(LineReader reader) {
        this.reader = reader;
    }

    @Override
    public void flush() {
        StringBuffer buffer = this.getBuffer();
        int lastNewline = buffer.lastIndexOf("\n");
        if (lastNewline >= 0) {
            this.reader.printAbove(buffer.substring(0, lastNewline + 1));
            buffer.delete(0, lastNewline + 1);
        }
    }
}

