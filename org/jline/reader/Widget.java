/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import org.jline.reader.Binding;

@FunctionalInterface
public interface Widget
extends Binding {
    public boolean apply();
}

