/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;

public abstract class AbstractCharSortedSet
extends AbstractCharSet
implements CharSortedSet {
    protected AbstractCharSortedSet() {
    }

    @Override
    public abstract CharBidirectionalIterator iterator();
}

