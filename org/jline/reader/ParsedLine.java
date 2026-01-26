/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import java.util.List;

public interface ParsedLine {
    public String word();

    public int wordCursor();

    public int wordIndex();

    public List<String> words();

    public String line();

    public int cursor();
}

