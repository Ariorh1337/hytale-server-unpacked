/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.MessageInfo;
import com.google.protobuf.MessageLite;
import com.google.protobuf.ProtoSyntax;

@CheckReturnValue
final class RawMessageInfo
implements MessageInfo {
    private static final int IS_PROTO2_BIT = 1;
    private static final int IS_EDITION_BIT = 4;
    private final MessageLite defaultInstance;
    private final String info;
    private final Object[] objects;
    private final int flags;

    RawMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
        this.defaultInstance = defaultInstance;
        this.info = info;
        this.objects = objects;
        int position = 0;
        char value = info.charAt(position++);
        if (value < '\ud800') {
            this.flags = value;
        } else {
            int result = value & 0x1FFF;
            int shift = 13;
            while ((value = info.charAt(position++)) >= '\ud800') {
                result |= (value & 0x1FFF) << shift;
                shift += 13;
            }
            this.flags = result | value << shift;
        }
    }

    String getStringInfo() {
        return this.info;
    }

    Object[] getObjects() {
        return this.objects;
    }

    @Override
    public MessageLite getDefaultInstance() {
        return this.defaultInstance;
    }

    @Override
    public ProtoSyntax getSyntax() {
        if ((this.flags & 1) != 0) {
            return ProtoSyntax.PROTO2;
        }
        if ((this.flags & 4) == 4) {
            return ProtoSyntax.EDITIONS;
        }
        return ProtoSyntax.PROTO3;
    }

    @Override
    public boolean isMessageSetWireFormat() {
        return (this.flags & 2) == 2;
    }
}

