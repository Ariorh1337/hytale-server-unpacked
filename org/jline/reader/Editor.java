/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import java.io.IOException;
import java.util.List;

public interface Editor {
    public void open(List<String> var1) throws IOException;

    public void run() throws IOException;

    public void setRestricted(boolean var1);
}

