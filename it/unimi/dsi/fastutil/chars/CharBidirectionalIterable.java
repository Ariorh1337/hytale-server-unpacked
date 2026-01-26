/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharIterable;

public interface CharBidirectionalIterable
extends CharIterable {
    @Override
    public CharBidirectionalIterator iterator();
}

