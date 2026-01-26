/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.backend;

import com.google.common.flogger.parser.MessageParser;
import com.google.common.flogger.util.Checks;

public final class TemplateContext {
    private final MessageParser parser;
    private final String message;

    public TemplateContext(MessageParser parser, String message) {
        this.parser = Checks.checkNotNull(parser, "parser");
        this.message = Checks.checkNotNull(message, "message");
    }

    public MessageParser getParser() {
        return this.parser;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object obj) {
        if (obj instanceof TemplateContext) {
            TemplateContext other = (TemplateContext)obj;
            return this.parser.equals(other.parser) && this.message.equals(other.message);
        }
        return false;
    }

    public int hashCode() {
        return this.parser.hashCode() ^ this.message.hashCode();
    }
}

