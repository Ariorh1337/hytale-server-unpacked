/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;

public abstract class IntArrayValidator
extends Validator {
    public abstract boolean test(int[] var1);

    public abstract String errorMessage(int[] var1, String var2);

    public abstract String errorMessage(int[] var1);
}

