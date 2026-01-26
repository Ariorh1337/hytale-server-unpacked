/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3ErrorCode;
import io.netty.handler.codec.http3.Http3Exception;
import io.netty.handler.codec.http3.Http3FrameTypeValidator;

final class Http3PushStreamFrameTypeValidator
implements Http3FrameTypeValidator {
    static final Http3PushStreamFrameTypeValidator INSTANCE = new Http3PushStreamFrameTypeValidator();

    private Http3PushStreamFrameTypeValidator() {
    }

    @Override
    public void validate(long type, boolean first) throws Http3Exception {
        switch ((int)type) {
            case 3: 
            case 4: 
            case 5: 
            case 7: 
            case 13: {
                throw new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Unexpected frame type '" + type + "' received");
            }
        }
    }
}

