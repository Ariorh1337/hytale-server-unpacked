/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io;

import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.protocol.Hitbox;
import com.hypixel.hytale.server.core.io.NetworkSerializer;

public interface NetworkSerializers {
    public static final NetworkSerializer<Box, Hitbox> BOX = t -> {
        Hitbox packet = new Hitbox();
        packet.minX = (float)t.getMin().getX();
        packet.minY = (float)t.getMin().getY();
        packet.minZ = (float)t.getMin().getZ();
        packet.maxX = (float)t.getMax().getX();
        packet.maxY = (float)t.getMax().getY();
        packet.maxZ = (float)t.getMax().getZ();
        return packet;
    };
}

