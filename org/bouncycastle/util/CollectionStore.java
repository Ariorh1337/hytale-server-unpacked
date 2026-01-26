/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class CollectionStore<T>
implements Store<T>,
Iterable<T> {
    private Collection<T> _local;

    public CollectionStore(Collection<T> collection) {
        this._local = new ArrayList<T>(collection);
    }

    @Override
    public Collection<T> getMatches(Selector<T> selector) {
        if (selector == null) {
            return new ArrayList<T>(this._local);
        }
        ArrayList<T> arrayList = new ArrayList<T>();
        for (T t : this._local) {
            if (!selector.match(t)) continue;
            arrayList.add(t);
        }
        return arrayList;
    }

    @Override
    public Iterator<T> iterator() {
        return this.getMatches(null).iterator();
    }
}

