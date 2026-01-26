/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.validation;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;

public interface LateValidator<T>
extends Validator<T> {
    public void acceptLate(T var1, ValidationResults var2, ExtraInfo var3);
}

