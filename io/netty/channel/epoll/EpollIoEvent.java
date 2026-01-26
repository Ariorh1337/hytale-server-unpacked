/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.IoEvent;
import io.netty.channel.epoll.EpollIoOps;

public interface EpollIoEvent
extends IoEvent {
    public EpollIoOps ops();
}

