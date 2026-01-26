/*
 * Decompiled with CFR 0.152.
 */
package com.google.errorprone.annotations;

import com.google.errorprone.annotations.Modifier;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.ANNOTATION_TYPE})
public @interface IncompatibleModifiers {
    @Deprecated
    public javax.lang.model.element.Modifier[] value() default {};

    public Modifier[] modifier() default {};
}

