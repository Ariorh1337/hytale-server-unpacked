/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded.MpscAtomicUnpaddedArrayQueueProducerLimitField;

abstract class MpscAtomicUnpaddedArrayQueueL2Pad<E>
extends MpscAtomicUnpaddedArrayQueueProducerLimitField<E> {
    MpscAtomicUnpaddedArrayQueueL2Pad(int capacity) {
        super(capacity);
    }
}

