/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3ErrorCode;
import io.netty.handler.codec.http3.Http3Exception;
import io.netty.handler.codec.http3.Http3FrameTypeValidator;

final class Http3ControlStreamFrameTypeValidator
implements Http3FrameTypeValidator {
    static final Http3ControlStreamFrameTypeValidator INSTANCE = new Http3ControlStreamFrameTypeValidator();

    private Http3ControlStreamFrameTypeValidator() {
    }

    @Override
    public void validate(long type, boolean first) throws Http3Exception {
        switch ((int)type) {
            case 0: 
            case 1: 
            case 5: {
                if (first) {
                    throw new Http3Exception(Http3ErrorCode.H3_MISSING_SETTINGS, "Missing settings frame.");
                }
                throw new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Unexpected frame type '" + type + "' received");
            }
        }
    }
}

