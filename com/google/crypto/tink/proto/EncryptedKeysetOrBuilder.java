/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeysetInfo;
import com.google.crypto.tink.proto.KeysetInfoOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EncryptedKeysetOrBuilder
extends MessageOrBuilder {
    public ByteString getEncryptedKeyset();

    public boolean hasKeysetInfo();

    public KeysetInfo getKeysetInfo();

    public KeysetInfoOrBuilder getKeysetInfoOrBuilder();
}

