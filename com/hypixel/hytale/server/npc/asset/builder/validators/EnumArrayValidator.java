/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;

public abstract class EnumArrayValidator
extends Validator {
    public abstract <T extends Enum<T>> boolean test(T[] var1, Class<T> var2);

    public abstract <T extends Enum<T>> String errorMessage(String var1, T[] var2);
}

