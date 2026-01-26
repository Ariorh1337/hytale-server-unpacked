/*
 * Decompiled with CFR 0.152.
 */
package org.bson.internal;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.internal.CycleDetectingCodecRegistry;
import org.bson.internal.LazyCodec;

class ChildCodecRegistry<T>
implements CodecRegistry {
    private final ChildCodecRegistry<?> parent;
    private final CycleDetectingCodecRegistry registry;
    private final Class<T> codecClass;

    ChildCodecRegistry(CycleDetectingCodecRegistry registry, Class<T> codecClass) {
        this.codecClass = codecClass;
        this.parent = null;
        this.registry = registry;
    }

    private ChildCodecRegistry(ChildCodecRegistry<?> parent, Class<T> codecClass) {
        this.parent = parent;
        this.codecClass = codecClass;
        this.registry = parent.registry;
    }

    public Class<T> getCodecClass() {
        return this.codecClass;
    }

    public <U> Codec<U> get(Class<U> clazz) {
        if (this.hasCycles(clazz).booleanValue()) {
            return new LazyCodec<U>(this.registry, clazz);
        }
        return this.registry.get(new ChildCodecRegistry<U>(this, clazz));
    }

    public <U> Codec<U> get(Class<U> clazz, CodecRegistry registry) {
        return this.registry.get(clazz, registry);
    }

    private <U> Boolean hasCycles(Class<U> theClass) {
        ChildCodecRegistry<?> current = this;
        while (current != null) {
            if (current.codecClass.equals(theClass)) {
                return true;
            }
            current = current.parent;
        }
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChildCodecRegistry that = (ChildCodecRegistry)o;
        if (!this.codecClass.equals(that.codecClass)) {
            return false;
        }
        if (this.parent != null ? !this.parent.equals(that.parent) : that.parent != null) {
            return false;
        }
        return this.registry.equals(that.registry);
    }

    public int hashCode() {
        int result = this.parent != null ? this.parent.hashCode() : 0;
        result = 31 * result + this.registry.hashCode();
        result = 31 * result + this.codecClass.hashCode();
        return result;
    }
}

