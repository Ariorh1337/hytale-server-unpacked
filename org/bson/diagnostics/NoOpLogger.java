/*
 * Decompiled with CFR 0.152.
 */
package org.bson.diagnostics;

import org.bson.diagnostics.Logger;

class NoOpLogger
implements Logger {
    private final String name;

    NoOpLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

