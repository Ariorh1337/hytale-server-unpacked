/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatComparator;
import java.io.Serializable;
import java.util.Comparator;

public final class FloatComparators {
    public static final FloatComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final FloatComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private FloatComparators() {
    }

    public static FloatComparator oppositeComparator(FloatComparator c) {
        if (c instanceof OppositeComparator) {
            return ((OppositeComparator)c).comparator;
        }
        return new OppositeComparator(c);
    }

    public static FloatComparator asFloatComparator(final Comparator<? super Float> c) {
        if (c == null || c instanceof FloatComparator) {
            return (FloatComparator)c;
        }
        return new FloatComparator(){

            @Override
            public int compare(float x, float y) {
                return c.compare(Float.valueOf(x), Float.valueOf(y));
            }

            @Override
            public int compare(Float x, Float y) {
                return c.compare(x, y);
            }
        };
    }

    protected static class OppositeComparator
    implements FloatComparator,
    Serializable {
        private static final long serialVersionUID = 1L;
        final FloatComparator comparator;

        protected OppositeComparator(FloatComparator c) {
            this.comparator = c;
        }

        @Override
        public final int compare(float a, float b) {
            return this.comparator.compare(b, a);
        }

        @Override
        public final FloatComparator reversed() {
            return this.comparator;
        }
    }

    protected static class NaturalImplicitComparator
    implements FloatComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        @Override
        public final int compare(float a, float b) {
            return Float.compare(a, b);
        }

        @Override
        public FloatComparator reversed() {
            return OPPOSITE_COMPARATOR;
        }

        private Object readResolve() {
            return NATURAL_COMPARATOR;
        }
    }

    protected static class OppositeImplicitComparator
    implements FloatComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        @Override
        public final int compare(float a, float b) {
            return -Float.compare(a, b);
        }

        @Override
        public FloatComparator reversed() {
            return NATURAL_COMPARATOR;
        }

        private Object readResolve() {
            return OPPOSITE_COMPARATOR;
        }
    }
}

