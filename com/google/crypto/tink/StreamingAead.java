/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;

public interface StreamingAead {
    public WritableByteChannel newEncryptingChannel(WritableByteChannel var1, byte[] var2) throws GeneralSecurityException, IOException;

    public SeekableByteChannel newSeekableDecryptingChannel(SeekableByteChannel var1, byte[] var2) throws GeneralSecurityException, IOException;

    public ReadableByteChannel newDecryptingChannel(ReadableByteChannel var1, byte[] var2) throws GeneralSecurityException, IOException;

    public OutputStream newEncryptingStream(OutputStream var1, byte[] var2) throws GeneralSecurityException, IOException;

    public InputStream newDecryptingStream(InputStream var1, byte[] var2) throws GeneralSecurityException, IOException;
}

