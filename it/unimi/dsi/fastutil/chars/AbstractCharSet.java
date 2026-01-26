/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.util.Set;

public abstract class AbstractCharSet
extends AbstractCharCollection
implements Cloneable,
CharSet {
    protected AbstractCharSet() {
    }

    @Override
    public abstract CharIterator iterator();

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        Set s = (Set)o;
        if (s.size() != this.size()) {
            return false;
        }
        if (s instanceof CharSet) {
            return this.containsAll((CharSet)s);
        }
        return this.containsAll(s);
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        CharIterator i = this.iterator();
        while (n-- != 0) {
            char k = i.nextChar();
            h += k;
        }
        return h;
    }

    @Override
    public boolean remove(char k) {
        return super.rem(k);
    }

    @Override
    @Deprecated
    public boolean rem(char k) {
        return this.remove(k);
    }
}

