/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public class Objects {
    public static boolean areEqual(Object object, Object object2) {
        return object == object2 || null != object && null != object2 && object.equals(object2);
    }

    public static int hashCode(Object object) {
        return null == object ? 0 : object.hashCode();
    }
}

