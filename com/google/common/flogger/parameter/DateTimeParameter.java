/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.parameter;

import com.google.common.flogger.backend.FormatOptions;
import com.google.common.flogger.parameter.DateTimeFormat;
import com.google.common.flogger.parameter.Parameter;
import com.google.common.flogger.parameter.ParameterVisitor;

public final class DateTimeParameter
extends Parameter {
    private final DateTimeFormat format;
    private final String formatString;

    public static Parameter of(DateTimeFormat format, FormatOptions options, int index) {
        return new DateTimeParameter(options, index, format);
    }

    private DateTimeParameter(FormatOptions options, int index, DateTimeFormat format) {
        super(options, index);
        this.format = format;
        this.formatString = options.appendPrintfOptions(new StringBuilder("%")).append(options.shouldUpperCase() ? (char)'T' : 't').append(format.getChar()).toString();
    }

    @Override
    protected void accept(ParameterVisitor visitor, Object value) {
        visitor.visitDateTime(value, this.format, this.getFormatOptions());
    }

    @Override
    public String getFormat() {
        return this.formatString;
    }
}

