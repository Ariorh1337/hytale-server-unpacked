/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;

public interface ProtocolMessageEnum
extends Internal.EnumLite {
    @Override
    public int getNumber();

    public Descriptors.EnumValueDescriptor getValueDescriptor();

    public Descriptors.EnumDescriptor getDescriptorForType();
}

