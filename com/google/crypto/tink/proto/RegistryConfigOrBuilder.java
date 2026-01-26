/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyTypeEntry;
import com.google.crypto.tink.proto.KeyTypeEntryOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import java.util.List;

@Deprecated
public interface RegistryConfigOrBuilder
extends MessageOrBuilder {
    public String getConfigName();

    public ByteString getConfigNameBytes();

    public List<KeyTypeEntry> getEntryList();

    public KeyTypeEntry getEntry(int var1);

    public int getEntryCount();

    public List<? extends KeyTypeEntryOrBuilder> getEntryOrBuilderList();

    public KeyTypeEntryOrBuilder getEntryOrBuilder(int var1);
}

