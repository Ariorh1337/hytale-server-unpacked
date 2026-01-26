/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

public class SingleFieldBuilder<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
implements AbstractMessage.BuilderParent {
    private AbstractMessage.BuilderParent parent;
    private BType builder;
    private MType message;
    private boolean isClean;

    public SingleFieldBuilder(MType message, AbstractMessage.BuilderParent parent, boolean isClean) {
        this.message = (GeneratedMessage)Internal.checkNotNull(message);
        this.parent = parent;
        this.isClean = isClean;
    }

    public void dispose() {
        this.parent = null;
    }

    public MType getMessage() {
        if (this.message == null) {
            this.message = (GeneratedMessage)this.builder.buildPartial();
        }
        return this.message;
    }

    public MType build() {
        this.isClean = true;
        return this.getMessage();
    }

    public BType getBuilder() {
        Object builder = this.builder;
        if (builder == null) {
            builder = (GeneratedMessage.Builder)((AbstractMessage)this.message).newBuilderForType(this);
            this.builder = builder;
            ((AbstractMessage.Builder)builder).mergeFrom((Message)this.message);
            ((GeneratedMessage.Builder)builder).markClean();
        }
        return builder;
    }

    public IType getMessageOrBuilder() {
        if (this.builder != null) {
            return (IType)this.builder;
        }
        return (IType)this.message;
    }

    @CanIgnoreReturnValue
    public SingleFieldBuilder<MType, BType, IType> setMessage(MType message) {
        this.message = (GeneratedMessage)Internal.checkNotNull(message);
        if (this.builder != null) {
            ((GeneratedMessage.Builder)this.builder).dispose();
            this.builder = null;
        }
        this.onChanged();
        return this;
    }

    @CanIgnoreReturnValue
    public SingleFieldBuilder<MType, BType, IType> mergeFrom(MType value) {
        if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
            this.message = value;
        } else {
            ((AbstractMessage.Builder)this.getBuilder()).mergeFrom((Message)value);
        }
        this.onChanged();
        return this;
    }

    @CanIgnoreReturnValue
    public SingleFieldBuilder<MType, BType, IType> clear() {
        this.message = (GeneratedMessage)(this.message != null ? this.message.getDefaultInstanceForType() : this.builder.getDefaultInstanceForType());
        if (this.builder != null) {
            ((GeneratedMessage.Builder)this.builder).dispose();
            this.builder = null;
        }
        this.onChanged();
        this.isClean = true;
        return this;
    }

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
    public void markDirty() {
        this.onChanged();
    }
}

