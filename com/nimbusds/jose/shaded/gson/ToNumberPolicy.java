/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.shaded.gson;

import com.nimbusds.jose.shaded.gson.JsonParseException;
import com.nimbusds.jose.shaded.gson.ToNumberStrategy;
import com.nimbusds.jose.shaded.gson.internal.LazilyParsedNumber;
import com.nimbusds.jose.shaded.gson.internal.NumberLimits;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.math.BigDecimal;

public enum ToNumberPolicy implements ToNumberStrategy
{
    DOUBLE{

        @Override
        public Double readNumber(JsonReader in) throws IOException {
            return in.nextDouble();
        }
    }
    ,
    LAZILY_PARSED_NUMBER{

        @Override
        public Number readNumber(JsonReader in) throws IOException {
            return new LazilyParsedNumber(in.nextString());
        }
    }
    ,
    LONG_OR_DOUBLE{

        @Override
        public Number readNumber(JsonReader in) throws IOException, JsonParseException {
            String value = in.nextString();
            if (value.indexOf(46) >= 0) {
                return this.parseAsDouble(value, in);
            }
            try {
                return Long.parseLong(value);
            }
            catch (NumberFormatException e) {
                return this.parseAsDouble(value, in);
            }
        }

        private Number parseAsDouble(String value, JsonReader in) throws IOException {
            try {
                Double d = Double.valueOf(value);
                if ((d.isInfinite() || d.isNaN()) && !in.isLenient()) {
                    throw new MalformedJsonException("JSON forbids NaN and infinities: " + d + "; at path " + in.getPreviousPath());
                }
                return d;
            }
            catch (NumberFormatException e) {
                throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), e);
            }
        }
    }
    ,
    BIG_DECIMAL{

        @Override
        public BigDecimal readNumber(JsonReader in) throws IOException {
            String value = in.nextString();
            try {
                return NumberLimits.parseBigDecimal(value);
            }
            catch (NumberFormatException e) {
                throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), e);
            }
        }
    };

}

