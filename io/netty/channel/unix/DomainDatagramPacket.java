/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.unix.DomainSocketAddress;

public final class DomainDatagramPacket
extends DefaultAddressedEnvelope<ByteBuf, DomainSocketAddress>
implements ByteBufHolder {
    public DomainDatagramPacket(ByteBuf data, DomainSocketAddress recipient) {
        super(data, recipient);
    }

    public DomainDatagramPacket(ByteBuf data, DomainSocketAddress recipient, DomainSocketAddress sender) {
        super(data, recipient, sender);
    }

    @Override
    public DomainDatagramPacket copy() {
        return this.replace(((ByteBuf)this.content()).copy());
    }

    @Override
    public DomainDatagramPacket duplicate() {
        return this.replace(((ByteBuf)this.content()).duplicate());
    }

    @Override
    public DomainDatagramPacket replace(ByteBuf content) {
        return new DomainDatagramPacket(content, (DomainSocketAddress)this.recipient(), (DomainSocketAddress)this.sender());
    }

    @Override
    public DomainDatagramPacket retain() {
        super.retain();
        return this;
    }

    @Override
    public DomainDatagramPacket retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public DomainDatagramPacket retainedDuplicate() {
        return this.replace(((ByteBuf)this.content()).retainedDuplicate());
    }

    @Override
    public DomainDatagramPacket touch() {
        super.touch();
        return this;
    }

    @Override
    public DomainDatagramPacket touch(Object hint) {
        super.touch(hint);
        return this;
    }
}

