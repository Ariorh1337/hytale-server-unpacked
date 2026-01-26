/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

@Deprecated
public class SingleFieldBuilderV3<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
implements AbstractMessage.BuilderParent {
    private AbstractMessage.BuilderParent parent;
    private BType builder;
    private MType message;
    private boolean isClean;

    @Deprecated
    public SingleFieldBuilderV3(MType message, AbstractMessage.BuilderParent parent, boolean isClean) {
        this.message = (AbstractMessage)Internal.checkNotNull(message);
        this.parent = parent;
        this.isClean = isClean;
    }

    @Deprecated
    public void dispose() {
        this.parent = null;
    }

    @Deprecated
    public MType getMessage() {
        if (this.message == null) {
            this.message = (AbstractMessage)this.builder.buildPartial();
        }
        return this.message;
    }

    @Deprecated
    public MType build() {
        this.isClean = true;
        return this.getMessage();
    }

    @Deprecated
    public BType getBuilder() {
        if (this.builder == null) {
            this.builder = (AbstractMessage.Builder)((AbstractMessage)this.message).newBuilderForType(this);
            ((AbstractMessage.Builder)this.builder).mergeFrom((Message)this.message);
            ((AbstractMessage.Builder)this.builder).markClean();
        }
        return this.builder;
    }

    @Deprecated
    public IType getMessageOrBuilder() {
        if (this.builder != null) {
            return (IType)this.builder;
        }
        return (IType)this.message;
    }

    @Deprecated
    @CanIgnoreReturnValue
    public SingleFieldBuilderV3<MType, BType, IType> setMessage(MType message) {
        this.message = (AbstractMessage)Internal.checkNotNull(message);
        if (this.builder != null) {
            ((AbstractMessage.Builder)this.builder).dispose();
            this.builder = null;
        }
        this.onChanged();
        return this;
    }

    @Deprecated
    @CanIgnoreReturnValue
    public SingleFieldBuilderV3<MType, BType, IType> mergeFrom(MType value) {
        if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
            this.message = value;
        } else {
            ((AbstractMessage.Builder)this.getBuilder()).mergeFrom((Message)value);
        }
        this.onChanged();
        return this;
    }

    @Deprecated
    @CanIgnoreReturnValue
    public SingleFieldBuilderV3<MType, BType, IType> clear() {
        this.message = (AbstractMessage)(this.message != null ? this.message.getDefaultInstanceForType() : this.builder.getDefaultInstanceForType());
        if (this.builder != null) {
            ((AbstractMessage.Builder)this.builder).dispose();
            this.builder = null;
        }
        this.onChanged();
        this.isClean = true;
        return this;
    }

    @Deprecated
    private void onChanged() {
        if (this.builder != null) {
            this.message = null;
        }
        if (this.isClean && this.parent != null) {
            this.parent.markDirty();
            this.isClean = false;
        }
    }

    @Override
    @Deprecated
    public void markDirty() {
        this.onChanged();
    }
}

