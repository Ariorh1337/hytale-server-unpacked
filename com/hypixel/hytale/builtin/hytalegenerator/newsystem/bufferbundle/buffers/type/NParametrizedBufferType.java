/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type;

import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class NParametrizedBufferType
extends NBufferType {
    @Nonnull
    public final Class parameterClass;

    public NParametrizedBufferType(@Nonnull String name, int index, @Nonnull Class bufferClass, @Nonnull Class parameterClass, @Nonnull Supplier<NBuffer> bufferSupplier) {
        super(name, index, bufferClass, bufferSupplier);
        this.parameterClass = parameterClass;
    }

    public boolean isValidType(@Nonnull Class bufferClass, @Nonnull Class parameterClass) {
        return this.bufferClass.equals(bufferClass) && this.parameterClass.equals(parameterClass);
    }

    @Override
    public boolean isValid(@Nonnull NBuffer buffer) {
        return this.bufferClass.isInstance(buffer);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NParametrizedBufferType)) {
            return false;
        }
        NParametrizedBufferType that = (NParametrizedBufferType)o;
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(this.parameterClass, that.parameterClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.parameterClass);
    }
}

