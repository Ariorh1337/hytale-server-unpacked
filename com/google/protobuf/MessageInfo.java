/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.MessageLite;
import com.google.protobuf.ProtoSyntax;

@CheckReturnValue
interface MessageInfo {
    public ProtoSyntax getSyntax();

    public boolean isMessageSetWireFormat();

    public MessageLite getDefaultInstance();
}

