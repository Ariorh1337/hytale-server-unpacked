/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractAllocatorEvent;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ChunkInfo;
import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.MemoryAddress;

abstract class AbstractChunkEvent
extends AbstractAllocatorEvent {
    @DataAmount
    @Description(value="Size of the chunk")
    public int capacity;
    @Description(value="Is this chunk referencing off-heap memory?")
    public boolean direct;
    @Description(value="The memory address of the off-heap memory, if available")
    @MemoryAddress
    public long address;

    AbstractChunkEvent() {
    }

    public void fill(ChunkInfo chunk, Class<? extends AbstractByteBufAllocator> allocatorType) {
        this.allocatorType = allocatorType;
        this.capacity = chunk.capacity();
        this.direct = chunk.isDirect();
        this.address = chunk.memoryAddress();
    }
}

