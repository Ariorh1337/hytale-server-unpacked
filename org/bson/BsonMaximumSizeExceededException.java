/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonSerializationException;

public class BsonMaximumSizeExceededException
extends BsonSerializationException {
    private static final long serialVersionUID = 8725368828269129777L;

    public BsonMaximumSizeExceededException(String message) {
        super(message);
    }
}

