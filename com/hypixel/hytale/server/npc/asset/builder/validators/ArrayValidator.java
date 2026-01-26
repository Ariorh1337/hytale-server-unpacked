/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectArrayHelper;
import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;

public abstract class ArrayValidator
extends Validator {
    public abstract boolean test(BuilderObjectArrayHelper<?, ?> var1);

    public abstract String errorMessage(String var1, BuilderObjectArrayHelper<?, ?> var2);

    public abstract String errorMessage(BuilderObjectArrayHelper<?, ?> var1);
}

