/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.registry.Registration;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;

public class CommandRegistration
extends Registration {
    @Nonnull
    private final AbstractCommand abstractCommand;

    public CommandRegistration(@Nonnull AbstractCommand command, @Nonnull BooleanSupplier isEnabled, @Nonnull Runnable unregister) {
        super(isEnabled, unregister);
        this.abstractCommand = command;
    }

    public CommandRegistration(@Nonnull CommandRegistration registration, @Nonnull BooleanSupplier isEnabled, @Nonnull Runnable unregister) {
        super(isEnabled, unregister);
        this.abstractCommand = registration.abstractCommand;
    }
}

