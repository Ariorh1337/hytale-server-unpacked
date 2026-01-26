/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import java.util.concurrent.Executor;

public interface ThreadAwareExecutor
extends Executor {
    public boolean isExecutorThread(Thread var1);
}

