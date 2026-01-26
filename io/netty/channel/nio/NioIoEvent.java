/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.IoEvent;
import io.netty.channel.nio.NioIoOps;

public interface NioIoEvent
extends IoEvent {
    public NioIoOps ops();
}

