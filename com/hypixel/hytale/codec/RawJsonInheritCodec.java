/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.RawJsonCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import javax.annotation.Nullable;

public interface RawJsonInheritCodec<T>
extends RawJsonCodec<T> {
    @Nullable
    public T decodeAndInheritJson(RawJsonReader var1, T var2, ExtraInfo var3) throws IOException;

    public void decodeAndInheritJson(RawJsonReader var1, T var2, T var3, ExtraInfo var4) throws IOException;
}

