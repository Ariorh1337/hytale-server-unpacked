/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.KeysetReader;
import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.Keyset;
import com.google.errorprone.annotations.InlineMe;
import com.google.protobuf.ExtensionRegistryLite;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class BinaryKeysetReader
implements KeysetReader {
    private final InputStream inputStream;

    public static KeysetReader withInputStream(InputStream stream) {
        return new BinaryKeysetReader(stream);
    }

    public static KeysetReader withBytes(byte[] bytes) {
        return new BinaryKeysetReader(new ByteArrayInputStream(bytes));
    }

    @Deprecated
    @InlineMe(replacement="BinaryKeysetReader.withInputStream(new FileInputStream(file))", imports={"com.google.crypto.tink.BinaryKeysetReader", "java.io.FileInputStream"})
    public static KeysetReader withFile(File file) throws IOException {
        return BinaryKeysetReader.withInputStream(new FileInputStream(file));
    }

    private BinaryKeysetReader(InputStream stream) {
        this.inputStream = stream;
    }

    @Override
    public Keyset read() throws IOException {
        try {
            Keyset keyset = Keyset.parseFrom(this.inputStream, ExtensionRegistryLite.getEmptyRegistry());
            return keyset;
        }
        finally {
            this.inputStream.close();
        }
    }

    @Override
    public EncryptedKeyset readEncrypted() throws IOException {
        try {
            EncryptedKeyset encryptedKeyset = EncryptedKeyset.parseFrom(this.inputStream, ExtensionRegistryLite.getEmptyRegistry());
            return encryptedKeyset;
        }
        finally {
            this.inputStream.close();
        }
    }
}

