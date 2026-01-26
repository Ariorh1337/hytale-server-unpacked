/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.store.StoredCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.LegacyModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public class ChunkColumn
implements Component<ChunkStore> {
    public static final BuilderCodec<ChunkColumn> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ChunkColumn.class, ChunkColumn::new).append(new KeyedCodec<T[]>("Sections", new ArrayCodec<Holder<ChunkStore>>(new StoredCodec<Holder<ChunkStore>>(ChunkStore.HOLDER_CODEC_KEY), Holder[]::new)), (chunk, holders) -> {
        chunk.sectionHolders = holders;
    }, chunk -> {
        Ref<ChunkStore> section;
        int length = chunk.sections.length;
        if (chunk.sectionHolders != null) {
            length = Math.max(chunk.sectionHolders.length, chunk.sections.length);
        }
        Holder[] array = new Holder[length];
        if (chunk.sectionHolders != null) {
            System.arraycopy(chunk.sectionHolders, 0, array, 0, chunk.sectionHolders.length);
        }
        for (int i = 0; i < chunk.sections.length && (section = chunk.sections[i]) != null; ++i) {
            Store<ChunkStore> store = section.getStore();
            array[i] = store.copySerializableEntity(section);
        }
        return array;
    }).add()).build();
    private final Ref<ChunkStore>[] sections = new Ref[10];
    @Nullable
    private Holder<ChunkStore>[] sectionHolders;

    public static ComponentType<ChunkStore, ChunkColumn> getComponentType() {
        return LegacyModule.get().getChunkColumnComponentType();
    }

    public ChunkColumn() {
    }

    public ChunkColumn(Holder<ChunkStore>[] sectionHolders) {
        this.sectionHolders = sectionHolders;
    }

    @Nullable
    public Ref<ChunkStore> getSection(int section) {
        if (section < 0 || section >= this.sections.length) {
            return null;
        }
        return this.sections[section];
    }

    @Nonnull
    public Ref<ChunkStore>[] getSections() {
        return this.sections;
    }

    @Nullable
    public Holder<ChunkStore>[] getSectionHolders() {
        return this.sectionHolders;
    }

    @Nullable
    public Holder<ChunkStore>[] takeSectionHolders() {
        Holder<ChunkStore>[] temp = this.sectionHolders;
        this.sectionHolders = null;
        return temp;
    }

    public void putSectionHolders(Holder<ChunkStore>[] holders) {
        this.sectionHolders = holders;
    }

    @Override
    @Nonnull
    public Component<ChunkStore> clone() {
        int i;
        ChunkColumn newChunk = new ChunkColumn();
        int length = this.sections.length;
        if (this.sectionHolders != null) {
            length = Math.max(this.sectionHolders.length, this.sections.length);
        }
        Holder[] holders = new Holder[length];
        if (this.sectionHolders != null) {
            for (i = 0; i < this.sectionHolders.length; ++i) {
                Holder<ChunkStore> sectionHolder = this.sectionHolders[i];
                if (sectionHolder == null) continue;
                holders[i] = sectionHolder.clone();
            }
        }
        for (i = 0; i < this.sections.length; ++i) {
            Ref<ChunkStore> section = this.sections[i];
            if (section == null) continue;
            holders[i] = section.getStore().copyEntity(section);
        }
        newChunk.sectionHolders = holders;
        return newChunk;
    }

    @Override
    @Nonnull
    public Component<ChunkStore> cloneSerializable() {
        int i;
        ChunkColumn newChunk = new ChunkColumn();
        int length = this.sections.length;
        if (this.sectionHolders != null) {
            length = Math.max(this.sectionHolders.length, this.sections.length);
        }
        Holder[] holders = new Holder[length];
        if (this.sectionHolders != null) {
            for (i = 0; i < this.sectionHolders.length; ++i) {
                Holder<ChunkStore> sectionHolder = this.sectionHolders[i];
                if (sectionHolder == null) continue;
                holders[i] = sectionHolder.clone();
            }
        }
        for (i = 0; i < this.sections.length; ++i) {
            Ref<ChunkStore> section = this.sections[i];
            if (section == null) continue;
            holders[i] = section.getStore().copySerializableEntity(section);
        }
        newChunk.sectionHolders = holders;
        return newChunk;
    }
}

