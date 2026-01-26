/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.function.supplier;

import com.hypixel.hytale.function.supplier.CachedSupplier;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class SupplierUtil {
    @Nonnull
    public static <T> CachedSupplier<T> cache(Supplier<T> delegate) {
        return new CachedSupplier<T>(delegate);
    }
}

