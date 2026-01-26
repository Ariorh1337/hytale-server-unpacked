/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeysetInfo;
import com.google.protobuf.MessageOrBuilder;
import java.util.List;

public interface KeysetInfoOrBuilder
extends MessageOrBuilder {
    public int getPrimaryKeyId();

    public List<KeysetInfo.KeyInfo> getKeyInfoList();

    public KeysetInfo.KeyInfo getKeyInfo(int var1);

    public int getKeyInfoCount();

    public List<? extends KeysetInfo.KeyInfoOrBuilder> getKeyInfoOrBuilderList();

    public KeysetInfo.KeyInfoOrBuilder getKeyInfoOrBuilder(int var1);
}

