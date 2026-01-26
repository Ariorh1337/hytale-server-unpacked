/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.IoHandle;
import io.netty.channel.unix.FileDescriptor;

public interface EpollIoHandle
extends IoHandle {
    public FileDescriptor fd();
}

