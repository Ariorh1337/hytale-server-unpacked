/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.local;

import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.local.LocalIoHandler;
import java.util.concurrent.ThreadFactory;

@Deprecated
public class LocalEventLoopGroup
extends MultiThreadIoEventLoopGroup {
    public LocalEventLoopGroup() {
        this(0);
    }

    public LocalEventLoopGroup(int nThreads) {
        this(nThreads, (ThreadFactory)null);
    }

    public LocalEventLoopGroup(ThreadFactory threadFactory) {
        this(0, threadFactory);
    }

    public LocalEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        super(nThreads, threadFactory, LocalIoHandler.newFactory());
    }
}

