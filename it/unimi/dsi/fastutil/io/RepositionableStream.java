/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.io;

import java.io.IOException;

public interface RepositionableStream {
    public void position(long var1) throws IOException;

    public long position() throws IOException;
}

