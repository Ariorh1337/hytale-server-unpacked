/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractAllocatorEvent;
import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractByteBufAllocator;
import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.MemoryAddress;

abstract class AbstractBufferEvent
extends AbstractAllocatorEvent {
    @DataAmount
    @Description(value="Configured buffer capacity")
    public int size;
    @DataAmount
    @Description(value="Actual allocated buffer capacity")
    public int maxFastCapacity;
    @DataAmount
    @Description(value="Maximum buffer capacity")
    public int maxCapacity;
    @Description(value="Is this buffer referencing off-heap memory?")
    public boolean direct;
    @Description(value="The memory address of the off-heap memory, if available")
    @MemoryAddress
    public long address;

    AbstractBufferEvent() {
    }

    public void fill(AbstractByteBuf buf, Class<? extends AbstractByteBufAllocator> allocatorType) {
        this.allocatorType = allocatorType;
        this.size = buf.capacity();
        this.maxFastCapacity = buf.maxFastWritableBytes() + buf.writerIndex();
        this.maxCapacity = buf.maxCapacity();
        this.direct = buf.isDirect();
        this.address = buf._memoryAddress();
    }
}

