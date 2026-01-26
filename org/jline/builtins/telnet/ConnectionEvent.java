/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins.telnet;

import org.jline.builtins.telnet.Connection;

public class ConnectionEvent {
    private final Connection source;
    private final Type type;

    public ConnectionEvent(Connection source, Type type) {
        this.type = type;
        this.source = source;
    }

    public Connection getSource() {
        return this.source;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        CONNECTION_IDLE,
        CONNECTION_TIMEDOUT,
        CONNECTION_LOGOUTREQUEST,
        CONNECTION_BREAK,
        CONNECTION_TERMINAL_GEOMETRY_CHANGED;

    }
}

