/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.util.AsciiString;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.Nullable;

final class QuicClientSessionCache {
    private static final int DEFAULT_CACHE_SIZE;
    private final AtomicInteger maximumCacheSize = new AtomicInteger(DEFAULT_CACHE_SIZE);
    private final AtomicInteger sessionTimeout = new AtomicInteger(300);
    private int sessionCounter;
    private final Map<HostPort, SessionHolder> sessions = new LinkedHashMap<HostPort, SessionHolder>(){
        private static final long serialVersionUID = -7773696788135734448L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<HostPort, SessionHolder> eldest) {
            int maxSize = QuicClientSessionCache.this.maximumCacheSize.get();
            return maxSize >= 0 && this.size() > maxSize;
        }
    };

    QuicClientSessionCache() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void saveSession(@Nullable String host, int port, long creationTime, long timeout, byte[] session, boolean isSingleUse) {
        HostPort hostPort = QuicClientSessionCache.keyFor(host, port);
        if (hostPort != null) {
            Map<HostPort, SessionHolder> map = this.sessions;
            synchronized (map) {
                if (++this.sessionCounter == 255) {
                    this.sessionCounter = 0;
                    this.expungeInvalidSessions();
                }
                this.sessions.put(hostPort, new SessionHolder(creationTime, timeout, session, isSingleUse));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean hasSession(@Nullable String host, int port) {
        HostPort hostPort = QuicClientSessionCache.keyFor(host, port);
        if (hostPort != null) {
            Map<HostPort, SessionHolder> map = this.sessions;
            synchronized (map) {
                return this.sessions.containsKey(hostPort);
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    byte @Nullable [] getSession(@Nullable String host, int port) {
        HostPort hostPort = QuicClientSessionCache.keyFor(host, port);
        if (hostPort != null) {
            SessionHolder sessionHolder;
            Map<HostPort, SessionHolder> map = this.sessions;
            synchronized (map) {
                sessionHolder = this.sessions.get(hostPort);
                if (sessionHolder == null) {
                    return null;
                }
                if (sessionHolder.isSingleUse()) {
                    this.sessions.remove(hostPort);
                }
            }
            if (sessionHolder.isValid()) {
                return sessionHolder.sessionBytes();
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeSession(@Nullable String host, int port) {
        HostPort hostPort = QuicClientSessionCache.keyFor(host, port);
        if (hostPort != null) {
            Map<HostPort, SessionHolder> map = this.sessions;
            synchronized (map) {
                this.sessions.remove(hostPort);
            }
        }
    }

    void setSessionTimeout(int seconds) {
        int oldTimeout = this.sessionTimeout.getAndSet(seconds);
        if (oldTimeout > seconds) {
            this.clear();
        }
    }

    int getSessionTimeout() {
        return this.sessionTimeout.get();
    }

    void setSessionCacheSize(int size) {
        long oldSize = this.maximumCacheSize.getAndSet(size);
        if (oldSize > (long)size || size == 0) {
            this.clear();
        }
    }

    int getSessionCacheSize() {
        return this.maximumCacheSize.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clear() {
        Map<HostPort, SessionHolder> map = this.sessions;
        synchronized (map) {
            this.sessions.clear();
        }
    }

    private void expungeInvalidSessions() {
        SessionHolder sessionHolder;
        assert (Thread.holdsLock(this.sessions));
        if (this.sessions.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<HostPort, SessionHolder>> iterator = this.sessions.entrySet().iterator();
        while (iterator.hasNext() && !(sessionHolder = iterator.next().getValue()).isValid(now)) {
            iterator.remove();
        }
    }

    @Nullable
    private static HostPort keyFor(@Nullable String host, int port) {
        if (host == null && port < 1) {
            return null;
        }
        return new HostPort(host, port);
    }

    static {
        int cacheSize = SystemPropertyUtil.getInt("javax.net.ssl.sessionCacheSize", 20480);
        DEFAULT_CACHE_SIZE = cacheSize >= 0 ? cacheSize : 20480;
    }

    private static final class HostPort {
        private final int hash;
        private final String host;
        private final int port;

        HostPort(@Nullable String host, int port) {
            this.host = host;
            this.port = port;
            this.hash = 31 * AsciiString.hashCode(host) + port;
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof HostPort)) {
                return false;
            }
            HostPort other = (HostPort)obj;
            return this.port == other.port && this.host.equalsIgnoreCase(other.host);
        }

        public String toString() {
            return "HostPort{host='" + this.host + '\'' + ", port=" + this.port + '}';
        }
    }

    private static final class SessionHolder {
        private final long creationTime;
        private final long timeout;
        private final byte[] sessionBytes;
        private final boolean isSingleUse;

        SessionHolder(long creationTime, long timeout, byte[] session, boolean isSingleUse) {
            this.creationTime = creationTime;
            this.timeout = timeout;
            this.sessionBytes = session;
            this.isSingleUse = isSingleUse;
        }

        boolean isValid() {
            return this.isValid(System.currentTimeMillis());
        }

        boolean isValid(long current) {
            return current <= this.creationTime + this.timeout;
        }

        boolean isSingleUse() {
            return this.isSingleUse;
        }

        byte[] sessionBytes() {
            return this.sessionBytes;
        }
    }
}

