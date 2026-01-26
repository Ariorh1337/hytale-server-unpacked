/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Parameters;
import com.google.errorprone.annotations.Immutable;
import javax.annotation.Nullable;

@Immutable
public abstract class Key {
    public abstract Parameters getParameters();

    @Nullable
    public abstract Integer getIdRequirementOrNull();

    public abstract boolean equalsKey(Key var1);
}

