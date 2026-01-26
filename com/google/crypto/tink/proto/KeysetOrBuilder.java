/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Keyset;
import com.google.protobuf.MessageOrBuilder;
import java.util.List;

public interface KeysetOrBuilder
extends MessageOrBuilder {
    public int getPrimaryKeyId();

    public List<Keyset.Key> getKeyList();

    public Keyset.Key getKey(int var1);

    public int getKeyCount();

    public List<? extends Keyset.KeyOrBuilder> getKeyOrBuilderList();

    public Keyset.KeyOrBuilder getKeyOrBuilder(int var1);
}

