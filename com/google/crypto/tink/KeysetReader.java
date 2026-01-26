/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.Keyset;
import java.io.IOException;

public interface KeysetReader {
    public Keyset read() throws IOException;

    public EncryptedKeyset readEncrypted() throws IOException;
}

