/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.utils.posqueries;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import java.util.Stack;
import java.util.logging.Level;

public class SpatialQueryDebug {
    private final StringBuilder builder = new StringBuilder();
    private String indent = "";
    private Stack<String> scope = new Stack();

    public SpatialQueryDebug() {
        this.appendLine("SPATIAL QUERY DEBUG");
    }

    public SpatialQueryDebug appendLine(String string) {
        HytaleLogger.getLogger().at(Level.INFO).log(this.indent + "| " + string);
        return this;
    }

    public SpatialQueryDebug indent(String scopeReason) {
        HytaleLogger.getLogger().at(Level.INFO).log(this.indent + "\u2b91 " + scopeReason);
        this.indent = this.indent + "  ";
        this.scope.add(scopeReason);
        return this;
    }

    public SpatialQueryDebug unindent() {
        if (this.indent.length() >= 2) {
            this.indent = this.indent.substring(0, this.indent.length() - 2);
        }
        if (!this.scope.isEmpty()) {
            String scopeReason = this.scope.pop();
            HytaleLogger.getLogger().at(Level.INFO).log(this.indent + "\u2b90 (DONE) " + scopeReason);
        }
        return this;
    }

    public String fmt(Vector3d point) {
        return "(" + String.format("%.1f", point.x) + ", " + String.format("%.1f", point.y) + ", " + String.format("%.1f", point.z) + ")";
    }

    public String toString() {
        return this.builder.toString();
    }
}

