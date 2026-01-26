/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import java.util.List;
import java.util.Map;
import org.jline.reader.Candidate;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.LineReader;

public interface CompletionMatcher {
    public void compile(Map<LineReader.Option, Boolean> var1, boolean var2, CompletingParsedLine var3, boolean var4, int var5, String var6);

    public List<Candidate> matches(List<Candidate> var1);

    public Candidate exactMatch();

    public String getCommonPrefix();
}

