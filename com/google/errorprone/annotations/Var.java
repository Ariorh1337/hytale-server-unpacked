/*
 * Decompiled with CFR 0.152.
 */
package com.google.errorprone.annotations;

import com.google.errorprone.annotations.IncompatibleModifiers;
import com.google.errorprone.annotations.Modifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Retention(value=RetentionPolicy.RUNTIME)
@IncompatibleModifiers(modifier={Modifier.FINAL})
public @interface Var {
}

