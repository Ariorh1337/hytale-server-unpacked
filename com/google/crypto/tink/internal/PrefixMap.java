/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Immutable
public final class PrefixMap<P> {
    private static final Bytes EMPTY_BYTES = Bytes.copyFrom(new byte[0]);
    private final Map<Bytes, List<P>> entries;

    public Iterable<P> getAllWithMatchingPrefix(byte[] text) {
        List<P> fiveByteEntriesOrNull;
        final List<P> zeroByteEntriesOrNull = this.entries.get(EMPTY_BYTES);
        List<P> list = fiveByteEntriesOrNull = text.length >= 5 ? this.entries.get(Bytes.copyFrom(text, 0, 5)) : null;
        if (zeroByteEntriesOrNull == null && fiveByteEntriesOrNull == null) {
            return new ArrayList();
        }
        if (zeroByteEntriesOrNull == null) {
            return fiveByteEntriesOrNull;
        }
        if (fiveByteEntriesOrNull == null) {
            return zeroByteEntriesOrNull;
        }
        return new Iterable<P>(){
            final /* synthetic */ PrefixMap this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public Iterator<P> iterator() {
                return new ConcatenatedIterator(fiveByteEntriesOrNull.iterator(), zeroByteEntriesOrNull.iterator());
            }
        };
    }

    private PrefixMap(Map<Bytes, List<P>> entries) {
        this.entries = entries;
    }

    private static class ConcatenatedIterator<P>
    implements Iterator<P> {
        private final Iterator<P> it0;
        private final Iterator<P> it1;

        private ConcatenatedIterator(Iterator<P> it0, Iterator<P> it1) {
            this.it0 = it0;
            this.it1 = it1;
        }

        @Override
        public boolean hasNext() {
            return this.it0.hasNext() || this.it1.hasNext();
        }

        @Override
        public P next() {
            if (this.it0.hasNext()) {
                return this.it0.next();
            }
            return this.it1.next();
        }
    }

    public static class Builder<P> {
        private final Map<Bytes, List<P>> entries = new HashMap<Bytes, List<P>>();

        @CanIgnoreReturnValue
        public Builder<P> put(Bytes prefix, P primitive) throws GeneralSecurityException {
            List<Object> listForThisPrefix;
            if (prefix.size() != 0 && prefix.size() != 5) {
                throw new GeneralSecurityException("PrefixMap only supports 0 and 5 byte prefixes");
            }
            if (this.entries.containsKey(prefix)) {
                listForThisPrefix = this.entries.get(prefix);
            } else {
                listForThisPrefix = new ArrayList();
                this.entries.put(prefix, listForThisPrefix);
            }
            listForThisPrefix.add(primitive);
            return this;
        }

        public PrefixMap<P> build() {
            return new PrefixMap(this.entries);
        }
    }
}

