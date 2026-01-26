/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBufAllocator;
import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Enabled(value=false)
@Category(value={"Netty"})
abstract class AbstractAllocatorEvent
extends Event {
    @Label(value="Allocator type")
    @Description(value="The type of allocator this event is for")
    public Class<? extends AbstractByteBufAllocator> allocatorType;

    AbstractAllocatorEvent() {
    }
}

