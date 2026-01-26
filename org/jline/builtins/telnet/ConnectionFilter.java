/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins.telnet;

import java.net.InetAddress;

public interface ConnectionFilter {
    public boolean isAllowed(InetAddress var1);
}

