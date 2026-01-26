/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import org.jline.reader.History;

public interface Expander {
    public String expandHistory(History var1, String var2);

    public String expandVar(String var1);
}

