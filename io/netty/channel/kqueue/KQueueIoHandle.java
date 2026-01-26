/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.kqueue;

import io.netty.channel.IoHandle;

public interface KQueueIoHandle
extends IoHandle {
    public int ident();
}

