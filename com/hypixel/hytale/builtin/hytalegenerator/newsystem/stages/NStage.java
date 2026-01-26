/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public interface NStage {
    public void run(@Nonnull Context var1);

    @Nonnull
    public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid();

    @Nonnull
    public List<NBufferType> getOutputTypes();

    @Nonnull
    public String getName();

    public static final class Context {
        @Nonnull
        public Map<NBufferType, NBufferBundle.Access.View> bufferAccess;
        @Nonnull
        public WorkerIndexer.Id workerId;

        public Context(@Nonnull Map<NBufferType, NBufferBundle.Access.View> bufferAccess, @Nonnull WorkerIndexer.Id workerId) {
            this.bufferAccess = bufferAccess;
            this.workerId = workerId;
        }
    }
}

