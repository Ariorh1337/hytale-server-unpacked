/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type;

import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NBuffer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class NBufferType {
    public final Class bufferClass;
    public final int index;
    public final Supplier<NBuffer> bufferSupplier;
    public final String name;

    public NBufferType(@Nonnull String name, int index, @Nonnull Class bufferClass, @Nonnull Supplier<NBuffer> bufferSupplier) {
        this.name = name;
        this.index = index;
        this.bufferClass = bufferClass;
        this.bufferSupplier = bufferSupplier;
    }

    public boolean equals(Object o) {
        if (!(o instanceof NBufferType)) {
            return false;
        }
        NBufferType that = (NBufferType)o;
        return this.index == that.index && this.bufferClass.equals(that.bufferClass) && this.bufferSupplier.equals(that.bufferSupplier);
    }

    public boolean isValidType(@Nonnull Class bufferClass) {
        return this.bufferClass.equals(bufferClass);
    }

    public boolean isValid(@Nonnull NBuffer buffer) {
        return this.bufferClass.isInstance(buffer);
    }

    public int hashCode() {
        int result = this.bufferClass.hashCode();
        result = 31 * result + this.index;
        result = 31 * result + this.bufferSupplier.hashCode();
        return result;
    }
}

