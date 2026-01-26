/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;

public abstract class BooleanArrayValidator
extends Validator {
    public abstract boolean test(boolean[] var1);

    public abstract String errorMessage(String var1, boolean[] var2);

    public abstract String errorMessage(boolean[] var1);
}

