/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.parser;

import com.google.common.flogger.parser.MessageBuilder;
import com.google.common.flogger.parser.ParseException;

public abstract class MessageParser {
    public static final int MAX_ARG_COUNT = 1000000;

    protected abstract <T> void parseImpl(MessageBuilder<T> var1) throws ParseException;

    public abstract void unescape(StringBuilder var1, String var2, int var3, int var4);
}

