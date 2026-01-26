/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.tinkkey;

import com.google.crypto.tink.KeyTemplate;
import com.google.errorprone.annotations.Immutable;

@Immutable
public interface TinkKey {
    public boolean hasSecret();

    public KeyTemplate getKeyTemplate();
}

