/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import org.bson.codecs.pojo.CreatorExecutable;
import org.bson.codecs.pojo.InstanceCreator;
import org.bson.codecs.pojo.InstanceCreatorFactory;
import org.bson.codecs.pojo.InstanceCreatorImpl;

final class InstanceCreatorFactoryImpl<T>
implements InstanceCreatorFactory<T> {
    private final CreatorExecutable<T> creatorExecutable;

    InstanceCreatorFactoryImpl(CreatorExecutable<T> creatorExecutable) {
        this.creatorExecutable = creatorExecutable;
    }

    @Override
    public InstanceCreator<T> create() {
        return new InstanceCreatorImpl<T>(this.creatorExecutable);
    }
}

