/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public interface Completer {
    public void complete(LineReader var1, ParsedLine var2, List<Candidate> var3);
}

