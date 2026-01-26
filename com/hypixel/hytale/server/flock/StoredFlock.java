/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.flock;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.store.StoredCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StoredFlock {
    public static final BuilderCodec<StoredFlock> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(StoredFlock.class, StoredFlock::new).append(new KeyedCodec<T[]>("Members", new ArrayCodec<Holder<EntityStore>>(new StoredCodec<Holder<EntityStore>>(EntityStore.HOLDER_CODEC_KEY), Holder[]::new)), (flock, array) -> {
        flock.members = array;
    }, flock -> flock.members).add()).build();
    @Nullable
    private Holder<EntityStore>[] members;

    public void storeNPCs(@Nonnull List<Ref<EntityStore>> refs, @Nonnull Store<EntityStore> store) {
        ComponentRegistry.Data<EntityStore> data = EntityStore.REGISTRY.getData();
        ObjectArrayList<Holder<EntityStore>> members = new ObjectArrayList<Holder<EntityStore>>();
        for (int i = 0; i < refs.size(); ++i) {
            Holder<EntityStore> holder;
            Ref<EntityStore> ref = refs.get(i);
            if (!ref.isValid() || !(holder = store.removeEntity(ref, RemoveReason.UNLOAD)).hasSerializableComponents(data)) continue;
            members.add(holder);
        }
        this.members = (Holder[])members.toArray(Holder[]::new);
    }

    public boolean hasStoredNPCs() {
        return this.members != null;
    }

    public void restoreNPCs(@Nonnull List<Ref<EntityStore>> output, @Nonnull Store<EntityStore> store) {
        for (Holder<EntityStore> member : this.members) {
            Ref<EntityStore> ref = store.addEntity(member, AddReason.LOAD);
            if (ref == null) {
                SpawningPlugin.get().getLogger().at(Level.WARNING).log("Failed to restore stored spawn marker member! " + String.valueOf(member));
                continue;
            }
            output.add(ref);
        }
        this.clear();
    }

    public void clear() {
        this.members = null;
    }

    @Nonnull
    public String toString() {
        return "StoredFlock{, members=" + Arrays.toString(this.members) + "}";
    }

    @Nonnull
    public StoredFlock clone() {
        StoredFlock storedFlock = new StoredFlock();
        if (this.members != null) {
            Holder[] newMembers = new Holder[this.members.length];
            for (int i = 0; i < newMembers.length; ++i) {
                newMembers[i] = this.members[i].clone();
            }
            storedFlock.members = newMembers;
        }
        return storedFlock;
    }
}

