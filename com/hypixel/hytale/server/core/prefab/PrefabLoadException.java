/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab;

import javax.annotation.Nonnull;

public class PrefabLoadException
extends RuntimeException {
    private Type type;

    public PrefabLoadException(@Nonnull Type type) {
        super(type.name());
        this.type = type;
    }

    public PrefabLoadException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public PrefabLoadException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public PrefabLoadException(Type type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        ERROR,
        NOT_FOUND;

    }
}

