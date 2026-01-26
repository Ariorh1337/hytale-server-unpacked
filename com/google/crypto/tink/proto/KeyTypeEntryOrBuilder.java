/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

@Deprecated
public interface KeyTypeEntryOrBuilder
extends MessageOrBuilder {
    public String getPrimitiveName();

    public ByteString getPrimitiveNameBytes();

    public String getTypeUrl();

    public ByteString getTypeUrlBytes();

    public int getKeyManagerVersion();

    public boolean getNewKeyAllowed();

    public String getCatalogueName();

    public ByteString getCatalogueNameBytes();
}

