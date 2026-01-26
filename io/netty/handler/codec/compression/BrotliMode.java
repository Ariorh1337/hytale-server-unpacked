/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aayushatharva.brotli4j.encoder.Encoder$Mode
 */
package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.Encoder;

public enum BrotliMode {
    GENERIC,
    TEXT,
    FONT;


    Encoder.Mode adapt() {
        switch (this) {
            case GENERIC: {
                return Encoder.Mode.GENERIC;
            }
            case TEXT: {
                return Encoder.Mode.TEXT;
            }
            case FONT: {
                return Encoder.Mode.FONT;
            }
        }
        throw new IllegalStateException("Unsupported enum value: " + (Object)((Object)this));
    }
}

