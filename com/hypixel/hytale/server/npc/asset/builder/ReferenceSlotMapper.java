/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.hypixel.hytale.server.npc.asset.builder.SlotMapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ReferenceSlotMapper<T>
extends SlotMapper {
    private final List<T> list = new ObjectArrayList<T>();
    private final Supplier<T> slotSupplier;

    public ReferenceSlotMapper(Supplier<T> slotSupplier) {
        this.slotSupplier = slotSupplier;
    }

    public ReferenceSlotMapper(Supplier<T> slotSupplier, boolean trackNames) {
        super(trackNames);
        this.slotSupplier = slotSupplier;
    }

    public T getReference(String name) {
        int slot = this.getSlot(name);
        if (slot < this.list.size()) {
            return this.list.get(slot);
        }
        T object = this.slotSupplier.get();
        this.list.add(object);
        return object;
    }

    public List<T> getReferenceList() {
        return this.list;
    }
}

