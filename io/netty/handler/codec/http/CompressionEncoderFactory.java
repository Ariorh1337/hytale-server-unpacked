/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

interface CompressionEncoderFactory {
    public MessageToByteEncoder<ByteBuf> createEncoder();
}

