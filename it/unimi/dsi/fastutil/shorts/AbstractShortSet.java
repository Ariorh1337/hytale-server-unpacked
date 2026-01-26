/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.Set;

public abstract class AbstractShortSet
extends AbstractShortCollection
implements Cloneable,
ShortSet {
    protected AbstractShortSet() {
    }

    @Override
    public abstract ShortIterator iterator();

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
        if (s instanceof ShortSet) {
            return this.containsAll((ShortSet)s);
        }
        return this.containsAll(s);
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ShortIterator i = this.iterator();
        while (n-- != 0) {
            short k = i.nextShort();
            h += k;
        }
        return h;
    }

    @Override
    public boolean remove(short k) {
        return super.rem(k);
    }

    @Override
    @Deprecated
    public boolean rem(short k) {
        return this.remove(k);
    }
}

