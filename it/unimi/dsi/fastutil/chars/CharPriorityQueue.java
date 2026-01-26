/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.chars.CharComparator;

public interface CharPriorityQueue
extends PriorityQueue<Character> {
    @Override
    public void enqueue(char var1);

    public char dequeueChar();

    public char firstChar();

    default public char lastChar() {
        throw new UnsupportedOperationException();
    }

    public CharComparator comparator();

    @Override
    @Deprecated
    default public void enqueue(Character x) {
        this.enqueue(x.charValue());
    }

    @Override
    @Deprecated
    default public Character dequeue() {
        return Character.valueOf(this.dequeueChar());
    }

    @Override
    @Deprecated
    default public Character first() {
        return Character.valueOf(this.firstChar());
    }

    @Override
    @Deprecated
    default public Character last() {
        return Character.valueOf(this.lastChar());
    }
}

