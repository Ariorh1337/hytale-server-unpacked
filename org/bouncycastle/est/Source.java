/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Source<T> {
    public InputStream getInputStream() throws IOException;

    public OutputStream getOutputStream() throws IOException;

    public T getSession();

    public void close() throws IOException;
}

