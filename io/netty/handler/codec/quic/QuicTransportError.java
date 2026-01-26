/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import java.util.ArrayList;
import java.util.Objects;

public final class QuicTransportError {
    public static final QuicTransportError NO_ERROR = new QuicTransportError(0L, "NO_ERROR");
    public static final QuicTransportError INTERNAL_ERROR = new QuicTransportError(1L, "INTERNAL_ERROR");
    public static final QuicTransportError CONNECTION_REFUSED = new QuicTransportError(2L, "CONNECTION_REFUSED");
    public static final QuicTransportError FLOW_CONTROL_ERROR = new QuicTransportError(3L, "FLOW_CONTROL_ERROR");
    public static final QuicTransportError STREAM_LIMIT_ERROR = new QuicTransportError(4L, "STREAM_LIMIT_ERROR");
    public static final QuicTransportError STREAM_STATE_ERROR = new QuicTransportError(5L, "STREAM_STATE_ERROR");
    public static final QuicTransportError FINAL_SIZE_ERROR = new QuicTransportError(6L, "FINAL_SIZE_ERROR");
    public static final QuicTransportError FRAME_ENCODING_ERROR = new QuicTransportError(7L, "FRAME_ENCODING_ERROR");
    public static final QuicTransportError TRANSPORT_PARAMETER_ERROR = new QuicTransportError(8L, "TRANSPORT_PARAMETER_ERROR");
    public static final QuicTransportError CONNECTION_ID_LIMIT_ERROR = new QuicTransportError(9L, "CONNECTION_ID_LIMIT_ERROR");
    public static final QuicTransportError PROTOCOL_VIOLATION = new QuicTransportError(10L, "PROTOCOL_VIOLATION");
    public static final QuicTransportError INVALID_TOKEN = new QuicTransportError(11L, "INVALID_TOKEN");
    public static final QuicTransportError APPLICATION_ERROR = new QuicTransportError(12L, "APPLICATION_ERROR");
    public static final QuicTransportError CRYPTO_BUFFER_EXCEEDED = new QuicTransportError(13L, "CRYPTO_BUFFER_EXCEEDED");
    public static final QuicTransportError KEY_UPDATE_ERROR = new QuicTransportError(14L, "KEY_UPDATE_ERROR");
    public static final QuicTransportError AEAD_LIMIT_REACHED = new QuicTransportError(15L, "AEAD_LIMIT_REACHED");
    public static final QuicTransportError NO_VIABLE_PATH = new QuicTransportError(16L, "NO_VIABLE_PATH");
    private static final QuicTransportError[] INT_TO_ENUM_MAP;
    private final long code;
    private final String name;

    private QuicTransportError(long code, String name) {
        this.code = code;
        this.name = name;
    }

    public boolean isCryptoError() {
        return this.code >= 256L && this.code <= 511L;
    }

    public String name() {
        return this.name;
    }

    public long code() {
        return this.code;
    }

    public static QuicTransportError valueOf(long value) {
        if (value > 17L) {
            value -= 256L;
        }
        if (value < 0L || value >= (long)INT_TO_ENUM_MAP.length) {
            throw new IllegalArgumentException("Unknown error code value: " + value);
        }
        return INT_TO_ENUM_MAP[(int)value];
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QuicTransportError quicError = (QuicTransportError)o;
        return this.code == quicError.code;
    }

    public int hashCode() {
        return Objects.hash(this.code);
    }

    public String toString() {
        return "QuicTransportError{code=" + this.code + ", name='" + this.name + '\'' + '}';
    }

    static {
        ArrayList<QuicTransportError> errorList = new ArrayList<QuicTransportError>();
        errorList.add(NO_ERROR);
        errorList.add(INTERNAL_ERROR);
        errorList.add(CONNECTION_REFUSED);
        errorList.add(FLOW_CONTROL_ERROR);
        errorList.add(STREAM_LIMIT_ERROR);
        errorList.add(STREAM_STATE_ERROR);
        errorList.add(FINAL_SIZE_ERROR);
        errorList.add(FRAME_ENCODING_ERROR);
        errorList.add(TRANSPORT_PARAMETER_ERROR);
        errorList.add(CONNECTION_ID_LIMIT_ERROR);
        errorList.add(PROTOCOL_VIOLATION);
        errorList.add(INVALID_TOKEN);
        errorList.add(APPLICATION_ERROR);
        errorList.add(CRYPTO_BUFFER_EXCEEDED);
        errorList.add(KEY_UPDATE_ERROR);
        errorList.add(AEAD_LIMIT_REACHED);
        errorList.add(NO_VIABLE_PATH);
        for (int i = 256; i <= 511; ++i) {
            errorList.add(new QuicTransportError(i, "CRYPTO_ERROR"));
        }
        INT_TO_ENUM_MAP = errorList.toArray(new QuicTransportError[0]);
    }
}

