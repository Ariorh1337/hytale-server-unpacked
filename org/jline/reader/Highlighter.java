/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import java.util.regex.Pattern;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;

public interface Highlighter {
    public AttributedString highlight(LineReader var1, String var2);

    default public void refresh(LineReader reader) {
    }

    default public void setErrorPattern(Pattern errorPattern) {
    }

    default public void setErrorIndex(int errorIndex) {
    }
}

