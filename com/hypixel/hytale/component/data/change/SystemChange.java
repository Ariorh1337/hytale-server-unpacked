/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.data.change;

import com.hypixel.hytale.component.data.change.ChangeType;
import com.hypixel.hytale.component.data.change.DataChange;
import com.hypixel.hytale.component.system.ISystem;
import javax.annotation.Nonnull;

public class SystemChange<ECS_TYPE>
implements DataChange {
    private final ChangeType type;
    private final ISystem<ECS_TYPE> system;

    public SystemChange(ChangeType type, ISystem<ECS_TYPE> system) {
        this.type = type;
        this.system = system;
    }

    public ChangeType getType() {
        return this.type;
    }

    public ISystem<ECS_TYPE> getSystem() {
        return this.system;
    }

    @Nonnull
    public String toString() {
        return "SystemChange{type=" + String.valueOf((Object)this.type) + ", system=" + String.valueOf(this.system) + "}";
    }
}

