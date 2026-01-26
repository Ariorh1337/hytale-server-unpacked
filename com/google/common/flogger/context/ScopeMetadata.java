/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.context;

import com.google.common.flogger.MetadataKey;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.util.Checks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class ScopeMetadata
extends Metadata {
    public static Builder builder() {
        return new Builder();
    }

    public static <T> ScopeMetadata singleton(MetadataKey<T> key, T value) {
        return new SingletonMetadata(key, value);
    }

    public static ScopeMetadata none() {
        return EmptyMetadata.INSTANCE;
    }

    private ScopeMetadata() {
    }

    public abstract ScopeMetadata concatenate(ScopeMetadata var1);

    abstract Entry<?> get(int var1);

    @Override
    public MetadataKey<?> getKey(int n) {
        return this.get((int)n).key;
    }

    @Override
    public Object getValue(int n) {
        return this.get((int)n).value;
    }

    private static final class EmptyMetadata
    extends ScopeMetadata {
        static final ScopeMetadata INSTANCE = new EmptyMetadata();

        private EmptyMetadata() {
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        Entry<?> get(int n) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        @NullableDecl
        public <T> T findValue(MetadataKey<T> key) {
            Checks.checkArgument(!key.canRepeat(), "metadata key must be single valued");
            return null;
        }

        @Override
        public ScopeMetadata concatenate(ScopeMetadata metadata) {
            return metadata;
        }
    }

    private static final class SingletonMetadata
    extends ScopeMetadata {
        private final Entry<?> entry;

        <T> SingletonMetadata(MetadataKey<T> key, T value) {
            this.entry = new Entry<T>(key, value);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        Entry<?> get(int n) {
            if (n == 0) {
                return this.entry;
            }
            throw new IndexOutOfBoundsException();
        }

        @NullableDecl
        public <R> R findValue(MetadataKey<R> key) {
            Checks.checkArgument(!key.canRepeat(), "metadata key must be single valued");
            return this.entry.key.equals(key) ? (R)this.entry.value : null;
        }

        @Override
        public ScopeMetadata concatenate(ScopeMetadata metadata) {
            int extraSize = metadata.size();
            if (extraSize == 0) {
                return this;
            }
            Entry[] merged = new Entry[extraSize + 1];
            merged[0] = this.entry;
            for (int i = 0; i < extraSize; ++i) {
                merged[i + 1] = metadata.get(i);
            }
            return new ImmutableScopeMetadata(merged);
        }
    }

    private static final class ImmutableScopeMetadata
    extends ScopeMetadata {
        private final Entry<?>[] entries;

        ImmutableScopeMetadata(Entry<?>[] entries) {
            this.entries = entries;
        }

        @Override
        public int size() {
            return this.entries.length;
        }

        @Override
        Entry<?> get(int n) {
            return this.entries[n];
        }

        @Override
        @NullableDecl
        public <T> T findValue(MetadataKey<T> key) {
            Checks.checkArgument(!key.canRepeat(), "metadata key must be single valued");
            for (int n = this.entries.length - 1; n >= 0; --n) {
                Entry<?> e = this.entries[n];
                if (!e.key.equals(key)) continue;
                return e.value;
            }
            return null;
        }

        @Override
        public ScopeMetadata concatenate(ScopeMetadata metadata) {
            int extraSize = metadata.size();
            if (extraSize == 0) {
                return this;
            }
            if (this.entries.length == 0) {
                return metadata;
            }
            Entry<?>[] merged = Arrays.copyOf(this.entries, this.entries.length + extraSize);
            for (int i = 0; i < extraSize; ++i) {
                merged[i + this.entries.length] = metadata.get(i);
            }
            return new ImmutableScopeMetadata(merged);
        }
    }

    public static final class Builder {
        private static final Entry<?>[] EMPTY_ARRAY = new Entry[0];
        private final List<Entry<?>> entries = new ArrayList(2);

        private Builder() {
        }

        public <T> Builder add(MetadataKey<T> key, T value) {
            this.entries.add(new Entry<T>(key, value));
            return this;
        }

        public ScopeMetadata build() {
            return new ImmutableScopeMetadata(this.entries.toArray(EMPTY_ARRAY));
        }
    }

    private static final class Entry<T> {
        final MetadataKey<T> key;
        final T value;

        Entry(MetadataKey<T> key, T value) {
            this.key = Checks.checkNotNull(key, "key");
            this.value = Checks.checkNotNull(value, "value");
        }
    }
}

