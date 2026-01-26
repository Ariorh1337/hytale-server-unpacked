/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.local;

import io.netty.channel.IoHandle;

interface LocalIoHandle
extends IoHandle {
    public void closeNow();
}

