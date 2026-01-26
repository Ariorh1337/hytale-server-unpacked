/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io;

@FunctionalInterface
public interface NetworkSerializer<Type, Packet> {
    public Packet toPacket(Type var1);
}

