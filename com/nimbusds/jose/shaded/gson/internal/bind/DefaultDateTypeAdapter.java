/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.shaded.gson.internal.bind;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.internal.JavaVersion;
import com.nimbusds.jose.shaded.gson.internal.PreJava9DateFormatProvider;
import com.nimbusds.jose.shaded.gson.internal.bind.TypeAdapters;
import com.nimbusds.jose.shaded.gson.internal.bind.util.ISO8601Utils;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonToken;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public final class DefaultDateTypeAdapter<T extends Date>
extends TypeAdapter<T> {
    private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
    public static final TypeAdapterFactory DEFAULT_STYLE_FACTORY = new TypeAdapterFactory(){

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == Date.class ? new DefaultDateTypeAdapter(DateType.DATE, 2, 2) : null;
        }

        public String toString() {
            return "DefaultDateTypeAdapter#DEFAULT_STYLE_FACTORY";
        }
    };
    private final DateType<T> dateType;
    private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();

    private DefaultDateTypeAdapter(DateType<T> dateType, String datePattern) {
        this.dateType = Objects.requireNonNull(dateType);
        this.dateFormats.add(new SimpleDateFormat(datePattern, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            this.dateFormats.add(new SimpleDateFormat(datePattern));
        }
    }

    private DefaultDateTypeAdapter(DateType<T> dateType, int dateStyle, int timeStyle) {
        this.dateType = Objects.requireNonNull(dateType);
        this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle));
        }
        if (JavaVersion.isJava9OrLater()) {
            this.dateFormats.add(PreJava9DateFormatProvider.getUsDateTimeFormat(dateStyle, timeStyle));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        String dateFormatAsString;
        if (value == null) {
            out.nullValue();
            return;
        }
        DateFormat dateFormat = this.dateFormats.get(0);
        List<DateFormat> list = this.dateFormats;
        synchronized (list) {
            dateFormatAsString = dateFormat.format(value);
        }
        out.value(dateFormatAsString);
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        Date date = this.deserializeToDate(in);
        return this.dateType.deserialize(date);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Date deserializeToDate(JsonReader in) throws IOException {
        String s = in.nextString();
        List<DateFormat> list = this.dateFormats;
        synchronized (list) {
            for (DateFormat dateFormat : this.dateFormats) {
                TimeZone originalTimeZone = dateFormat.getTimeZone();
                try {
                    Date date = dateFormat.parse(s);
                    return date;
                }
                catch (ParseException parseException) {}
                continue;
                finally {
                    dateFormat.setTimeZone(originalTimeZone);
                }
            }
        }
        try {
            return ISO8601Utils.parse(s, new ParsePosition(0));
        }
        catch (ParseException e) {
            throw new JsonSyntaxException("Failed parsing '" + s + "' as Date; at path " + in.getPreviousPath(), e);
        }
    }

    public String toString() {
        DateFormat defaultFormat = this.dateFormats.get(0);
        if (defaultFormat instanceof SimpleDateFormat) {
            return "DefaultDateTypeAdapter(" + ((SimpleDateFormat)defaultFormat).toPattern() + ')';
        }
        return "DefaultDateTypeAdapter(" + defaultFormat.getClass().getSimpleName() + ')';
    }

    public static abstract class DateType<T extends Date> {
        public static final DateType<Date> DATE = new DateType<Date>(Date.class){

            @Override
            protected Date deserialize(Date date) {
                return date;
            }
        };
        private final Class<T> dateClass;

        protected DateType(Class<T> dateClass) {
            this.dateClass = dateClass;
        }

        protected abstract T deserialize(Date var1);

        private TypeAdapterFactory createFactory(DefaultDateTypeAdapter<T> adapter) {
            return TypeAdapters.newFactory(this.dateClass, adapter);
        }

        public final TypeAdapterFactory createAdapterFactory(String datePattern) {
            return this.createFactory(new DefaultDateTypeAdapter(this, datePattern));
        }

        public final TypeAdapterFactory createAdapterFactory(int dateStyle, int timeStyle) {
            return this.createFactory(new DefaultDateTypeAdapter(this, dateStyle, timeStyle));
        }
    }
}

