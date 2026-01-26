/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

class ShellDateTimeConverter
implements Converter<Long> {
    ShellDateTimeConverter() {
    }

    @Override
    public void convert(Long value, StrictJsonWriter writer) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (value >= -59014396800000L && value <= 253399536000000L) {
            writer.writeRaw(String.format("ISODate(\"%s\")", dateFormat.format(new Date(value))));
        } else {
            writer.writeRaw(String.format("new Date(%d)", value));
        }
    }
}

