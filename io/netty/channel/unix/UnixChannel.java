/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.unix;

import io.netty.channel.Channel;
import io.netty.channel.unix.FileDescriptor;

public interface UnixChannel
extends Channel {
    public FileDescriptor fd();
}

