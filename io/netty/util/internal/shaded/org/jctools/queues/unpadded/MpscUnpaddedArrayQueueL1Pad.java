/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.shaded.org.jctools.queues.unpadded;

import io.netty.util.internal.shaded.org.jctools.queues.unpadded.ConcurrentCircularUnpaddedArrayQueue;

abstract class MpscUnpaddedArrayQueueL1Pad<E>
extends ConcurrentCircularUnpaddedArrayQueue<E> {
    MpscUnpaddedArrayQueueL1Pad(int capacity) {
        super(capacity);
    }
}

