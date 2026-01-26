/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.IoOps;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoEvent;
import io.netty.channel.epoll.Native;

public final class EpollIoOps
implements IoOps {
    public static final EpollIoOps EPOLLOUT;
    public static final EpollIoOps EPOLLIN;
    public static final EpollIoOps EPOLLERR;
    public static final EpollIoOps EPOLLRDHUP;
    public static final EpollIoOps EPOLLET;
    static final int EPOLL_ERR_OUT_MASK;
    static final int EPOLL_ERR_IN_MASK;
    static final int EPOLL_RDHUP_MASK;
    private static final EpollIoEvent[] EVENTS;
    final int value;

    private static void addToArray(EpollIoEvent[] array, EpollIoOps ops) {
        array[ops.value] = new DefaultEpollIoEvent(ops);
    }

    private EpollIoOps(int value) {
        this.value = value;
    }

    public boolean contains(EpollIoOps ops) {
        return (this.value & ops.value) != 0;
    }

    boolean contains(int value) {
        return (this.value & value) != 0;
    }

    public EpollIoOps with(EpollIoOps ops) {
        if (this.contains(ops)) {
            return this;
        }
        return EpollIoOps.valueOf(this.value | ops.value());
    }

    public EpollIoOps without(EpollIoOps ops) {
        if (!this.contains(ops)) {
            return this;
        }
        return EpollIoOps.valueOf(this.value & ~ops.value());
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
        EpollIoOps nioOps = (EpollIoOps)o;
        return this.value == nioOps.value;
    }

    public int hashCode() {
        return this.value;
    }

    public static EpollIoOps valueOf(int value) {
        return EpollIoOps.eventOf(value).ops();
    }

    public String toString() {
        return "EpollIoOps{value=" + this.value + '}';
    }

    static EpollIoEvent eventOf(int value) {
        EpollIoEvent event;
        if (value > 0 && value < EVENTS.length && (event = EVENTS[value]) != null) {
            return event;
        }
        return new DefaultEpollIoEvent(new EpollIoOps(value));
    }

    static {
        Epoll.ensureAvailability();
        EPOLLOUT = new EpollIoOps(Native.EPOLLOUT);
        EPOLLIN = new EpollIoOps(Native.EPOLLIN);
        EPOLLERR = new EpollIoOps(Native.EPOLLERR);
        EPOLLRDHUP = new EpollIoOps(Native.EPOLLRDHUP);
        EPOLLET = new EpollIoOps(Native.EPOLLET);
        EPOLL_ERR_OUT_MASK = EpollIoOps.EPOLLERR.value | EpollIoOps.EPOLLOUT.value;
        EPOLL_ERR_IN_MASK = EpollIoOps.EPOLLERR.value | EpollIoOps.EPOLLIN.value;
        EPOLL_RDHUP_MASK = EpollIoOps.EPOLLRDHUP.value;
        EpollIoOps all = new EpollIoOps(EpollIoOps.EPOLLOUT.value | EpollIoOps.EPOLLIN.value | EpollIoOps.EPOLLERR.value | EpollIoOps.EPOLLRDHUP.value);
        EVENTS = new EpollIoEvent[all.value + 1];
        EpollIoOps.addToArray(EVENTS, EPOLLOUT);
        EpollIoOps.addToArray(EVENTS, EPOLLIN);
        EpollIoOps.addToArray(EVENTS, EPOLLERR);
        EpollIoOps.addToArray(EVENTS, EPOLLRDHUP);
        EpollIoOps.addToArray(EVENTS, all);
    }

    private static final class DefaultEpollIoEvent
    implements EpollIoEvent {
        private final EpollIoOps ops;

        DefaultEpollIoEvent(EpollIoOps ops) {
            this.ops = ops;
        }

        @Override
        public EpollIoOps ops() {
            return this.ops;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            EpollIoEvent event = (EpollIoEvent)o;
            return event.ops().equals(this.ops());
        }

        public int hashCode() {
            return this.ops().hashCode();
        }

        public String toString() {
            return "DefaultEpollIoEvent{ops=" + this.ops + '}';
        }
    }
}

