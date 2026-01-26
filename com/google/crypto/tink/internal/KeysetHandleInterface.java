/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;

public interface KeysetHandleInterface {
    public Entry getPrimary();

    public int size();

    public Entry getAt(int var1);

    public static interface Entry {
        public Key getKey();

        public KeyStatus getStatus();

        public int getId();

        public boolean isPrimary();
    }
}

