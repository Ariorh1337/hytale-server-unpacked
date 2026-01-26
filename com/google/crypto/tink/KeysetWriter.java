/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.Keyset;
import java.io.IOException;

public interface KeysetWriter {
    public void write(Keyset var1) throws IOException;

    public void write(EncryptedKeyset var1) throws IOException;
}

