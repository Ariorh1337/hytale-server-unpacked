/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.meta;

import com.hypixel.hytale.registry.Registration;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;

public class BlockStateRegistration
extends Registration {
    private final Class<? extends BlockState> blockStateClass;

    public BlockStateRegistration(Class<? extends BlockState> blockStateClass, BooleanSupplier isEnabled, Runnable unregister) {
        super(isEnabled, unregister);
        this.blockStateClass = blockStateClass;
    }

    public BlockStateRegistration(@Nonnull BlockStateRegistration registration, BooleanSupplier isEnabled, Runnable unregister) {
        super(isEnabled, unregister);
        this.blockStateClass = registration.blockStateClass;
    }

    public Class<? extends BlockState> getBlockStateClass() {
        return this.blockStateClass;
    }

    @Override
    @Nonnull
    public String toString() {
        return "BlockStateRegistration{blockStateClass=" + String.valueOf(this.blockStateClass) + ", " + super.toString() + "}";
    }
}

