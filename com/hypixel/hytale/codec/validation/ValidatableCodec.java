/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.validation;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.WrappedCodec;
import java.util.Set;

public interface ValidatableCodec<T>
extends Codec<T> {
    public void validate(T var1, ExtraInfo var2);

    public void validateDefaults(ExtraInfo var1, Set<Codec<?>> var2);

    public static void validateDefaults(Codec<?> codec, ExtraInfo extraInfo, Set<Codec<?>> tested) {
        do {
            if (!(codec instanceof WrappedCodec)) {
                if (!(codec instanceof ValidatableCodec)) break;
                ValidatableCodec validatableCodec = (ValidatableCodec)codec;
                validatableCodec.validateDefaults(extraInfo, tested);
                break;
            }
            WrappedCodec wrappedCodec = (WrappedCodec)((Object)codec);
            codec = wrappedCodec.getChildCodec();
        } while (codec != null);
    }
}

