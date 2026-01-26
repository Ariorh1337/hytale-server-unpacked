/*
 * Decompiled with CFR 0.152.
 */
package org.bson.internal;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.internal.ChildCodecRegistry;

interface CycleDetectingCodecRegistry
extends CodecRegistry {
    public <T> Codec<T> get(ChildCodecRegistry<T> var1);
}

