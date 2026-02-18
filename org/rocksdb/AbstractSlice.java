/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksMutableObject;

public abstract class AbstractSlice<T>
extends RocksMutableObject {
    protected AbstractSlice() {
    }

    protected AbstractSlice(long l) {
        super(l);
    }

    public T data() {
        return this.data0(this.getNativeHandle());
    }

    protected abstract T data0(long var1);

    public abstract void removePrefix(int var1);

    public abstract void clear();

    public int size() {
        return AbstractSlice.size0(this.getNativeHandle());
    }

    public boolean empty() {
        return AbstractSlice.empty0(this.getNativeHandle());
    }

    public String toString(boolean bl) {
        return AbstractSlice.toString0(this.getNativeHandle(), bl);
    }

    public String toString() {
        return this.toString(false);
    }

    public int compare(AbstractSlice<?> abstractSlice) {
        assert (abstractSlice != null);
        if (this.isOwningHandle() && abstractSlice.isOwningHandle()) {
            return AbstractSlice.compare0(this.getNativeHandle(), abstractSlice.getNativeHandle());
        }
        if (!this.isOwningHandle() && !abstractSlice.isOwningHandle()) {
            return 0;
        }
        if (this.isOwningHandle()) {
            return 1;
        }
        return -1;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof AbstractSlice) {
            return this.compare((AbstractSlice)object) == 0;
        }
        return false;
    }

    public boolean startsWith(AbstractSlice<?> abstractSlice) {
        if (abstractSlice != null) {
            return AbstractSlice.startsWith0(this.getNativeHandle(), abstractSlice.getNativeHandle());
        }
        return false;
    }

    protected static native long createNewSliceFromString(String var0);

    private static native int size0(long var0);

    private static native boolean empty0(long var0);

    private static native String toString0(long var0, boolean var2);

    private static native int compare0(long var0, long var2);

    private static native boolean startsWith0(long var0, long var2);

    @Override
    protected final void disposeInternal(long l) {
        AbstractSlice.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

