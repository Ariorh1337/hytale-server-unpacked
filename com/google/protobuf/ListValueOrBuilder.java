/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Value;
import com.google.protobuf.ValueOrBuilder;
import java.util.List;

public interface ListValueOrBuilder
extends MessageOrBuilder {
    public List<Value> getValuesList();

    public Value getValues(int var1);

    public int getValuesCount();

    public List<? extends ValueOrBuilder> getValuesOrBuilderList();

    public ValueOrBuilder getValuesOrBuilder(int var1);
}

