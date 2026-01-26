/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.SSL
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslInternalSession;
import io.netty.handler.ssl.OpenSslSessionCache;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.internal.tcnative.SSL;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class OpenSslClientSessionCache
extends OpenSslSessionCache {
    private final Map<HostPort, Set<OpenSslSessionCache.NativeSslSession>> sessions = new HashMap<HostPort, Set<OpenSslSessionCache.NativeSslSession>>();

    OpenSslClientSessionCache(Map<Long, ReferenceCountedOpenSslEngine> engines) {
        super(engines);
    }

    @Override
    protected boolean sessionCreated(OpenSslSessionCache.NativeSslSession session) {
        assert (Thread.holdsLock(this));
        HostPort hostPort = OpenSslClientSessionCache.keyFor(session.getPeerHost(), session.getPeerPort());
        if (hostPort == null) {
            return false;
        }
        Set<OpenSslSessionCache.NativeSslSession> sessionsForHost = this.sessions.get(hostPort);
        if (sessionsForHost == null) {
            sessionsForHost = new HashSet<OpenSslSessionCache.NativeSslSession>(4);
            this.sessions.put(hostPort, sessionsForHost);
        }
        sessionsForHost.add(session);
        return true;
    }

    @Override
    protected void sessionRemoved(OpenSslSessionCache.NativeSslSession session) {
        assert (Thread.holdsLock(this));
        HostPort hostPort = OpenSslClientSessionCache.keyFor(session.getPeerHost(), session.getPeerPort());
        if (hostPort == null) {
            return;
        }
        Set<OpenSslSessionCache.NativeSslSession> sessionsForHost = this.sessions.get(hostPort);
        if (sessionsForHost != null) {
            sessionsForHost.remove(session);
            if (sessionsForHost.isEmpty()) {
                this.sessions.remove(hostPort);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    boolean setSession(long ssl, OpenSslInternalSession session, String host, int port) {
        boolean reused;
        HostPort hostPort = OpenSslClientSessionCache.keyFor(host, port);
        if (hostPort == null) {
            return false;
        }
        OpenSslSessionCache.NativeSslSession nativeSslSession = null;
        boolean singleUsed = false;
        OpenSslClientSessionCache openSslClientSessionCache = this;
        synchronized (openSslClientSessionCache) {
            Set<OpenSslSessionCache.NativeSslSession> sessionsForHost = this.sessions.get(hostPort);
            if (sessionsForHost == null) {
                return false;
            }
            if (sessionsForHost.isEmpty()) {
                this.sessions.remove(hostPort);
                return false;
            }
            ArrayList<OpenSslSessionCache.NativeSslSession> toBeRemoved = null;
            for (OpenSslSessionCache.NativeSslSession sslSession : sessionsForHost) {
                if (sslSession.isValid()) {
                    nativeSslSession = sslSession;
                    break;
                }
                if (toBeRemoved == null) {
                    toBeRemoved = new ArrayList<OpenSslSessionCache.NativeSslSession>(2);
                }
                toBeRemoved.add(sslSession);
            }
            if (toBeRemoved != null) {
                for (OpenSslSessionCache.NativeSslSession sslSession : toBeRemoved) {
                    this.removeSessionWithId(sslSession.sessionId());
                }
            }
            if (nativeSslSession == null) {
                return false;
            }
            reused = SSL.setSession((long)ssl, (long)nativeSslSession.session());
            if (reused) {
                singleUsed = nativeSslSession.shouldBeSingleUse();
            }
        }
        if (reused) {
            if (singleUsed) {
                nativeSslSession.invalidate();
                session.invalidate();
            }
            nativeSslSession.setLastAccessedTime(System.currentTimeMillis());
            session.setSessionDetails(nativeSslSession.getCreationTime(), nativeSslSession.getLastAccessedTime(), nativeSslSession.sessionId(), nativeSslSession.keyValueStorage);
        }
        return reused;
    }

    private static HostPort keyFor(String host, int port) {
        if (host == null && port < 1) {
            return null;
        }
        return new HostPort(host, port);
    }

    @Override
    synchronized void clear() {
        super.clear();
        this.sessions.clear();
    }

    private static final class HostPort {
        private final int hash;
        private final String host;
        private final int port;

        HostPort(String host, int port) {
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
}

