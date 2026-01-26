/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.IoOps;
import io.netty.channel.nio.NioIoEvent;

public final class NioIoOps
implements IoOps {
    public static final NioIoOps NONE = new NioIoOps(0);
    public static final NioIoOps ACCEPT = new NioIoOps(16);
    public static final NioIoOps CONNECT = new NioIoOps(8);
    public static final NioIoOps WRITE = new NioIoOps(4);
    public static final NioIoOps READ = new NioIoOps(1);
    public static final NioIoOps READ_AND_ACCEPT = new NioIoOps(17);
    public static final NioIoOps READ_AND_WRITE = new NioIoOps(5);
    private static final NioIoEvent[] EVENTS;
    final int value;

    private static void addToArray(NioIoEvent[] array, NioIoOps opt) {
        array[opt.value] = new DefaultNioIoEvent(opt);
    }

    private NioIoOps(int value) {
        this.value = value;
    }

    public boolean contains(NioIoOps ops) {
        return this.isIncludedIn(ops.value);
    }

    public NioIoOps with(NioIoOps ops) {
        if (this.contains(ops)) {
            return this;
        }
        return NioIoOps.valueOf(this.value | ops.value());
    }

    public NioIoOps without(NioIoOps ops) {
        if (!this.contains(ops)) {
            return this;
        }
        return NioIoOps.valueOf(this.value & ~ops.value());
    }

    public int value() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NioIoOps nioOps = (NioIoOps)o;
        return this.value == nioOps.value;
    }

    public int hashCode() {
        return this.value;
    }

    public static NioIoOps valueOf(int value) {
        return NioIoOps.eventOf(value).ops();
    }

    public boolean isIncludedIn(int ops) {
        return (ops & this.value) != 0;
    }

    public boolean isNotIncludedIn(int ops) {
        return (ops & this.value) == 0;
    }

    static NioIoEvent eventOf(int value) {
        NioIoEvent event;
        if (value > 0 && value < EVENTS.length && (event = EVENTS[value]) != null) {
            return event;
        }
        return new DefaultNioIoEvent(new NioIoOps(value));
    }

    static {
        NioIoOps all = new NioIoOps(NioIoOps.NONE.value | NioIoOps.ACCEPT.value | NioIoOps.CONNECT.value | NioIoOps.WRITE.value | NioIoOps.READ.value);
        EVENTS = new NioIoEvent[all.value + 1];
        NioIoOps.addToArray(EVENTS, NONE);
        NioIoOps.addToArray(EVENTS, ACCEPT);
        NioIoOps.addToArray(EVENTS, CONNECT);
        NioIoOps.addToArray(EVENTS, WRITE);
        NioIoOps.addToArray(EVENTS, READ);
        NioIoOps.addToArray(EVENTS, READ_AND_ACCEPT);
        NioIoOps.addToArray(EVENTS, READ_AND_WRITE);
        NioIoOps.addToArray(EVENTS, all);
    }

    private static final class DefaultNioIoEvent
    implements NioIoEvent {
        private final NioIoOps ops;

        DefaultNioIoEvent(NioIoOps ops) {
            this.ops = ops;
        }

        @Override
        public NioIoOps ops() {
            return this.ops;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NioIoEvent event = (NioIoEvent)o;
            return event.ops().equals(this.ops());
        }

        public int hashCode() {
            return this.ops().hashCode();
        }
    }
}

