/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.shaded.org.jctools.queues.unpadded;

import io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueueProducerIndexField;

abstract class MpscUnpaddedArrayQueueMidPad<E>
extends MpscUnpaddedArrayQueueProducerIndexField<E> {
    MpscUnpaddedArrayQueueMidPad(int capacity) {
        super(capacity);
    }
}

