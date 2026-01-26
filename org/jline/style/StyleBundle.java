/*
 * Decompiled with CFR 0.152.
 */
package org.jline.style;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface StyleBundle {

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD})
    @Documented
    public static @interface DefaultStyle {
        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD})
    @Documented
    public static @interface StyleName {
        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    @Documented
    public static @interface StyleGroup {
        public String value();
    }
}

