/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3Exception;

@FunctionalInterface
interface Http3FrameTypeValidator {
    public static final Http3FrameTypeValidator NO_VALIDATION = (type, first) -> {};

    public void validate(long var1, boolean var3) throws Http3Exception;
}

