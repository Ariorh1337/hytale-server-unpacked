/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicEvent;
import java.net.InetSocketAddress;
import java.util.Objects;

public abstract class QuicPathEvent
implements QuicEvent {
    private final InetSocketAddress local;
    private final InetSocketAddress remote;

    QuicPathEvent(InetSocketAddress local, InetSocketAddress remote) {
        this.local = Objects.requireNonNull(local, "local");
        this.remote = Objects.requireNonNull(remote, "remote");
    }

    public InetSocketAddress local() {
        return this.local;
    }

    public InetSocketAddress remote() {
        return this.remote;
    }

    public String toString() {
        return "QuicPathEvent{local=" + this.local + ", remote=" + this.remote + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QuicPathEvent that = (QuicPathEvent)o;
        if (!Objects.equals(this.local, that.local)) {
            return false;
        }
        return Objects.equals(this.remote, that.remote);
    }

    public int hashCode() {
        int result = this.local != null ? this.local.hashCode() : 0;
        result = 31 * result + (this.remote != null ? this.remote.hashCode() : 0);
        return result;
    }

    public static final class PeerMigrated
    extends QuicPathEvent {
        public PeerMigrated(InetSocketAddress local, InetSocketAddress remote) {
            super(local, remote);
        }

        @Override
        public String toString() {
            return "QuicPathEvent.PeerMigrated{local=" + this.local() + ", remote=" + this.remote() + '}';
        }
    }

    public static final class ReusedSourceConnectionId
    extends QuicPathEvent {
        private final long seq;
        private final InetSocketAddress oldLocal;
        private final InetSocketAddress oldRemote;

        public ReusedSourceConnectionId(long seq, InetSocketAddress oldLocal, InetSocketAddress oldRemote, InetSocketAddress local, InetSocketAddress remote) {
            super(local, remote);
            this.seq = seq;
            this.oldLocal = Objects.requireNonNull(oldLocal, "oldLocal");
            this.oldRemote = Objects.requireNonNull(oldRemote, "oldRemote");
        }

        public long seq() {
            return this.seq;
        }

        public InetSocketAddress oldLocal() {
            return this.oldLocal;
        }

        public InetSocketAddress oldRemote() {
            return this.oldRemote;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            ReusedSourceConnectionId that = (ReusedSourceConnectionId)o;
            if (this.seq != that.seq) {
                return false;
            }
            if (!Objects.equals(this.oldLocal, that.oldLocal)) {
                return false;
            }
            return Objects.equals(this.oldRemote, that.oldRemote);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (int)(this.seq ^ this.seq >>> 32);
            result = 31 * result + (this.oldLocal != null ? this.oldLocal.hashCode() : 0);
            result = 31 * result + (this.oldRemote != null ? this.oldRemote.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "QuicPathEvent.ReusedSourceConnectionId{seq=" + this.seq + ", oldLocal=" + this.oldLocal + ", oldRemote=" + this.oldRemote + ", local=" + this.local() + ", remote=" + this.remote() + '}';
        }
    }

    public static final class Closed
    extends QuicPathEvent {
        public Closed(InetSocketAddress local, InetSocketAddress remote) {
            super(local, remote);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode();
        }

        @Override
        public String toString() {
            return "QuicPathEvent.Closed{local=" + this.local() + ", remote=" + this.remote() + '}';
        }
    }

    public static final class FailedValidation
    extends QuicPathEvent {
        public FailedValidation(InetSocketAddress local, InetSocketAddress remote) {
            super(local, remote);
        }

        @Override
        public String toString() {
            return "QuicPathEvent.FailedValidation{local=" + this.local() + ", remote=" + this.remote() + '}';
        }
    }

    public static final class Validated
    extends QuicPathEvent {
        public Validated(InetSocketAddress local, InetSocketAddress remote) {
            super(local, remote);
        }

        @Override
        public String toString() {
            return "QuicPathEvent.Validated{local=" + this.local() + ", remote=" + this.remote() + '}';
        }
    }

    public static final class New
    extends QuicPathEvent {
        public New(InetSocketAddress local, InetSocketAddress remote) {
            super(local, remote);
        }

        @Override
        public String toString() {
            return "QuicPathEvent.New{local=" + this.local() + ", remote=" + this.remote() + '}';
        }
    }
}

