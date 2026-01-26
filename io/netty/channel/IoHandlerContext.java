/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

public interface IoHandlerContext {
    public boolean canBlock();

    public long delayNanos(long var1);

    public long deadlineNanos();

    default public void reportActiveIoTime(long activeNanos) {
    }

    default public boolean shouldReportActiveIoTime() {
        return false;
    }
}

