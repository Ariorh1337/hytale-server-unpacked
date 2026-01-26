/*
 * Decompiled with CFR 0.152.
 */
package com.google.errorprone.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface InlineMe {
    public String replacement();

    public String[] imports() default {};

    public String[] staticImports() default {};
}

