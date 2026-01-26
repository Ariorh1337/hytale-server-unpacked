/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;

public class ComponentOnlyValidator
extends Validator {
    public static final ComponentOnlyValidator INSTANCE = new ComponentOnlyValidator();

    private ComponentOnlyValidator() {
    }

    public static ComponentOnlyValidator get() {
        return INSTANCE;
    }
}

