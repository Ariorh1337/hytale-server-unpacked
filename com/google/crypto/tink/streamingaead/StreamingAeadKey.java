/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.streamingaead.StreamingAeadParameters;
import javax.annotation.Nullable;

public abstract class StreamingAeadKey
extends Key {
    @Override
    @Nullable
    public final Integer getIdRequirementOrNull() {
        return null;
    }

    @Override
    public abstract StreamingAeadParameters getParameters();
}

