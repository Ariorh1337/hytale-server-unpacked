/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.CleanableDirectBuffer;
import java.nio.ByteBuffer;

interface Cleaner {
    public CleanableDirectBuffer allocate(int var1);

    @Deprecated
    public void freeDirectBuffer(ByteBuffer var1);
}

