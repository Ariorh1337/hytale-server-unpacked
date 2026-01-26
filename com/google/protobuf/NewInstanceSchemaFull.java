/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Message;
import com.google.protobuf.NewInstanceSchema;

final class NewInstanceSchemaFull
implements NewInstanceSchema {
    NewInstanceSchemaFull() {
    }

    @Override
    public Object newInstance(Object defaultInstance) {
        return ((Message)defaultInstance).toBuilder().buildPartial();
    }
}

