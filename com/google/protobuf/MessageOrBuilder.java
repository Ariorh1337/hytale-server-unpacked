/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.UnknownFieldSet;
import java.util.List;
import java.util.Map;

@CheckReturnValue
public interface MessageOrBuilder
extends MessageLiteOrBuilder {
    @Override
    public Message getDefaultInstanceForType();

    public List<String> findInitializationErrors();

    public String getInitializationErrorString();

    public Descriptors.Descriptor getDescriptorForType();

    public Map<Descriptors.FieldDescriptor, Object> getAllFields();

    public boolean hasOneof(Descriptors.OneofDescriptor var1);

    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor var1);

    public boolean hasField(Descriptors.FieldDescriptor var1);

    public Object getField(Descriptors.FieldDescriptor var1);

    public int getRepeatedFieldCount(Descriptors.FieldDescriptor var1);

    public Object getRepeatedField(Descriptors.FieldDescriptor var1, int var2);

    public UnknownFieldSet getUnknownFields();
}

