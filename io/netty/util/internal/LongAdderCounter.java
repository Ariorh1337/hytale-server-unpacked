/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.LongCounter;
import java.util.concurrent.atomic.LongAdder;

@Deprecated
final class LongAdderCounter
extends LongAdder
implements LongCounter {
    LongAdderCounter() {
    }

    @Override
    public long value() {
        return this.longValue();
    }
}

