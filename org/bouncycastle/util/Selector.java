/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public interface Selector<T>
extends Cloneable {
    public boolean match(T var1);

    public Object clone();
}

