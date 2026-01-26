/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http3.CharSequenceMap;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public interface Http3Headers
extends Headers<CharSequence, CharSequence, Http3Headers> {
    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iterator();

    public Iterator<CharSequence> valueIterator(CharSequence var1);

    public Http3Headers method(CharSequence var1);

    public Http3Headers scheme(CharSequence var1);

    public Http3Headers authority(CharSequence var1);

    public Http3Headers path(CharSequence var1);

    public Http3Headers status(CharSequence var1);

    default public Http3Headers protocol(CharSequence value) {
        this.set(PseudoHeaderName.PROTOCOL.value(), value);
        return this;
    }

    @Nullable
    public CharSequence method();

    @Nullable
    public CharSequence scheme();

    @Nullable
    public CharSequence authority();

    @Nullable
    public CharSequence path();

    @Nullable
    public CharSequence status();

    @Nullable
    default public CharSequence protocol() {
        return (CharSequence)this.get(PseudoHeaderName.PROTOCOL.value());
    }

    public boolean contains(CharSequence var1, CharSequence var2, boolean var3);

    public static enum PseudoHeaderName {
        METHOD(":method", true, 1),
        SCHEME(":scheme", true, 2),
        AUTHORITY(":authority", true, 4),
        PATH(":path", true, 8),
        STATUS(":status", false, 16),
        PROTOCOL(":protocol", true, 32);

        private static final char PSEUDO_HEADER_PREFIX = ':';
        private static final byte PSEUDO_HEADER_PREFIX_BYTE = 58;
        private final AsciiString value;
        private final boolean requestOnly;
        private final int flag;
        private static final CharSequenceMap<PseudoHeaderName> PSEUDO_HEADERS;

        private PseudoHeaderName(String value, boolean requestOnly, int flag) {
            this.value = AsciiString.cached(value);
            this.requestOnly = requestOnly;
            this.flag = flag;
        }

        public AsciiString value() {
            return this.value;
        }

        public static boolean hasPseudoHeaderFormat(CharSequence headerName) {
            if (headerName instanceof AsciiString) {
                AsciiString asciiHeaderName = (AsciiString)headerName;
                return asciiHeaderName.length() > 0 && asciiHeaderName.byteAt(0) == 58;
            }
            return headerName.length() > 0 && headerName.charAt(0) == ':';
        }

        public static boolean isPseudoHeader(CharSequence name) {
            return PSEUDO_HEADERS.contains(name);
        }

        @Nullable
        public static PseudoHeaderName getPseudoHeader(CharSequence name) {
            return (PseudoHeaderName)((Object)PSEUDO_HEADERS.get(name));
        }

        public boolean isRequestOnly() {
            return this.requestOnly;
        }

        public int getFlag() {
            return this.flag;
        }

        static {
            PSEUDO_HEADERS = new CharSequenceMap();
            for (PseudoHeaderName pseudoHeader : PseudoHeaderName.values()) {
                PSEUDO_HEADERS.add(pseudoHeader.value(), pseudoHeader);
            }
        }
    }
}

