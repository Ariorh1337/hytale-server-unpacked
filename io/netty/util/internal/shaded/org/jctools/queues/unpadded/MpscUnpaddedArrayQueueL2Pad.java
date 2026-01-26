/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.shaded.org.jctools.queues.unpadded;

import io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueueProducerLimitField;

abstract class MpscUnpaddedArrayQueueL2Pad<E>
extends MpscUnpaddedArrayQueueProducerLimitField<E> {
    MpscUnpaddedArrayQueueL2Pad(int capacity) {
        super(capacity);
    }
}

