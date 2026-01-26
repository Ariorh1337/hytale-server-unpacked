/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.netty.handler.codec.http3;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.meta.TypeQualifierDefault;
import org.jetbrains.annotations.NotNull;

@Documented
@TypeQualifierDefault(value={ElementType.PARAMETER, ElementType.METHOD})
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.PACKAGE})
@NotNull
@interface NotNullByDefault {
}

