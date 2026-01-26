/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.singleplayer;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.protocol.packets.serveraccess.Access;
import javax.annotation.Nonnull;

public class SingleplayerRequestAccessEvent
implements IEvent<Void> {
    private final Access access;

    public SingleplayerRequestAccessEvent(Access access) {
        this.access = access;
    }

    public Access getAccess() {
        return this.access;
    }

    @Nonnull
    public String toString() {
        return "SingleplayerRequestAccessEvent{access=" + String.valueOf((Object)this.access) + "}";
    }
}

