/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer.search;

import io.netty.buffer.search.AhoCorasicSearchProcessorFactory;
import io.netty.buffer.search.MultiSearchProcessorFactory;

public abstract class AbstractMultiSearchProcessorFactory
implements MultiSearchProcessorFactory {
    public static AhoCorasicSearchProcessorFactory newAhoCorasicSearchProcessorFactory(byte[] ... needles) {
        return new AhoCorasicSearchProcessorFactory(needles);
    }
}

