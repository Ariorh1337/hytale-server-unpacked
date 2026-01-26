/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.IoHandle;
import java.nio.channels.SelectableChannel;

public interface NioIoHandle
extends IoHandle {
    public SelectableChannel selectableChannel();
}

