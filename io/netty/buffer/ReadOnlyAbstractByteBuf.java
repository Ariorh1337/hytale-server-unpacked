/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ReadOnlyByteBuf;

final class ReadOnlyAbstractByteBuf
extends ReadOnlyByteBuf {
    ReadOnlyAbstractByteBuf(AbstractByteBuf buffer) {
        super(buffer);
    }

    @Override
    public AbstractByteBuf unwrap() {
        return (AbstractByteBuf)super.unwrap();
    }

    @Override
    protected byte _getByte(int index) {
        return this.unwrap()._getByte(index);
    }

    @Override
    protected short _getShort(int index) {
        return this.unwrap()._getShort(index);
    }

    @Override
    protected short _getShortLE(int index) {
        return this.unwrap()._getShortLE(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return this.unwrap()._getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return this.unwrap()._getUnsignedMediumLE(index);
    }

    @Override
    protected int _getInt(int index) {
        return this.unwrap()._getInt(index);
    }

    @Override
    protected int _getIntLE(int index) {
        return this.unwrap()._getIntLE(index);
    }

    @Override
    protected long _getLong(int index) {
        return this.unwrap()._getLong(index);
    }

    @Override
    protected long _getLongLE(int index) {
        return this.unwrap()._getLongLE(index);
    }
}

