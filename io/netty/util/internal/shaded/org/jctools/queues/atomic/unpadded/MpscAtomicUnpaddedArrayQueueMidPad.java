/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded.MpscAtomicUnpaddedArrayQueueProducerIndexField;

abstract class MpscAtomicUnpaddedArrayQueueMidPad<E>
extends MpscAtomicUnpaddedArrayQueueProducerIndexField<E> {
    MpscAtomicUnpaddedArrayQueueMidPad(int capacity) {
        super(capacity);
    }
}

