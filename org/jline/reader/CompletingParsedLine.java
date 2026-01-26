/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import org.jline.reader.ParsedLine;

public interface CompletingParsedLine
extends ParsedLine {
    public CharSequence escape(CharSequence var1, boolean var2);

    public int rawWordCursor();

    public int rawWordLength();
}

