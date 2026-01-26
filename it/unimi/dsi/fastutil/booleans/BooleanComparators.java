/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import java.io.Serializable;
import java.util.Comparator;

public final class BooleanComparators {
    public static final BooleanComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final BooleanComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private BooleanComparators() {
    }

    public static BooleanComparator oppositeComparator(BooleanComparator c) {
        if (c instanceof OppositeComparator) {
            return ((OppositeComparator)c).comparator;
        }
        return new OppositeComparator(c);
    }

    public static BooleanComparator asBooleanComparator(final Comparator<? super Boolean> c) {
        if (c == null || c instanceof BooleanComparator) {
            return (BooleanComparator)c;
        }
        return new BooleanComparator(){

            @Override
            public int compare(boolean x, boolean y) {
                return c.compare(x, y);
            }

            @Override
            public int compare(Boolean x, Boolean y) {
                return c.compare(x, y);
            }
        };
    }

    protected static class OppositeComparator
    implements BooleanComparator,
    Serializable {
        private static final long serialVersionUID = 1L;
        final BooleanComparator comparator;

        protected OppositeComparator(BooleanComparator c) {
            this.comparator = c;
        }

        @Override
        public final int compare(boolean a, boolean b) {
            return this.comparator.compare(b, a);
        }

        @Override
        public final BooleanComparator reversed() {
            return this.comparator;
        }
    }

    protected static class NaturalImplicitComparator
    implements BooleanComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        @Override
        public final int compare(boolean a, boolean b) {
            return Boolean.compare(a, b);
        }

        @Override
        public BooleanComparator reversed() {
            return OPPOSITE_COMPARATOR;
        }

        private Object readResolve() {
            return NATURAL_COMPARATOR;
        }
    }

    protected static class OppositeImplicitComparator
    implements BooleanComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        @Override
        public final int compare(boolean a, boolean b) {
            return -Boolean.compare(a, b);
        }

        @Override
        public BooleanComparator reversed() {
            return NATURAL_COMPARATOR;
        }

        private Object readResolve() {
            return OPPOSITE_COMPARATOR;
        }
    }
}

