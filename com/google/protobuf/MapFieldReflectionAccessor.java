/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Message;
import java.util.List;

public abstract class MapFieldReflectionAccessor {
    abstract List<Message> getList();

    abstract List<Message> getMutableList();

    abstract Message getMapEntryMessageDefaultInstance();
}

