/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins.telnet;

import org.jline.builtins.telnet.ConnectionEvent;

public interface ConnectionListener {
    default public void connectionIdle(ConnectionEvent ce) {
    }

    default public void connectionTimedOut(ConnectionEvent ce) {
    }

    default public void connectionLogoutRequest(ConnectionEvent ce) {
    }

    default public void connectionSentBreak(ConnectionEvent ce) {
    }

    default public void connectionTerminalGeometryChanged(ConnectionEvent ce) {
    }
}

