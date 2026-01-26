/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.IoOps;

public interface IoRegistration {
    public <T> T attachment();

    public long submit(IoOps var1);

    public boolean isValid();

    public boolean cancel();
}

